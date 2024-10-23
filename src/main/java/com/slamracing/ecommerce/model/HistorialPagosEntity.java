package com.slamracing.ecommerce.model;

import com.slamracing.ecommerce.model.enums.EstadoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class HistorialPagosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historial_pago_id")
    private Long historialPagoId;

    @ManyToOne
    @JoinColumn(name = "pago_id", nullable = false)
    private PagoEntity pagoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false)
    private EstadoPago estadoPago;

    @Column(name = "fecha_cambio")
    @LastModifiedDate
    private LocalDateTime fechaCambio;

    @Column(columnDefinition = "TEXT")
    private String comentario;
}
