package com.slamracing.ecommerce.repository;

import com.slamracing.ecommerce.model.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    Optional <ProductoEntity> findBySlug(String slug);
    List<ProductoEntity> findByNombre(String nombre);
}
