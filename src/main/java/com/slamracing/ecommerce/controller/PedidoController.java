package com.slamracing.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.slamracing.ecommerce.model.PedidoEntity;
import com.slamracing.ecommerce.service.PedidoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/pedidosAdmin")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public String listarPedidos(Model model) {
        List<PedidoEntity> pedidos = pedidoService.listarPedidos();
        model.addAttribute("pedidos", pedidos);
        return "admin/pedidosAdmin"; 
    }
    @GetMapping("/buscar")
public String buscarPedidos(@RequestParam String query, Model model) {
    List<PedidoEntity> pedidos = pedidoService.buscarPedidos(query);
    model.addAttribute("pedidos", pedidos);
    return "admin/pedidosAdmin"; 
}
}

