package com.slamracing.ecommerce.repository;

import com.slamracing.ecommerce.model.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
}
