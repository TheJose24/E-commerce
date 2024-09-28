package com.slamracing.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @PostMapping("/suscripcionNoticias")
    public ResponseEntity<String> suscripcion(@RequestParam String email) {
        return ResponseEntity.ok("¡Gracias por suscribirte a nuestras noticias!");
    }
}
