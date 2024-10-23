package com.slamracing.ecommerce.repository;

import com.slamracing.ecommerce.model.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
}
