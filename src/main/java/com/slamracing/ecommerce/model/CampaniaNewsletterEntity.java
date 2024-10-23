package com.slamracing.ecommerce.model;

import com.slamracing.ecommerce.model.enums.EstadoCampania;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "campanias_newsletter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CampaniaNewsletterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campania_id")
    private Long campaniaId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String asunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCampania estado;

    @ManyToOne
    @JoinColumn(name = "creado_por")
    private UsuarioEntity creadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime fechaCreacion;
}
