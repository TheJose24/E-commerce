package com.slamracing.ecommerce.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreferenciaRequestDTO {

    // Informacion de contacto
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    // Informacion de envio
    private String departamento;
    private String provincia;
    private String distrito;
    private String direccion;
    private String referencia;

    private String external_reference;

    private List<ItemDTO> items;
}
