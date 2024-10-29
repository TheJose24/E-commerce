package com.slamracing.ecommerce.repository;

import com.slamracing.ecommerce.model.ResenaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResenaRepository extends JpaRepository<ResenaEntity, Long> {
    List<ResenaEntity> findByProductoProductoId(Long productoId);
}
