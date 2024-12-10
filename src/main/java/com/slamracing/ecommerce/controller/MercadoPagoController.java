package com.slamracing.ecommerce.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slamracing.ecommerce.dto.DetallePedidoDTO;
import com.slamracing.ecommerce.dto.PedidoDTO;
import com.slamracing.ecommerce.model.DetallePedidoEntity;
import com.slamracing.ecommerce.model.PedidoEntity;
import com.slamracing.ecommerce.model.ProductoEntity;
import com.slamracing.ecommerce.model.UsuarioEntity;
import com.slamracing.ecommerce.model.enums.EstadoPedido;
import com.slamracing.ecommerce.payment.dto.PagoTarjetaDTO;
import com.slamracing.ecommerce.payment.dto.PreferenciaRequestDTO;
import com.slamracing.ecommerce.payment.service.PagoTarjetaService;
import com.slamracing.ecommerce.payment.service.PreferenciaService;
import com.slamracing.ecommerce.service.PedidoService;
import com.slamracing.ecommerce.service.ProductoService;
import com.slamracing.ecommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/v1/mercadoPago")
public class MercadoPagoController {

    @Autowired
    private final PagoTarjetaService pagoTarjetaService;

    @Autowired
    private final PreferenciaService preferenciaService;

    @Autowired
    private UserService usuarioService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CarritoController carritoController;

    @Autowired
    PedidoService pedidoService;

    public MercadoPagoController(PagoTarjetaService pagoTarjetaService, PreferenciaService preferenciaService) {
        this.pagoTarjetaService = pagoTarjetaService;
        this.preferenciaService = preferenciaService;
    }

    @PostMapping("/preferencias/crear")
    public ResponseEntity<String> crearPreferencia(@RequestBody PreferenciaRequestDTO preferenciaRequestDTO) {
        System.out.println("request de preferencia recibido: " + preferenciaRequestDTO);
        String preferenceId = preferenciaService.createPreference(preferenciaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(preferenceId);
    }

    @PostMapping("/procesar_pago")
    public ResponseEntity<Long> procesarPago(@RequestBody @Validated PagoTarjetaDTO pagoTarjetaDTO){
        Long paymentId = pagoTarjetaService.processPayment(pagoTarjetaDTO);
        System.out.println(paymentId.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentId);
    }

    @PostMapping("/generarPedido")
    public ResponseEntity<Long> generarPedido(@RequestBody PedidoDTO pedidoDTO) {
        try {
            log.info("Recibiendo pedido DTO: {}", pedidoDTO);

            // Crear pedido
            PedidoEntity pedido = new PedidoEntity();
            pedido.setSubtotal(pedidoDTO.getSubtotal());
            pedido.setTotal(pedidoDTO.getTotal());
            pedido.setFechaPedido(LocalDateTime.now());
            pedido.setEstado(EstadoPedido.PENDIENTE);

            // Buscar y validar usuario
            UsuarioEntity usuarioBd = usuarioService.buscarUsuarioPorId(7L);
            if (usuarioBd == null) {
                log.error("Usuario no encontrado con ID: {}", 7L);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }
            pedido.setUsuario(usuarioBd);

            // Crear detalles
            Set<DetallePedidoEntity> detalles = new HashSet<>();
            for (DetallePedidoDTO detalleDTO : pedidoDTO.getDetalles()) {
                DetallePedidoEntity detalle = new DetallePedidoEntity();
                detalle.setPedido(pedido);
                detalle.setProducto(productoService.buscarProductoPorId(detalleDTO.getProductoId()));
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
                detalle.setTotal(detalleDTO.getTotal());
                detalles.add(detalle);
            }
            pedido.setDetalles(detalles);

            pedidoService.guardarPedido(pedido);
            log.info("Pedido guardado con ID: {}", pedido.getPedidoId());

            return ResponseEntity.status(HttpStatus.CREATED).body(pedido.getPedidoId());
        } catch (Exception e) {
            log.error("Error al generar el pedido: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/notificacion")
    public ResponseEntity<String> recibirNotificacion(@RequestBody String body) {
        try {
            System.out.println("Webhook: " + body);

            // Analizar el JSON del cuerpo de la notificación para extraer el ID del pago
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode bodyJson = objectMapper.readTree(body);

            String paymentId = bodyJson.get("data").get("id").asText(); // Suponiendo que el ID del pago está en un campo llamado "payment_id"

            String pagoInfo = pagoTarjetaService.buscarPagoPorId(paymentId);

            ObjectMapper pagoMapper = new ObjectMapper();
            JsonNode pagoJson  = pagoMapper.readTree(pagoInfo);

            String estadoPago = pagoJson.get("status").asText();

            if ("approved".equals(estadoPago)) {
                // El pago fue aprobado, actualizar el stock del producto y el estado del pedido
                PedidoEntity pedido = pedidoService.buscarPedidoPorId(Long.parseLong(pagoJson.get("external_reference").asText()));

                Set<DetallePedidoEntity> detallesPedido = pedido.getDetalles();
                for (DetallePedidoEntity detalle : detallesPedido) {
                    ProductoEntity producto = detalle.getProducto();
                    producto.setStock(producto.getStock() - detalle.getCantidad());
                    productoService.actualizarProducto(producto);
                    carritoController.setPedido(new PedidoEntity());
                    carritoController.setDetalles(new HashSet<>());
                }

            }

            return ResponseEntity.status(HttpStatus.OK).body("Notificación recibida");
        } catch (JsonProcessingException e) {
            log.error("Error al analizar el JSON de la notificación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al analizar el JSON de la notificación");
        } catch (Exception e) {
            log.error("Error al recibir la notificación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al recibir la notificación");
        }
    }
}
