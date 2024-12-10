package com.slamracing.ecommerce.payment.dto;

import org.antlr.v4.runtime.misc.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PagadorDTO {
    @NotNull
    private String email;

    @NotNull
    private PagadorIdentificacionDTO identification;
}