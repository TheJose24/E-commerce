package com.slamracing.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "galeria_imagenes")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GaleriaImagenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imagen_id")
    private Long imagenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @Column(name = "url_imagen", nullable = false)
    private String urlImagen;

    @Column(nullable = false)
    private Integer orden;

    @Override
    public String toString() {
        return "GaleriaImagenEntity{" +
                "imagenId=" + imagenId +
                ", urlImagen='" + urlImagen + '\'' +
                '}';
    }
}
