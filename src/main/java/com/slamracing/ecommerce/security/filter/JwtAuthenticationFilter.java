package com.slamracing.ecommerce.security.filter;

import com.slamracing.ecommerce.dto.TokenResponse;
import com.slamracing.ecommerce.model.UsuarioEntity;
import com.slamracing.ecommerce.repository.UsuarioRepository;
import com.slamracing.ecommerce.security.service.JwtService;
import com.slamracing.ecommerce.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository userRepository;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getServletPath().contains("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                Optional<UsuarioEntity> user = userRepository.findByEmail(userEmail);

                if (user.isPresent()) {
                    if (jwtService.isTokenExpired(jwt)) {
                        // Intentar refrescar el token
                        try {
                            TokenResponse newTokens = authService.refreshToken(authHeader);
                            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newTokens.getAccessToken());
                            jwt = newTokens.getAccessToken();
                        } catch (Exception e) {
                            log.error("Error refreshing token", e);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }
                    }

                    if (jwtService.isTokenValid(jwt, user.get())) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error processing JWT token", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}

