package com.slamracing.ecommerce.controller;

import com.slamracing.ecommerce.dto.ActualizarCantidadRequest;
import com.slamracing.ecommerce.dto.DetallePedidoDTO;
import com.slamracing.ecommerce.dto.PedidoDTO;
import com.slamracing.ecommerce.model.DetallePedidoEntity;
import com.slamracing.ecommerce.model.PedidoEntity;
import com.slamracing.ecommerce.model.ProductoEntity;
import com.slamracing.ecommerce.service.ProductoService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/carrito")
@SessionAttributes("carrito")
public class CarritoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private WebController webController;

    @Getter
    @Setter
    private Set<DetallePedidoEntity> detalles = new HashSet<>();

    @Getter
    @Setter
    private PedidoEntity pedido = new PedidoEntity();

    @PostMapping("/pagar")
    public ResponseEntity<Void> pagar(@RequestBody PedidoDTO pedidoDTO) {
        try {
            log.info("Procesando pago del pedido");

            PedidoEntity pedido = new PedidoEntity();
            pedido.setSubtotal(pedidoDTO.getSubtotal());
            pedido.setTotal(pedidoDTO.getTotal());
            pedido.setFechaPedido(LocalDateTime.now());

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
            webController.setPedido(pedido);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al procesar el pago: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/verCarrito")
    public ResponseEntity<PedidoEntity> verCarrito() {
        try {
            if (pedido.getDetalles() == null) {
                pedido.setDetalles(new HashSet<>());
            }
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            log.error("Error al obtener carrito: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/agregarProducto")
    public ResponseEntity<Void> agregarProducto(@RequestBody Long id) {
        try {
            ProductoEntity producto = productoService.buscarProductoPorId(id);
            if (producto == null) {
                return ResponseEntity.notFound().build();
            }

            encontrarOCrearDetalle(producto);
            actualizarTotalesPedido();

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al agregar producto: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/actualizarCantidad/{id}")
    public ResponseEntity<Void> actualizarCantidad(
            @PathVariable Long id,
            @RequestBody ActualizarCantidadRequest request) {
        try {
            Optional<DetallePedidoEntity> detalleOpt = encontrarDetalle(id);
            if (detalleOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (!actualizarCantidadDetalle(detalleOpt.get(), request.getIncremento())) {
                return ResponseEntity.badRequest().build();
            }

            actualizarTotalesPedido();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar cantidad: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/eliminarProducto/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        try {
            if (detalles.removeIf(d -> d.getProducto().getProductoId().equals(id))) {
                actualizarTotalesPedido();
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error al eliminar producto: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private DetallePedidoEntity encontrarOCrearDetalle(ProductoEntity producto) {
        return detalles.stream()
                .filter(d -> d.getProducto().getProductoId().equals(producto.getProductoId()))
                .findFirst()
                .map(this::incrementarCantidad)
                .orElseGet(() -> crearNuevoDetalle(producto));
    }

    private DetallePedidoEntity incrementarCantidad(DetallePedidoEntity detalle) {
        if (detalle.getCantidad() < detalle.getProducto().getStock()) {
            detalle.setCantidad(detalle.getCantidad() + 1);
            detalle.setTotal(calcularTotalDetalle(detalle));
        }
        return detalle;
    }

    private DetallePedidoEntity crearNuevoDetalle(ProductoEntity producto) {
        DetallePedidoEntity detalle = new DetallePedidoEntity();
        detalle.setProducto(producto);
        detalle.setCantidad(1);
        detalle.setPrecioUnitario(producto.getPrecio());
        detalle.setTotal(calcularTotalDetalle(detalle));
        detalle.setPedido(pedido);
        detalles.add(detalle);
        return detalle;
    }

    private Optional<DetallePedidoEntity> encontrarDetalle(Long productoId) {
        return detalles.stream()
                .filter(d -> d.getProducto().getProductoId().equals(productoId))
                .findFirst();
    }

    private boolean actualizarCantidadDetalle(DetallePedidoEntity detalle, boolean incremento) {
        int nuevaCantidad = detalle.getCantidad() + (incremento ? 1 : -1);

        if (nuevaCantidad <= 0) {
            detalles.remove(detalle);
            return true;
        }

        if (nuevaCantidad > detalle.getProducto().getStock()) {
            return false;
        }

        detalle.setCantidad(nuevaCantidad);
        detalle.setTotal(calcularTotalDetalle(detalle));
        return true;
    }

    private BigDecimal calcularTotalDetalle(DetallePedidoEntity detalle) {
        BigDecimal precioUnitario = detalle.getProducto().getPrecio();
        BigDecimal descuento = BigDecimal.valueOf(detalle.getProducto().getDescuento() / 100.0);
        BigDecimal precioConDescuento = precioUnitario.subtract(precioUnitario.multiply(descuento));
        return precioConDescuento.multiply(BigDecimal.valueOf(detalle.getCantidad()));
    }

    private void actualizarTotalesPedido() {
        BigDecimal subtotal = detalles.stream()
                .map(DetallePedidoEntity::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setSubtotal(subtotal);
        pedido.setTotal(subtotal);
        pedido.setDetalles(detalles);
    }
}
