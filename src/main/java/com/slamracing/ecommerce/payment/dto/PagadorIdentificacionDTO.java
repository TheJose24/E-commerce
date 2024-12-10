package com.slamracing.ecommerce.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
@Data
@NoArgsConstructor
public class PagadorIdentificacionDTO {
    @NotNull
    private String type;

    @NotNull
    private String number;
}
