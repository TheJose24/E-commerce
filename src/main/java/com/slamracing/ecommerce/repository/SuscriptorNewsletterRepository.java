package com.slamracing.ecommerce.repository;

import com.slamracing.ecommerce.model.SuscriptorNewsletterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SuscriptorNewsletterRepository extends JpaRepository<SuscriptorNewsletterEntity, Long> {

    SuscriptorNewsletterEntity findByEmail(String email);

    @Query("SELECT COUNT(s) FROM SuscriptorNewsletterEntity s WHERE s.estado = true")
    Long countTotalSuscriptores();

    @Query("SELECT COUNT(s) FROM SuscriptorNewsletterEntity s WHERE s.fechaSuscripcion >= :inicioMes AND s.estado = true")
    Long countNuevosSuscriptoresMesActual(@Param("inicioMes") LocalDateTime inicioMes);


}
