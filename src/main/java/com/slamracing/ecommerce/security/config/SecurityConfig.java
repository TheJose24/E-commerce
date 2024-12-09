package com.slamracing.ecommerce.security.config;

import com.slamracing.ecommerce.model.TokenEntity;
import com.slamracing.ecommerce.repository.TokenRepository;
import com.slamracing.ecommerce.repository.UsuarioRepository;
import com.slamracing.ecommerce.security.filter.JwtAuthenticationFilter;
import com.slamracing.ecommerce.security.service.JwtService;
import com.slamracing.ecommerce.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider,
            AuthService authService) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/images/**", "/css/**", "/js/**", "/productoImages/**").permitAll()
                        .requestMatchers("/login", "/registro", "/cambiar_idioma").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/v1/carrito/**").permitAll()
                        .requestMatchers("/api/email/**").permitAll()
                        .requestMatchers("/", "/productos", "/contacto", "formularioContacto","/sobre_nosotros", "/soporte").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/401").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter(authService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthService authService) {
        return new JwtAuthenticationFilter(
                jwtService,
                userDetailsService,
                userRepository,
                authService
        );
    }
}
