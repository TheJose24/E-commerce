package com.slamracing.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;


import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PagoTarjetaDTO {

    @NotNull
    @JsonProperty("description")
    private String productDescription;

    @NotNull
    private Integer installments;

    private String issuer_id;

    @NotNull
    private PagadorDTO payer;

    @NotNull
    private String payment_method_id;

    @NotNull
    private String token;

    @NotNull
    private BigDecimal transaction_amount;

    // id del pedido
    private String external_reference;

    private String statement_descriptor;

    private Object additional_info;

}