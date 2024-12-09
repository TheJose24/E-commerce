package com.slamracing.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
        @NotBlank(message = "El nombre es obligatorio")
        @Schema(description = "Nombre del usuario", example = "Juan Pérez")
        String nombre;

        @NotBlank(message = "La contraseña es obligatoria")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$",
                message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial")
        @Schema(description = "Contraseña del usuario", example = "Contraseña123!")
        String contrasena;

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El formato del correo electrónico no es válido")
        @Schema(description = "Correo electrónico del usuario", example = "juan@gmail.com")
        String email;
}
