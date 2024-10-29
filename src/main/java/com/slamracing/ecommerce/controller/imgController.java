package com.slamracing.ecommerce.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class imgController {
    private final ResourceLoader resourceLoader;

    public imgController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/productoImages/{filename:.+}")
    public Resource getImage(@PathVariable String filename) {
        // Cargar el recurso de la carpeta images
        return resourceLoader.getResource("file:productoImages/" + filename);
    }

}
