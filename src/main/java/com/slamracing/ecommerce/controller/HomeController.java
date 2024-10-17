package com.slamracing.ecommerce.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class HomeController {

    @PostMapping("/suscripcionNoticias")
    public ResponseEntity<String> suscripcion(@Valid @RequestBody String email) {
        System.out.println("Email: " + email);
        return ResponseEntity.ok("¡Gracias por suscribirte a nuestras noticias!");
    }
}
