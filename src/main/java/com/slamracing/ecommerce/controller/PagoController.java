package com.slamracing.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.slamracing.ecommerce.model.PagoEntity;
import com.slamracing.ecommerce.service.PagoService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/pagosAdmin")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping
    public String listarPagos(@RequestParam(value = "query", required = false) String query, Model model) {
        List<PagoEntity> pagos;
        if (query != null && !query.isEmpty()) {
            pagos = pagoService.buscarPagos(query);
        } else {
            pagos = pagoService.listarPagos();
        }

        // Si no se encontraron pagos, agregar el mensaje al modelo
        if (pagos.isEmpty()) {
            model.addAttribute("mensaje", "No se encontraron pagos para la búsqueda: " + query);
        }

        model.addAttribute("pagos", pagos);
        model.addAttribute("query", query); // Mantener el valor de la búsqueda en el campo del formulario
        return "admin/pagosAdmin";
    }
}