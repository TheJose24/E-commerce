package com.slamracing.ecommerce.service;

import com.slamracing.ecommerce.dto.AuthRequest;
import com.slamracing.ecommerce.dto.RegisterRequest;
import com.slamracing.ecommerce.dto.TokenResponse;
import com.slamracing.ecommerce.exception.AuthenticationFailedException;
import com.slamracing.ecommerce.exception.TokenProcessingException;
import com.slamracing.ecommerce.exception.UserAlreadyExistsException;
import com.slamracing.ecommerce.model.TokenEntity;
import com.slamracing.ecommerce.model.UsuarioEntity;
import com.slamracing.ecommerce.model.enums.EstadoUsuario;
import com.slamracing.ecommerce.model.enums.Role;
import com.slamracing.ecommerce.repository.TokenRepository;
import com.slamracing.ecommerce.repository.UsuarioRepository;
import com.slamracing.ecommerce.security.service.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AuthService {
    private static final String BEARER_PREFIX = "Bearer ";

    private final UsuarioRepository usuarioRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public TokenResponse register(@Valid final RegisterRequest request) {
        log.debug("Iniciando registro de usuario: {}", request.getEmail());

        validateNewUser(request.getEmail());

        final UsuarioEntity usuario = createUserEntity(request);
        final UsuarioEntity usuarioGuardado = usuarioRepository.save(usuario);

        final TokenResponse tokenResponse = generateTokenResponse(usuarioGuardado);

        usuarioGuardado.setTokenRecuperacion(tokenResponse.getRefreshToken());
        usuarioRepository.save(usuarioGuardado);

        return tokenResponse;
    }

    private void validateNewUser(String email) {
        usuarioRepository.findByEmail(email).ifPresent(user -> {
            log.warn("Intento de registro con email existente: {}", email);
            throw new UserAlreadyExistsException("El email ya está registrado", HttpStatus.CONFLICT);
        });
    }

    private UsuarioEntity createUserEntity(RegisterRequest request) {
        return UsuarioEntity.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .estadoUsuario(EstadoUsuario.ACTIVO)
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .role(Role.ADMIN)
                .tokens(List.of())
                .direcciones(Set.of())
                .build();
    }

    @Transactional
    public TokenResponse authenticate(@Valid AuthRequest request) {
        log.debug("Iniciando autenticación para usuario: {}", request.email());

        try {
            authenticateUser(request);
            final UsuarioEntity usuario = getUserByEmail(request.email());
            return generateTokenResponse(usuario);
        } catch (BadCredentialsException e) {
            log.warn("Intento de autenticación fallido para usuario: {}", request.email());
            throw new AuthenticationFailedException("Credenciales inválidas", HttpStatus.BAD_REQUEST);
        }
    }

    private void authenticateUser(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
    }

    private UsuarioEntity getUserByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationFailedException("Usuario no encontrado", HttpStatus.NOT_FOUND));
    }

    private TokenResponse generateTokenResponse(UsuarioEntity usuario) {
        // Verificar si existe un token válido
        List<TokenEntity> validTokens = tokenRepository.findAllValidTokenByUser(usuario.getUsuarioId());
        if (!validTokens.isEmpty()) {
            TokenEntity validToken = validTokens.get(0);
            // Verificar si el token aún no está cerca de expirar
            if (!jwtService.isTokenNearExpiration(validToken.getToken())) {
                log.debug("Token válido encontrado para usuario: {}", usuario.getEmail());
                return TokenResponse.builder()
                        .accessToken(validToken.getToken())
                        .refreshToken(usuario.getTokenRecuperacion())
                        .build();
            }
        }

        // Si no hay token válido o está próximo a expirar, generar nuevos tokens
        final String accessToken = jwtService.generateToken(usuario);
        final String refreshToken = jwtService.generateRefreshToken(usuario);

        // Actualizar el token de recuperación en la entidad usuario
        usuario.setTokenRecuperacion(refreshToken);
        usuarioRepository.save(usuario);

        // Revocar tokens antiguos y guardar el nuevo
        revokeAllUserTokens(usuario);
        saveUserToken(usuario, accessToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    private void saveUserToken(UsuarioEntity usuario, String jwtToken) {
        try {
            final TokenEntity token = TokenEntity.builder()
                    .usuario(usuario)
                    .token(jwtToken)
                    .tokenType(TokenEntity.TokenType.BEARER)
                    .isExpired(false)
                    .isRevoked(false)
                    .build();
            tokenRepository.save(token);
        } catch (Exception e) {
            log.error("Error al guardar el token para usuario: {}", usuario.getEmail(), e);
            throw new TokenProcessingException("Error al guardar el token", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void revokeAllUserTokens(UsuarioEntity usuario) {
        try {
            final List<TokenEntity> validUserTokens = tokenRepository.findAllValidTokenByUser(usuario.getUsuarioId());
            if (validUserTokens.isEmpty()) {
                return;
            }

            validUserTokens.forEach(this::revokeToken);
            tokenRepository.saveAll(validUserTokens);

            log.debug("Tokens revocados para usuario: {}", usuario.getEmail());
        } catch (Exception e) {
            log.error("Error al revocar tokens para usuario: {}", usuario.getEmail(), e);
            throw new TokenProcessingException("Error al revocar tokens", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void revokeToken(TokenEntity token) {
        token.setIsExpired(true);
        token.setIsRevoked(true);
    }

    @Transactional
    public TokenResponse refreshToken(@NotNull final String authorization) {
        log.debug("Iniciando refresh de token");

        try {
            final String refreshToken = extractTokenFromHeader(authorization);
            final String userEmail = jwtService.extractUsername(refreshToken);

            final UsuarioEntity usuario = validateRefreshToken(refreshToken, userEmail);

            // Generar nuevos tokens
            String newAccessToken = jwtService.generateToken(usuario);
            String newRefreshToken = jwtService.generateRefreshToken(usuario);

            // Actualizar tokens en base de datos
            revokeAllUserTokens(usuario);
            saveUserToken(usuario, newAccessToken);

            // Actualizar refresh token en usuario
            usuario.setTokenRecuperacion(newRefreshToken);
            usuarioRepository.save(usuario);

            log.debug("Token refrescado exitosamente para usuario: {}", userEmail);
            return new TokenResponse(newAccessToken, newRefreshToken);
        } catch (Exception e) {
            log.error("Error al refrescar el token", e);
            throw new TokenProcessingException("Error al refrescar el token", e, HttpStatus.UNAUTHORIZED);
        }
    }

    private String extractTokenFromHeader(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            log.warn("Header de autorización inválido recibido");
            throw new TokenProcessingException("Header de autorización inválido", HttpStatus.BAD_REQUEST);
        }
        return authorization.substring(BEARER_PREFIX.length());
    }

    private UsuarioEntity validateRefreshToken(String refreshToken, String userEmail) {
        if (userEmail == null) {
            throw new TokenProcessingException("Token inválido: no se pudo extraer el email", HttpStatus.BAD_REQUEST);
        }

        final UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AuthenticationFailedException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (!jwtService.isRefreshTokenValid(refreshToken, usuario)) {
            log.warn("Intento de refresh con token inválido para usuario: {}", userEmail);
            throw new TokenProcessingException("Token de refresh no válido o expirado", HttpStatus.FORBIDDEN);
        }

        return usuario;
    }
}