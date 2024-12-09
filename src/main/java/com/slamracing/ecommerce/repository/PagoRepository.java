package com.slamracing.ecommerce.repository;

import com.slamracing.ecommerce.model.PagoEntity;
import com.slamracing.ecommerce.model.enums.EstadoTransaccion;
import com.slamracing.ecommerce.model.enums.MetodoPago;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagoRepository extends JpaRepository<PagoEntity, Long> {
    @Query("SELECT p FROM PagoEntity p WHERE " +
            "CAST(p.pedido.pedidoId AS string) LIKE :query OR " +
            "p.idTransaccionServicio LIKE :query OR " +
            "CAST(p.monto AS string) LIKE :query")
    List<PagoEntity> buscarPorCriterio(@Param("query") String query);

    @Query("SELECT p FROM PagoEntity p WHERE p.estadoTransaccion = :estado")
    List<PagoEntity> buscarPorEstadoTransaccion(@Param("estado") EstadoTransaccion estado);

    @Query("SELECT p FROM PagoEntity p WHERE p.metodoPago = :metodo")
    List<PagoEntity> buscarPorMetodoPago(@Param("metodo") MetodoPago metodo);
}
