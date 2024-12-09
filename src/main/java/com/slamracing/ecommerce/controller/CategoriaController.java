package com.slamracing.ecommerce.controller;

import com.slamracing.ecommerce.model.CategoriaEntity;
import com.slamracing.ecommerce.security.service.JwtService;
import com.slamracing.ecommerce.security.service.SessionManager;
import com.slamracing.ecommerce.service.CategoriaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/categoria")
@RequiredArgsConstructor
@Slf4j
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final SessionManager sessionManager;

    @PostMapping("/agregarCategoria")
    public String agregarCategoria(
            @ModelAttribute CategoriaEntity categoria,
            @RequestParam("_token") String token,
            HttpSession session) {

        try {
            if (!sessionManager.isValidToken(token)) {
                return "redirect:/login";
            }

            categoriaService.agregarCategoria(categoria);
            log.info("Categoría agregada exitosamente: {}", categoria.getNombre());
            return "redirect:/admin/productos";
        } catch (Exception e) {
            log.error("Error al agregar categoría: {}", e.getMessage());
            session.setAttribute("error", "Error al agregar la categoría");
            return "redirect:/admin/productos";
        }
    }

    @PostMapping("/eliminarCategoria/{id}")
    public String eliminarCategoria(
            @PathVariable Long id,
            @RequestParam("_token") String token,
            HttpSession session) {

        try {
            if (!sessionManager.isValidToken(token)) {
                return "redirect:/login";
            }

            categoriaService.eliminarCategoria(id);
            log.info("Categoría eliminada exitosamente: {}", id);
            return "redirect:/admin/productos";
        } catch (Exception e) {
            log.error("Error al eliminar categoría: {}", e.getMessage());
            session.setAttribute("error", "Error al eliminar la categoría");
            return "redirect:/admin/productos";
        }
    }

    @PostMapping("/actualizarCategoria")
    public String actualizarCategoria(
            @ModelAttribute CategoriaEntity categoria,
            @RequestParam("_token") String token,
            HttpSession session) {

        try {
            if (!sessionManager.isValidToken(token)) {
                return "redirect:/login";
            }

            categoriaService.actualizarCategoria(categoria);
            log.info("Categoría actualizada exitosamente: {}", categoria.getNombre());
            return "redirect:/admin/productos";
        } catch (Exception e) {
            log.error("Error al actualizar categoría: {}", e.getMessage());
            session.setAttribute("error", "Error al actualizar la categoría");
            return "redirect:/admin/productos";
        }
    }
}