package com.slamracing.ecommerce.controller;

import com.slamracing.ecommerce.dto.EmailRequestDTO;
import com.slamracing.ecommerce.service.EmailServiceNewsletterImplement;
import com.slamracing.ecommerce.service.SuscripcionNoticiasService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/api/email")
@AllArgsConstructor
public class EmailController {

    private final EmailServiceNewsletterImplement emailService;
    private final SuscripcionNoticiasService suscripcionNoticiasService;

    @PostMapping("/suscripcion-noticias")
    public ResponseEntity<String> suscripcion(@Valid @RequestBody EmailRequestDTO suscriptor) {
        log.info("Solicitando suscripción a noticias para el email: {}", suscriptor.getEmail());

        try {
            LocalDateTime fechaHora = suscripcionNoticiasService.suscribir(suscriptor.getEmail());
            suscriptor.setNumeroSuscriptores(suscripcionNoticiasService.obtenerTotalSuscriptores());
            suscriptor.setNumeroSuscriptoresMesActual(suscripcionNoticiasService.obtenerNuevosSuscriptoresMesActual());
            suscriptor.setFechaHoraSuscripcion(fechaHora);
            emailService.enviarCorreoAdministrador(suscriptor, "Nuevo suscriptor a noticias");
            emailService.enviarCorreoUsuario(suscriptor, "¡Bienvenido a nuestras noticias!");
            return ResponseEntity.ok("¡Gracias por suscribirte a nuestras noticias!");
        } catch (MessagingException e) {
            log.error("Error al enviar correos de suscripción para el email: {}", suscriptor.getEmail(), e);
            return ResponseEntity.status(500).body("Error al procesar la suscripción. Por favor, inténtelo de nuevo más tarde.");
        }
    }

    @PutMapping("/desuscripcion-noticias/{email}")
    public ResponseEntity<String> desuscripcion(@PathVariable String email) {
        log.info("Solicitando desuscripción a noticias para el email: {}", email);

        try {
            suscripcionNoticiasService.desuscribir(email);
            return ResponseEntity.ok("¡Te has desuscrito de nuestras noticias!");
        } catch (Exception e) {
            log.error("Error al desuscribir al usuario con email: {}", email, e);
            return ResponseEntity.status(500).body("Error al procesar la desuscripción. Por favor, inténtelo de nuevo más tarde.");
        }
    }


}
