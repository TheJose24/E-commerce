package com.slamracing.ecommerce.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EmailRequestDTO {
        @Email
        private String email;

        private String asunto;

        private String mensaje;

        private Long numeroSuscriptores;

        private Long numeroSuscriptoresMesActual;

        private LocalDateTime fechaHoraSuscripcion;

}
