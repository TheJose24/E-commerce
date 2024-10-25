package com.slamracing.ecommerce.service;

import com.slamracing.ecommerce.model.SuscriptorNewsletterEntity;
import com.slamracing.ecommerce.repository.SuscriptorNewsletterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@AllArgsConstructor
public class SuscripcionNoticiasService {

    private final SuscriptorNewsletterRepository suscriptorNewsletterRepository;

    @Transactional
    public LocalDateTime suscribir(String email) {
        SuscriptorNewsletterEntity suscriptor = new SuscriptorNewsletterEntity();
        suscriptor.setEmail(email);
        try {
            SuscriptorNewsletterEntity fechaHora = suscriptorNewsletterRepository.save(suscriptor);
            suscriptor.setEstado(true);
            return fechaHora.getFechaSuscripcion();
        } catch (Exception e) {
            throw new RuntimeException("Error al suscribir al usuario", e);
        }
    }

    @Transactional
    public void desuscribir(String suscriptor) {
        try {
            SuscriptorNewsletterEntity usuario = suscriptorNewsletterRepository.findByEmail(suscriptor);
            usuario.setEstado(false);

            suscriptorNewsletterRepository.save(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la suscripción del usuario", e);
        }
    }

    public List<SuscriptorNewsletterEntity> obtenerSuscriptores() {
        return suscriptorNewsletterRepository.findAll();
    }

    public SuscriptorNewsletterEntity obtenerSuscriptorPorEmail(String email) {
        return suscriptorNewsletterRepository.findByEmail(email);
    }

    public Long obtenerTotalSuscriptores() {
        return suscriptorNewsletterRepository.countTotalSuscriptores();
    }

    public Long obtenerNuevosSuscriptoresMesActual() {
        LocalDateTime inicioMes = YearMonth.now().atDay(1).atStartOfDay();
        return suscriptorNewsletterRepository.countNuevosSuscriptoresMesActual(inicioMes);
    }

}
