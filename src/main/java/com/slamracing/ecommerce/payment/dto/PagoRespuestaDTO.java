package com.slamracing.ecommerce.payment.dto;

import com.mercadopago.net.MPResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class PagoRespuestaDTO {
    private Long id;

    private OffsetDateTime date_created;

    private OffsetDateTime date_approved;

    private String operation_type;

    private String payment_method_id;

    private String payment_type_id;

    private String status;

    private String detail;

    private String currency_id;

    private Object payer;

    private BigDecimal transaction_amount;

    //Detalles de la transacción (net_received_amount, total_paid_amount)
    private Object transaction_details;

    //private List<?> fee_details;

    private Boolean captured;

    private String statement_descriptor;

    private int installments;

}