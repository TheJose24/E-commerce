package com.slamracing.ecommerce.security.service;

import com.slamracing.ecommerce.model.TokenEntity;
import com.slamracing.ecommerce.model.UsuarioEntity;
import com.slamracing.ecommerce.repository.TokenRepository;
import com.slamracing.ecommerce.repository.UsuarioRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionManager {
    private final TokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    private String currentToken;
    private Long currentUserId;
    @Getter
    private String currentRole;

    @Scheduled(fixedRate = 300000) // 5 minutos
    public void scheduleTokenRefresh() {
        if (currentUserId != null && shouldRefreshToken()) {
            refreshToken();
        }
    }

    public void initializeSession(String email) {
        try {
            Optional<UsuarioEntity> usuario = usuarioRepository.findByEmail(email);
            if (usuario.isPresent()) {
                currentUserId = usuario.get().getUsuarioId();
                updateCurrentToken();
                currentRole = usuario.get().getRole().name();
            }
        } catch (Exception e) {
            log.error("Error inicializando sesión: {}", e.getMessage());
        }
    }

    public boolean hasRole(String role) {
        return role.equals(currentRole);
    }

    public boolean isValidToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }
            return token.equals(currentToken) && !jwtService.isTokenExpired(token);
        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            return false;
        }
    }


    public String getCurrentToken() {
        try {
            if (shouldRefreshToken()) {
                refreshToken();
            }
            return currentToken;
        } catch (Exception e) {
            log.error("Error obteniendo token actual: {}", e.getMessage());
            return null;
        }
    }

    private boolean shouldRefreshToken() {
        if (currentToken == null) return true;

        try {
            return jwtService.isTokenNearExpiration(currentToken);
        } catch (Exception e) {
            log.error("Error verificando expiración del token: {}", e.getMessage());
            return true;
        }
    }

    private synchronized void refreshToken() {
        try {
            if (currentUserId != null) {
                Optional<UsuarioEntity> usuario = usuarioRepository.findById(currentUserId);
                if (usuario.isPresent()) {
                    String newToken = jwtService.generateToken(usuario.get());
                    saveNewToken(usuario.get(), newToken);
                    currentToken = newToken;
                }
            }
        } catch (Exception e) {
            log.error("Error refrescando token: {}", e.getMessage());
        }
    }

    private void saveNewToken(UsuarioEntity usuario, String newToken) {
        try {
            TokenEntity tokenEntity = TokenEntity.builder()
                    .token(newToken)
                    .usuario(usuario)
                    .isExpired(false)
                    .isRevoked(false)
                    .tokenType(TokenEntity.TokenType.BEARER)
                    .build();

            tokenRepository.save(tokenEntity);
        } catch (Exception e) {
            log.error("Error guardando nuevo token: {}", e.getMessage());
        }
    }

    private void updateCurrentToken() {
        Optional<TokenEntity> validToken = tokenRepository.findValidTokenByUsuario(currentUserId);
        validToken.ifPresent(token -> currentToken = token.getToken());
    }

    public void clearSession() {
        // Limpiar datos de sesión
        currentToken = null;
        currentRole = null;
        currentUserId = null;
    }

}