package com.slamracing.ecommerce.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class CarritoRequest {
    @NotNull(message = "El ID del producto no puede ser nulo")
    private Long productoId;
    
    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 1, message = "La cantidad mínima debe ser 1")
    private Integer cantidad;
}