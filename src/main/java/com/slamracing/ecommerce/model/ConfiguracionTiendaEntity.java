package com.slamracing.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.security.Timestamp;

@Entity
@Table(name = "configuracion_tienda")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ConfiguracionTiendaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "configuracion_id")
    private Long configuracionId;

    @Column(name = "nombre_tienda", nullable = false, length = 100)
    private String nombreTienda;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "email_contacto", nullable = false, length = 100)
    private String emailContacto;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    @Column(length = 100)
    private String direccion;

    @Column(length = 10)
    private String moneda = "PEN";

    @Column(name = "iva_porcentaje", nullable = false)
    private BigDecimal ivaPorcentaje = new BigDecimal("18.00");

    @ManyToOne
    @JoinColumn(name = "actualizado_por")
    private UsuarioEntity actualizadoPor;

    @Column(name = "ultima_actualizacion")
    @LastModifiedDate
    private Timestamp ultimaActualizacion;
}
