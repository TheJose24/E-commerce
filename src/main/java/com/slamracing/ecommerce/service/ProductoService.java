package com.slamracing.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import com.slamracing.ecommerce.model.ProductoEntity;
import com.slamracing.ecommerce.model.ResenaEntity;
import com.slamracing.ecommerce.model.GaleriaImagenEntity;
import com.slamracing.ecommerce.repository.GaleriaImagenRepository;
import com.slamracing.ecommerce.repository.ResenaRepository;
import com.slamracing.ecommerce.repository.ProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
@Slf4j
@Service
public class ProductoService {

    @Autowired
    private GaleriaImagenRepository galeriaImagenRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ResenaRepository resenaRepository;

    public void agregarReviewProducto(ResenaEntity reviewProducto) {

        Long productoId = reviewProducto.getProducto().getProductoId();
        ProductoEntity producto = buscarProductoPorId(productoId);
        int numOpiniones = producto.getNumOpiniones();
        double puntuacionPromedio = producto.getPuntuacionPromedio();

        // Actualizar el número de opiniones y la puntuación promedio
        if (numOpiniones != 0) {
            puntuacionPromedio = (puntuacionPromedio * numOpiniones + reviewProducto.getCalificacion()) / (numOpiniones + 1);
            producto.setNumOpiniones(numOpiniones + 1);
        } else {
            puntuacionPromedio = reviewProducto.getCalificacion();
            producto.setNumOpiniones(1);
        }

        // Actualizar el producto con la nueva información
        producto.setPuntuacionPromedio(puntuacionPromedio);


        reviewProducto.setProducto(producto);
        resenaRepository.save(reviewProducto);
        System.out.println("Review guardado: " + reviewProducto);

    }
    public ProductoEntity buscarProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow (() -> new RuntimeException("Producto no encontrado"));
    }
    public List<ResenaEntity> opinionesPorProducto(Long idProducto) {
        return resenaRepository.findByProductoId(idProducto);
    }
    public String generarNombreUrl(String nombre, String color) {
        return nombre.toLowerCase().replace(" ", "-") + "-" + color.toLowerCase().replace(" ", "-");

    }

    public ProductoEntity agregarProducto(ProductoEntity producto) {

        for (GaleriaImagenEntity imagen : producto.getImagenes()) {
            imagen.setProducto(producto);
        }

        if (producto.getDescuento() == null) {
            producto.setDescuento(0);
        }

        if (producto.getStock() == null) {
            producto.setStock(0);
        }
        if (producto.getPuntuacionPromedio() == 0){
            producto.setPuntuacionPromedio(0.0);
        }

        return productoRepository.save(producto);
    }

    public List<ProductoEntity> listarProductos() {
        return productoRepository.findAll();
    }
    public List<ProductoEntity> listarProductosPorNombre(String nombre) {
        return productoRepository.findByNombre(nombre);
    }

    public ProductoEntity buscarProductoPorSlug(String slug) {
        return productoRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }


    public void actualizarProducto(ProductoEntity producto) {

        if (producto.getProductoId() == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }

        System.out.println("Producto obtenido de el controlador: " + producto);

        ProductoEntity productoDb = buscarProductoPorId(producto.getProductoId());

        // Actualizar las propiedades del producto
        producto.setNombre(producto.getNombre() != null && !producto.getNombre().isEmpty() ? producto.getNombre() : productoDb.getNombre());
        producto.setDescripcion(producto.getDescripcion() != null && !producto.getDescripcion().isEmpty() ? producto.getDescripcion() : productoDb.getDescripcion());
        producto.setMaterial(producto.getMaterial() != null && !producto.getMaterial().isEmpty() ? producto.getMaterial() : productoDb.getMaterial());
        producto.setPrecio(producto.getPrecio() != null ? producto.getPrecio() : productoDb.getPrecio());
        producto.setCategoria(producto.getCategoria() != null ? producto.getCategoria() : productoDb.getCategoria());
        producto.setDescuento(producto.getDescuento() !=null  ? producto.getDescuento() : productoDb.getDescuento());
        producto.setStock(producto.getStock() != null ? producto.getStock() : productoDb.getStock());
        producto.setColor(producto.getColor() != null && !producto.getColor().isEmpty() ? producto.getColor() : productoDb.getColor());
        producto.setNumOpiniones(productoDb.getNumOpiniones());
        producto.setPuntuacionPromedio(productoDb.getPuntuacionPromedio());

        // Generar el slug si el nombre o el color han cambiado
        boolean nombreCambiado = !producto.getNombre().equals(productoDb.getNombre());
        boolean colorCambiado = !producto.getColor().equals(productoDb.getColor());

        if (nombreCambiado || colorCambiado ) {
            producto.setSlug(generarNombreUrl(producto.getNombre(), producto.getColor()));
        } else {
            producto.setSlug(productoDb.getSlug());
        }

        // Verificar si hay imágenes asociadas al producto
        if (producto.getImagenes() != null && !producto.getImagenes().isEmpty()) {
            System.out.println("Imagenes del producto desde el servicio: " + producto.getImagenes());
            for (GaleriaImagenEntity imagen : producto.getImagenes()) {
                System.out.println("Imagen: " + imagen);
                // Establecer la relación con el producto
                imagen.setProducto(productoDb);
                System.out.println("Imagen actualizada: " + imagen);

                if (imagen.getImagenId() == null) {
                    galeriaImagenRepository.save(imagen);
                }
            }
        }

        try {
            System.out.println("Producto actualizado desde el servicio: " + producto);
            productoRepository.save(producto);
        } catch (Exception e) {
            log.error("Error al actualizar el producto con ID: " + producto.getProductoId(), e);
            throw new RuntimeException("Error al actualizar el producto. Consulta los registros para más detalles.", e);
        }
    }
    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }


}
