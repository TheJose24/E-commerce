package com.slamracing.ecommerce.controller;

import com.slamracing.ecommerce.dto.RegisterRequest;
import com.slamracing.ecommerce.dto.UsuarioStatsDTO;
import com.slamracing.ecommerce.model.*;
import com.slamracing.ecommerce.repository.PagoRepository;
import com.slamracing.ecommerce.repository.PedidoRepository;
import com.slamracing.ecommerce.repository.ProductoRepository;
import com.slamracing.ecommerce.repository.UsuarioRepository;
import com.slamracing.ecommerce.security.service.SessionManager;
import com.slamracing.ecommerce.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {

    private final SessionManager sessionManager;
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final PedidoService pedidoService;
    private final PagoService pagoService;
    private final UserService userService;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final PagoRepository pagoRepository;

    String PAGINA_ACTUAL;

    @Setter
    @Getter
    private PedidoEntity pedido;

    @GetMapping("/procesoPago")
    public String procesoPago(Model model) {
        // Si el pedido no es nulo, agregarlo al modelo
        if (pedido != null) {
            log.info("Pedido recibido en procesoPago: {}", pedido);
            model.addAttribute("pedido", pedido);
            model.addAttribute("detalle", pedido.getDetalles());
        }
        return "user/comprar";
    }

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
        return "redirect:/" + PAGINA_ACTUAL;
    }

    @GetMapping("/")
    public String home(Model model, Locale locale) {
        PAGINA_ACTUAL = "";
        // Mostramos el idioma actual en mayúsculas
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());
        return "user/index";
    }

    @GetMapping("/productos")
    public String producto(Model model, Locale locale) {
        PAGINA_ACTUAL = "productos";
        // Mostrar el idioma actual en mayúsculas
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());

        List<ProductoEntity> productos = productoService.listarProductos();
        model.addAttribute("productos", productos);
        return "user/producto";
    }

    @GetMapping("/detalle/{slug}")
    public String informacionProducto(@PathVariable String slug, Model model, Locale locale) {
        PAGINA_ACTUAL = "productos";
        // Mostrar el idioma actual en mayúsculas
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());
        // Buscar el producto por su slug
        ProductoEntity productoDb = productoService.buscarProductoPorSlug(slug);
        log.info("Informacion del producto: {}", productoDb);
        model.addAttribute("producto", productoDb);

        // Buscar todos los productos con el mismo nombre
        List<ProductoEntity> productosPorNombre = productoService.listarProductosPorNombre(productoDb.getNombre());
        log.info("Productos con el mismo nombre: {}", productosPorNombre);
        model.addAttribute("productosPorNombre", productosPorNombre);

        return "user/informacion_producto";
    }

    @GetMapping("/contacto")
    public String contacto(Model model, Locale locale) {
        PAGINA_ACTUAL = "contacto";
        // Mostramos el idioma actual en mayúsculas
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());
        return "user/contacto";
    }

    @GetMapping("sobre_nosotros")
    public String sobre_nosotros(Model model, Locale locale) {
        PAGINA_ACTUAL = "sobre_nosotros";
        // Mostramos el idioma actual en mayúsculas
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());
        return "user/sobre_nosotros";
    }

    // Manejamos el envío del formulario de contacto
    @PostMapping("/formularioContacto")
    public String enviarFormularioContacto(@RequestParam String nombre, @RequestParam String correo, @RequestParam String mensaje) {
        System.out.println("nombre :" + nombre + ", correo :" + correo + ", mensaje :" + mensaje);

        return "redirect:/contacto";
    }


    @GetMapping("/soporte")
    public String soporte(Model model, Locale locale) {
        PAGINA_ACTUAL = "soporte";
        // Mostramos el idioma actual en mayúsculas
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());
        return "user/soporte";
    }

    @GetMapping("/login")
    public String login(Model model, Locale locale) {
        PAGINA_ACTUAL = "login";
        // Mostramos el idioma actual en mayúsculas
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model, Locale locale) {
        PAGINA_ACTUAL = "registro";
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());
        model.addAttribute("registerRequest", new RegisterRequest());
        return "registro";
    }

    @GetMapping("/401")
    public String unauthorized(Model model, Locale locale) {
        PAGINA_ACTUAL = "401";
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());
        return "401";
    }

    @GetMapping("/admin/productos")
    public String adminProductos(Model model, Locale locale) {
        String token = sessionManager.getCurrentToken();

        if (token == null) {
            return "redirect:/login";
        }

        if (!sessionManager.hasRole("ADMIN")) {
            return "redirect:/401";
        }

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


    @GetMapping("/admin/pedidosAdmin")
    public String listarPedidos(Model model) {
        String token = sessionManager.getCurrentToken();

        if (token == null) {
            return "redirect:/login";
        }

        if (!sessionManager.hasRole("ADMIN")) {
            return "redirect:/401";
        }
        List<PedidoEntity> pedidos = pedidoService.listarPedidos();
        model.addAttribute("pedidos", pedidos);
        return "admin/pedidosAdmin";
    }

    @GetMapping("/admin/pedidosAdmin/buscar")
    public String buscarPedidos(@RequestParam String query, Model model) {
        String token = sessionManager.getCurrentToken();

        if (token == null) {
            return "redirect:/login";
        }

        if (!sessionManager.hasRole("ADMIN")) {
            return "redirect:/401";
        }
        List<PedidoEntity> pedidos = pedidoService.buscarPedidos(query);
        model.addAttribute("pedidos", pedidos);
        return "admin/pedidosAdmin";

    }

    @GetMapping("/admin/pagosAdmin")
    public String listarPagos(@RequestParam(value = "query", required = false) String query, Model model) {
        String token = sessionManager.getCurrentToken();

        if (token == null) {
            return "redirect:/login";
        }

        if (!sessionManager.hasRole("ADMIN")) {
            return "redirect:/401";
        }

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

    @GetMapping("/admin/usuariosAdmin")
    public String listarUsuarios(Model model) {
        String token = sessionManager.getCurrentToken();

        if (token == null) {
            return "redirect:/login";
        }

        if (!sessionManager.hasRole("ADMIN")) {
            return "redirect:/401";
        }
        List<UsuarioEntity> usuarios = userService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuariosAdmin";
    }

    @GetMapping("/admin/usuariosAdmin/buscar")
    public String buscarUsuarios(@RequestParam("termino") String termino, Model model) {
        String token = sessionManager.getCurrentToken();

        if (token == null) {
            return "redirect:/login";
        }

        if (!sessionManager.hasRole("ADMIN")) {
            return "redirect:/401";
        }

        List<UsuarioEntity> usuarios = userService.buscarUsuarios(termino);
        model.addAttribute("usuarios", usuarios);
        return "admin/usuariosAdmin";
    }

    @PostMapping("/admin/usuariosAdmin/actualizarUsuario")
    public String actualizarUsuario(@ModelAttribute UsuarioEntity usuario) {
        String token = sessionManager.getCurrentToken();

        if (token == null) {
            return "redirect:/login";
        }

        if (!sessionManager.hasRole("ADMIN")) {
            return "redirect:/401";
        }

        try {
            userService.actualizarUsuario(usuario);
            log.info("Usuario actualizado: {}", usuario);
        } catch (Exception e) {
            log.error("Error al actualizar el usuario: {}", e.getMessage());
        }
        return "redirect:/admin/usuariosAdmin";
    }

    @PostMapping("/admin/usuariosAdmin/eliminarUsuario/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        String token = sessionManager.getCurrentToken();

        if (token == null) {
            return "redirect:/login";
        }

        if (!sessionManager.hasRole("ADMIN")) {
            return "redirect:/401";
        }

        try {
            userService.eliminarUsuario(id);
            log.info("Usuario eliminado con ID: {}", id);
        } catch (Exception e) {
            log.error("Error al eliminar el usuario con ID: {}", id, e);
        }
        return "redirect:/admin/usuariosAdmin";
    }

    @GetMapping("/admin/inicio")
    public String inicio(Model model) {
        String token = sessionManager.getCurrentToken();

        if (token == null) {
            return "redirect:/login";
        }

        if (!sessionManager.hasRole("ADMIN")) {
            return "redirect:/401";
        }

        // Obtener datos para el gráfico de pedidos por mes
        Map<String, Long> pedidosPorMes = pedidoRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        pedido -> pedido.getFechaPedido().getMonth().getDisplayName(TextStyle.FULL, new Locale("es")),
                        Collectors.counting()
                ));

        // Obtener datos para el gráfico de productos por categoría
        Map<String, Long> productosPorCategoria = productoRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        producto -> producto.getCategoria().getNombre(),
                        Collectors.counting()
                ));

        // Obtener estadísticas de usuarios utilizando pedidoRepository
        List<UsuarioStatsDTO> usuariosStats = usuarioRepository.findAll().stream()
                .map(usuario -> {
                    UsuarioStatsDTO stats = new UsuarioStatsDTO();
                    stats.setId(usuario.getUsuarioId());
                    // Contar pedidos para cada usuario usando pedidoRepository
                    Long cantidadPedidos = pedidoRepository.countByUsuario(usuario);
                    stats.setCantidadPedidos(cantidadPedidos);
                    return stats;
                })
                .collect(Collectors.toList());

        model.addAttribute("pedidosPorMes", pedidosPorMes);
        model.addAttribute("productosPorCategoria", productosPorCategoria);
        model.addAttribute("usuariosStats", usuariosStats);

        return "admin/Inicio";
    }

    @GetMapping("/api/auth/logout")
    public String logout(HttpSession session) {
        try {
            // Limpiar sessionManager
            sessionManager.clearSession();

            // Limpiar sesión HTTP
            session.invalidate();

            return "redirect:/login";
        } catch (Exception e) {
            log.error("Error durante el logout: ", e);
            return "redirect:/error";
        }
    }
}