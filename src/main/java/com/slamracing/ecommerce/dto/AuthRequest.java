package com.slamracing.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthRequest(
        @Schema(description = "Correo electrónico del usuario", example = "usuario@example.com")
        String email,
        @Schema(description = "Contraseña del usuario", example = "Contraseña123!")
        String password
) {
}
