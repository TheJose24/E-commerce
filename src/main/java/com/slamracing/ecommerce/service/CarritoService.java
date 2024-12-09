package com.slamracing.ecommerce.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import com.slamracing.ecommerce.dto.CarritoRequest;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CarritoService {
    private final Map<String, Map<Long, CarritoItem>> carritos = new ConcurrentHashMap<>();
    
    public Map<Long, CarritoItem> obtenerCarrito(String username) {
        return carritos.getOrDefault(username, new HashMap<>());
    }
    
    public void agregarProducto(CarritoRequest request, String username) {
        carritos.computeIfAbsent(username, k -> new HashMap<>());
        Map<Long, CarritoItem> carrito = carritos.get(username);
        
        CarritoItem item = new CarritoItem();
        item.setCantidad(request.getCantidad());
        item.setPrecio(BigDecimal.ZERO); // Temporal, deberías obtener el precio real
        item.setTotal(BigDecimal.ZERO); // Temporal, deberías calcular el total real
        
        carrito.put(request.getProductoId(), item);
        log.info("Producto {} agregado al carrito para el usuario: {}", request.getProductoId(), username);
    }

    public void eliminarProducto(Long productoId, String username) {
        Map<Long, CarritoItem> carrito = carritos.get(username);
        if (carrito != null) {
            carrito.remove(productoId);
            log.info("Producto {} eliminado del carrito para el usuario: {}", productoId, username);
        }
    }
}

@Data
class CarritoItem {
    private Integer cantidad;
    private BigDecimal precio;
    private BigDecimal total;
} 