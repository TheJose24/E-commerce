package com.slamracing.ecommerce.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardDTO {
    private Map<String, Long> pedidosPorMes;
    private Map<String, Long> productosPorCategoria;
    private List<UsuarioStatsDTO> usuariosStats;
}