package com.slamracing.ecommerce.model;

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
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(length = 50, nullable = false)
    private String nombre;

    @Column(name = "fecha_creacion", updatable = false)
    @CreatedDate
    private LocalDateTime fechaCreacion;

    @Column(name = "ultima_actualizacion")
    @LastModifiedDate
    private LocalDateTime ultimaActualizacion;
}
