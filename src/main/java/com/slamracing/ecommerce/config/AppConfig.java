package com.slamracing.ecommerce.config;

import com.slamracing.ecommerce.model.UsuarioEntity;
import com.slamracing.ecommerce.repository.UsuarioRepository;
import com.slamracing.ecommerce.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;

@Slf4j
@Validated
@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private static final String USER_NOT_FOUND_MESSAGE = "No se encontró usuario con email: %s";
    private static final int BCRYPT_STRENGTH = 12;

    private final UsuarioRepository usuarioRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            validateUsername(username);
            try {
                UsuarioEntity usuario = findUserByEmail(username);
                return createUserDetails(usuario);
            } catch (UsernameNotFoundException e) {
                log.warn("Intento de acceso con usuario no existente: {}", username);
                throw e;
            } catch (Exception e) {
                log.error("Error inesperado al cargar usuario: {}", username, e);
                throw new AuthenticationServiceException("Error durante la autenticación", e);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            log.warn("Intento de autenticación con email vacío");
            throw new BadCredentialsException("El email no puede estar vacío");
        }
    }

    private UsuarioEntity findUserByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(USER_NOT_FOUND_MESSAGE, email)
                ));
    }

    private CustomUserDetails createUserDetails(UsuarioEntity usuario) {
        return CustomUserDetails.builder()
                .id(usuario.getUsuarioId())
                .username(usuario.getEmail())
                .password(usuario.getContrasena())
                .nombre(usuario.getNombre())
                .authorities(Collections.singleton(usuario.getRole().toGrantedAuthority()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }



    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setHideUserNotFoundExceptions(true);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
