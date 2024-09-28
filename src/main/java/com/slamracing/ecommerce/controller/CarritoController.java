package com.slamracing.ecommerce.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/api/v1/carrito")
public class CarritoController {


    @GetMapping("/verCarrito")
    public ResponseEntity<ArrayList<Object>> verCarrito() {
        ArrayList<Object> object = new ArrayList<>();

        return ResponseEntity.ok(object);
    }

}
