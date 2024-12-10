package com.slamracing.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDTO {
    private BigDecimal subtotal;
    private BigDecimal total;
    private List<DetallePedidoDTO> detalles;
}