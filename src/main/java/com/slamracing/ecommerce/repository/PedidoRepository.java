package com.slamracing.ecommerce.repository;

import com.slamracing.ecommerce.model.PedidoEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {
    @Query("SELECT p FROM PedidoEntity p WHERE " +
           "LOWER(p.usuario.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "CAST(p.pedidoId AS string) LIKE CONCAT('%', :query, '%')")
    List<PedidoEntity> buscarPorCriterio(@Param("query") String query);
}
