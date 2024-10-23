package com.slamracing.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "galeria_imagenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GaleriaImagenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imagen_id")
    private Long imagenId;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @Column(name = "url_imagen", nullable = false)
    private String urlImagen;

    private String descripcion;

    @Column(nullable = false)
    private Integer orden;
}
