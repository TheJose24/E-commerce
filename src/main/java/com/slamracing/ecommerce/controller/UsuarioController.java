package com.slamracing.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.slamracing.ecommerce.model.UsuarioEntity;
import com.slamracing.ecommerce.service.UsuarioService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/usuariosAdmin")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model) {
        List<UsuarioEntity> usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuariosAdmin";
    }

    @GetMapping("/buscar")
    public String buscarUsuarios(@RequestParam("termino") String termino, Model model) {
        List<UsuarioEntity> usuarios = usuarioService.buscarUsuarios(termino);
        model.addAttribute("usuarios", usuarios);
        return "admin/usuariosAdmin";
    }

    @PostMapping("/actualizarUsuario")
    public String actualizarUsuario(@ModelAttribute UsuarioEntity usuario) {
        try {
            usuarioService.actualizarUsuario(usuario);
            log.info("Usuario actualizado: {}", usuario);
        } catch (Exception e) {
            log.error("Error al actualizar el usuario: {}", e.getMessage());
        }
        return "redirect:/admin/usuariosAdmin";
    }

    @PostMapping("/eliminarUsuario/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            log.info("Usuario eliminado con ID: {}", id);
        } catch (Exception e) {
            log.error("Error al eliminar el usuario con ID: {}", id, e);
        }
        return "redirect:/admin/usuariosAdmin";
    }
}