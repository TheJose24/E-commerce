package com.slamracing.ecommerce.dto;

import com.slamracing.ecommerce.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UsuarioResponse(
        @Schema(description = "Identificador único del usuario")
        Long usuarioId,

        @Schema(description = "Nombre del usuario")
        String nombre,

        @Schema(description = "Correo electrónico del usuario")
        String email,

        @Schema(description = "Rol del usuario en el sistema")
        Role role,

        @Schema(description = "Fecha de registro del usuario")
        LocalDateTime fechaRegistro,

        @Schema(description = "Última actualización del usuario")
        LocalDateTime ultimaActualizacion
) {}
