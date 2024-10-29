package com.slamracing.ecommerce.controller;

import com.slamracing.ecommerce.model.CategoriaEntity;
import com.slamracing.ecommerce.model.ProductoEntity;
import com.slamracing.ecommerce.service.CategoriaService;
import com.slamracing.ecommerce.service.ProductoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
public class WebController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public WebController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    String PAGINA_ACTUAL;

    @GetMapping("/cambiar_idioma")
    public String changeLanguage(@RequestParam("idioma") String lang, HttpSession session) {
        Locale newLocale;

        // Usar el Locale específico basado en el idioma
        if ("en".equalsIgnoreCase(lang)) {
            newLocale = Locale.US; // Inglés de Estados Unidos
        } else if ("es".equalsIgnoreCase(lang)) {
            newLocale = new Locale.Builder()
                    .setLanguage("ES") // Español de Perú
                    .setRegion("PE")
                    .build();
        } else {
            newLocale = Locale.getDefault(); // Otras opciones, por defecto
        }

        // Establecer el Locale en la sesión
        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, newLocale);
        return "redirect:/"+PAGINA_ACTUAL;
    }

    @GetMapping("/")
    public String home(Model model, Locale locale) {
        PAGINA_ACTUAL="";
        // Mostramos el idioma actual en mayúsculas
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());
        return "user/index";
    }

    @GetMapping("/productos")
    public String producto(Model model, Locale locale) {
        PAGINA_ACTUAL="productos";
        // Mostrar el idioma actual en mayúsculas
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());
        return "user/producto";
    }

    @GetMapping("/contacto")
    public String contacto(Model model, Locale locale) {
        PAGINA_ACTUAL="contacto";
        // Mostramos el idioma actual en mayúsculas
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());
        return "user/contacto";
    }

    // Manejamos el envío del formulario de contacto
    @PostMapping("/formularioContacto")
    public String enviarFormularioContacto(@RequestParam String nombre,@RequestParam  String correo, @RequestParam String mensaje) {
        System.out.println("nombre :"+ nombre + ", correo :"+ correo + ", mensaje :"+ mensaje);

        return "redirect:/contacto";
    }

    @GetMapping("/admin/productos")
    public String adminProductos(Model model, Locale locale) {
        PAGINA_ACTUAL = "admin/productos";
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());

        // Obtenemos la lista de productos desde el servicio
        List<ProductoEntity> productos = productoService.listarProductos();
        model.addAttribute("productos", productos);

        model.addAttribute("producto", new ProductoEntity());

        List<CategoriaEntity> categorias = categoriaService.listarCategorias();

        model.addAttribute("categorias", categorias);
        return "admin/productosAdmin";
    }



}
