package com.slamracing.ecommerce.payment.dto;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ItemDTO {
    @NotNull
    private String id;
    @NotNull
    private String title;
    private String description;
    private String picture_url;
    private String category_id;
    @NotNull
    private int quantity;
    private String currency_id;
    @NotNull
    private BigDecimal unit_price;
}