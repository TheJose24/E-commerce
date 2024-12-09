package com.slamracing.ecommerce.controller;

import com.slamracing.ecommerce.service.CarritoService;
import com.slamracing.ecommerce.dto.CarritoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProducto(@Valid @RequestBody CarritoRequest request) {
        try {
            carritoService.agregarProducto(request, "usuario_temporal");
            return ResponseEntity.ok(carritoService.obtenerCarrito("usuario_temporal"));
        } catch (Exception e) {
            log.error("Error al agregar producto al carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long productoId) {
        try {
            carritoService.eliminarProducto(productoId, "usuario_temporal");
            return ResponseEntity.ok(carritoService.obtenerCarrito("usuario_temporal"));
        } catch (Exception e) {
            log.error("Error al eliminar producto del carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerCarrito() {
        try {
            return ResponseEntity.ok(carritoService.obtenerCarrito("usuario_temporal"));
        } catch (Exception e) {
            log.error("Error al obtener el carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}