package com.slamracing.ecommerce.security.service;

import com.slamracing.ecommerce.model.UsuarioEntity;
import com.slamracing.ecommerce.model.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Value("${application.security.jwt.issuer}")
    private String issuer;

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String REFRESH_TOKEN_TYPE = "refresh";
    private static final String USER_ID_CLAIM = "userId";
    private static final String NAME_CLAIM = "name";
    private static final String ROLE_CLAIM = "role";
    private static final long TOKEN_REFRESH_MARGIN_MINUTES = 5;

    @PostConstruct
    public void validateConfiguration() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("La clave secreta JWT no está configurada");
        }
        if (jwtExpiration <= 0 || refreshExpiration <= 0) {
            throw new IllegalStateException("Los tiempos de expiración JWT deben ser positivos");
        }
    }

    public boolean isTokenNearExpiration(String token) {
        Date expiration = extractExpiration(token);
        Date now = new Date();

        // Calcula si el token expirará en los próximos 5 minutos
        long minutesUntilExpiration = (expiration.getTime() - now.getTime()) / (60 * 1000);
        return minutesUntilExpiration <= TOKEN_REFRESH_MARGIN_MINUTES;
    }

    public String extractUsername(String token) {
        validateTokenNotEmpty(token);
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException e) {
            log.error("Token expirado al extraer username", e);
            throw new JwtException("El token ha expirado");
        } catch (Exception e) {
            log.error("Error al extraer username del token", e);
            throw new JwtException("Error al extraer el username: " + e.getMessage());
        }
    }

    private void validateTokenNotEmpty(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.warn("Intento de validar token nulo o vacío");
            throw new IllegalArgumentException("El token no puede ser nulo o vacío");
        }
    }

    public String generateToken(final UsuarioEntity user) {
        return generateToken(Collections.emptyMap(), user);
    }

    public String generateToken(Map<String, Object> extraClaims, UsuarioEntity user) {
        validateUser(user);
        try {
            return buildToken(extraClaims, user, jwtExpiration);
        } catch (Exception e) {
            log.error("Error al generar token para usuario: {}", user.getEmail(), e);
            throw new JwtException("Error al generar token: " + e.getMessage());
        }
    }

    public boolean isTokenValid(String token, UsuarioEntity user) {
        if (token == null || user == null) {
            log.warn("Token o usuario nulo en validación");
            return false;
        }

        try {
            final String username = extractUsername(token);
            final String tokenType = extractClaim(token, claims ->
                    claims.get(TOKEN_TYPE_CLAIM, String.class));

            if (REFRESH_TOKEN_TYPE.equals(tokenType)) {
                log.warn("Intento de usar refresh token como token de acceso");
                return false;
            }

            boolean isValid = username.equals(user.getEmail()) &&
                    !isTokenExpired(token) &&
                    validateIssuer(token) &&
                    validateRole(token, user.getRole());

            if (!isValid) {
                log.warn("Token inválido para usuario: {}", user.getEmail());
            }

            return isValid;
        } catch (Exception e) {
            log.error("Error al validar token", e);
            return false;
        }
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtException("El token ha expirado");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("Token JWT no soportado");
        } catch (MalformedJwtException e) {
            throw new JwtException("Token JWT malformado");
        } catch (SignatureException e) {
            throw new JwtException("La firma del token no es válida");
        } catch (Exception e) {
            throw new JwtException("Error al procesar el token: " + e.getMessage());
        }
    }

    public String generateRefreshToken(final UsuarioEntity user) {
        validateUser(user);
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", REFRESH_TOKEN_TYPE);
        return buildToken(claims, user, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UsuarioEntity user, long expiration) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expiration);

            return Jwts.builder()
                    .claims(extraClaims)
                    .claim(USER_ID_CLAIM, user.getUsuarioId())
                    .claim(NAME_CLAIM, user.getNombre())
                    .claim(ROLE_CLAIM, user.getRole().name())
                    .subject(user.getEmail())
                    .issuer(issuer)
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSignInKey())
                    .compact();
        } catch (Exception e) {
            throw new JwtException("Error al construir el token: " + e.getMessage());
        }
    }

    private void validateUser(UsuarioEntity user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email del usuario no puede ser nulo o vacío");
        }
        if (user.getNombre() == null || user.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario no puede ser nulo o vacío");
        }
        if (user.getUsuarioId() == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("El rol del usuario no puede ser nulo");
        }
    }

    private boolean validateRole(String token, Role userRole) {
        try {
            String tokenRole = extractClaim(token, claims ->
                    claims.get("role", String.class));
            return userRole.name().equals(tokenRole);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token, UsuarioEntity user) {
        try {
            if (token == null || user == null) {
                return false;
            }
            final String username = extractUsername(token);
            final String tokenType = extractClaim(token, claims ->
                    claims.get("type", String.class));

            return REFRESH_TOKEN_TYPE.equals(tokenType) &&
                    username.equals(user.getEmail()) &&
                    !isTokenExpired(token) &&
                    validateIssuer(token) &&
                    validateRole(token, user.getRole());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateIssuer(String token) {
        String tokenIssuer = extractClaim(token, Claims::getIssuer);
        return issuer.equals(tokenIssuer);
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignInKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new JwtException("Error al generar la clave de firma: " + e.getMessage());
        }
    }

    public String extractRole(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get(ROLE_CLAIM, String.class);
        } catch (Exception e) {
            log.error("Error extrayendo rol del token: {}", e.getMessage());
            return null;
        }
    }

    public Long extractUserId(String token) {
        try {
            return extractClaim(token, claims -> claims.get(USER_ID_CLAIM, Long.class));
        } catch (Exception e) {
            throw new JwtException("Error al extraer userId del token: " + e.getMessage());
        }
    }
}