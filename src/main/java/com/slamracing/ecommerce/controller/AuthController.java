package com.slamracing.ecommerce.controller;

import com.slamracing.ecommerce.dto.AuthRequest;
import com.slamracing.ecommerce.dto.RegisterRequest;
import com.slamracing.ecommerce.dto.TokenResponse;
import com.slamracing.ecommerce.security.service.SessionManager;
import com.slamracing.ecommerce.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para la autenticación de usuarios y gestión de tokens")
public class AuthController {

    private final AuthService service;
    private final SessionManager sessionManager;

    @Operation(summary = "Registro de Usuario", description = "Registra un nuevo usuario y genera un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro exitoso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta - datos de registro inválidos",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(
            @Parameter(description = "Datos de registro", required = true)
            @RequestBody RegisterRequest request) {
        TokenResponse response = service.register(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Inicio de Sesión", description = "Autentica un usuario y genera un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado - credenciales inválidas",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticate(
            @RequestBody AuthRequest request) {
        TokenResponse response = service.authenticate(request);
        sessionManager.initializeSession(request.email());
        String role = sessionManager.getCurrentRole();

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("access_token", response.getAccessToken());
        responseMap.put("role", role);

        return ResponseEntity.ok(responseMap);
    }

    @Operation(summary = "Refrescar Token", description = "Genera un nuevo token de acceso JWT usando un token de actualización")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "403", description = "Prohibido - token de actualización inválido o expirado",
                    content = @Content)
    })
    @PostMapping("/refresh-token")
    public TokenResponse refreshToken(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token de autorización en formato Bearer",
                    required = true,
                    schema = @Schema(type = "string", format = "jwt"),
                    example = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiSnVhbiIsInN1YiI6Imp1YW5AZ21haWwuY29tIiwiaWF0IjoxNzMxMTkwNzQ5LCJleHAiOjE3MzEyNzcxNDl9.HTbv4ofPcMUEikWWmCfJf-S4ex4ds6CsM-gk45e7oAo"
            )
            @RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication
    ) {
        return service.refreshToken(authentication);
    }

    @PostMapping("/save-token")
    public ResponseEntity<Void> saveToken(@RequestBody Map<String, String> tokenMap, HttpSession session) {
        session.setAttribute("jwt_token", tokenMap.get("token"));
        return ResponseEntity.ok().build();
    }
}