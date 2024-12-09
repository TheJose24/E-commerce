package com.slamracing.ecommerce.controller;

import com.slamracing.ecommerce.model.PagoEntity;
import com.slamracing.ecommerce.model.PedidoEntity;
import com.slamracing.ecommerce.model.ProductoEntity;
import com.slamracing.ecommerce.model.UsuarioRolEntity; // Asegúrate de importar el tipo correcto
import com.slamracing.ecommerce.repository.PagoRepository;
import com.slamracing.ecommerce.repository.PedidoRepository;
import com.slamracing.ecommerce.repository.ProductoRepository;
import com.slamracing.ecommerce.repository.UsuarioRolRepository; // Asegúrate de que este es el repositorio correcto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioRolRepository usuarioRolRepository; // Cambia el nombre si es necesario

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @GetMapping("/admin/inicio")
    public String inicio(Model model) {
        // Aquí puedes agregar lógica para obtener datos y pasarlos al modelo
        List<PedidoEntity> pedidos = pedidoRepository.findAll();
        List<ProductoEntity> productos = productoRepository.findAll();
        List<UsuarioRolEntity> usuarios = usuarioRolRepository.findAll();

        // Agregar datos al modelo
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("productos", productos);
        model.addAttribute("usuarios", usuarios);

        return "admin/Inicio"; // Nombre de la plantilla Thymeleaf
    }
}