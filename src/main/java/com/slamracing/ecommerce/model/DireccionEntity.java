package com.slamracing.ecommerce.model;

import com.slamracing.ecommerce.model.enums.TipoDireccion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "direcciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class DireccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "direccion_id")
    private Long direccionId;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    private String direccion;

    private String ciudad;

    private String estado;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    private String telefono;

    @Enumerated(EnumType.STRING)
    private TipoDireccion tipo;

    @Column(name = "fecha_creacion", updatable = false)
    @CreatedDate
    private LocalDateTime fechaCreacion;

    @Column(name = "ultima_actualizacion")
    @LastModifiedDate
    private LocalDateTime ultimaActualizacion;

}
