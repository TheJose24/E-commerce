package com.slamracing.ecommerce.repository;

import com.slamracing.ecommerce.model.UsuarioRolEntity;
import com.slamracing.ecommerce.model.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRolEntity, UsuarioRolId> {

}
