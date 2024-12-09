package com.slamracing.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.slamracing.ecommerce.model.UsuarioEntity;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email);
    List<UsuarioEntity> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(String nombre, String email);
}