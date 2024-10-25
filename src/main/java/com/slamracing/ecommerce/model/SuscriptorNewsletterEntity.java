package com.slamracing.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "suscriptores_newsletter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SuscriptorNewsletterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suscriptor_id")
    private Long suscriptorId;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "fecha_suscripcion", updatable = false)
    @CreatedDate
    private LocalDateTime fechaSuscripcion;

    @Column(name = "fecha_modificacion")
    @LastModifiedDate
    private LocalDateTime fechaModificacion;

    @Column(nullable = false)
    private Boolean estado = true;
}
