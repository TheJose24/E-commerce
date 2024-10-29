package com.slamracing.ecommerce.controller;

import com.slamracing.ecommerce.model.GaleriaImagenEntity;
import com.slamracing.ecommerce.model.ProductoEntity;
import com.slamracing.ecommerce.repository.GaleriaImagenRepository;
import com.slamracing.ecommerce.service.ProductoService;
import com.slamracing.ecommerce.service.SubirArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Controller
@RequestMapping("/api/v1/producto")

public class ProductoController {

    @Autowired
    private SubirArchivoService subirArchivoService;
    @Autowired
    private ProductoService productoService;

    @PostMapping("/agregarProducto")
    public String agregarProducto(@ModelAttribute ProductoEntity producto,
                                  @RequestParam("imagen") MultipartFile[] files) {
        if (producto.getProductoId() == null) {
            List<GaleriaImagenEntity> imagenes = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                try {
                    String nombreImagen = subirArchivoService.guardarImagen(file);
                    GaleriaImagenEntity imagen = new GaleriaImagenEntity();
                    imagen.setUrlImagen(nombreImagen);
                    imagen.setProducto(producto);
                    imagen.setOrden(i);
                    imagenes.add(imagen);
                } catch (Exception e) {
                    log.error("Error al guardar la imagen: {}", e.getMessage());
                    return "redirect:/admin/productos"; // Redirige en caso de error
                }
            }
            producto.setImagenes(imagenes);
        }
        productoService.agregarProducto(producto);
        log.info("Producto agregado {}", producto);
        return "redirect:/admin/productos";
    }


    @PostMapping("/actualizarProducto")
    public String actualizarProducto(@ModelAttribute ProductoEntity producto, @RequestParam("imagen") MultipartFile[] files) {
        // Busca el producto en la base de datos
        System.out.println("=============================================");
        ProductoEntity productodb = productoService.buscarProductoPorId(producto.getProductoId());
        System.out.println("Producto base de datos: " + productodb);
        System.out.println("Producto recibido para actualizar: " + producto);
        System.out.println("=============================================");

        // Verifica si se proporcionaron archivos y si la matriz de archivos no está vacía
        if (files != null && files.length > 0) {
            System.out.println("=============================================");
            System.out.println("Archivos proporcionados: " + files.length);

            // Lista para almacenar las nuevas imágenes
            List<GaleriaImagenEntity> nuevasImagenes = new ArrayList<>();
            List<GaleriaImagenEntity> imagenesDb = productodb.getImagenes();
            System.out.println("Imagenes de bd: " + imagenesDb.size());
            System.out.println("=============================================");

            // Itera sobre los archivos proporcionados
            for (int i = 0; i < files.length; i++) {
                System.out.println("=============================================");
                MultipartFile file = files[i];
                System.out.println("Archivo: " + file.getOriginalFilename());
                // Verifica si el archivo actual no está vacío
                System.out.println("=============================================");
                if (!file.isEmpty()) {
                    System.out.println("Archivo no vacío");

                    // Guarda la nueva imagen y obtiene su URL
                    String nombreImagen = subirArchivoService.guardarImagen(file);
                    System.out.println("Imagen guardada: " + nombreImagen);

                    // Crea una nueva instancia de ProductoImagen y la agrega a la lista de nuevas imágenes
                    GaleriaImagenEntity nuevaImagen = new GaleriaImagenEntity();
                    System.out.println("numero de iteracion: "+i);
                    System.out.println("numero de imagenes registradas: "+imagenesDb.size());
                    if (i < imagenesDb.size()) {
                        System.out.println("Se cumple condicion");
                        nuevaImagen.setImagenId(imagenesDb.get(i).getImagenId());
                        if (!imagenesDb.get(i).getUrlImagen().equals("default.png")) {
                            subirArchivoService.eliminarImagen(imagenesDb.get(i).getUrlImagen());
                        }
                    }
                    nuevaImagen.setUrlImagen(nombreImagen);
                    nuevaImagen.setProducto(producto);
                    nuevaImagen.setOrden(i);
                    nuevasImagenes.add(nuevaImagen);
                    System.out.println("Nueva imagen: " + nuevaImagen);
                }else {
                    nuevasImagenes.add(imagenesDb.get(i));
                    System.out.println("Archivo vacío, guardando imagen de bd: "+imagenesDb.get(i));
                }


            }

            System.out.println("=============================================");
            // Reemplaza las imágenes existentes con las nuevas imágenes
            producto.setImagenes(nuevasImagenes);
            System.out.println("Imagenes actualizadas: " + producto.getImagenes());
        }

        // Guarda el producto actualizado en la base de datos
        try {
            productoService.actualizarProducto(producto);
            System.out.println("Producto actualizado: " + producto);
            log.info("Producto actualizado {}", producto);
        } catch (Exception e) {
            // Maneja el error de manera adecuada, por ejemplo, mostrando un mensaje de error al usuario
            System.out.println("Error al actualizar el producto: " + e.getMessage());
            log.error("Error al actualizar el producto: {}", e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @PostMapping("/eliminarProducto/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        ProductoEntity productodb = productoService.buscarProductoPorId(id);

        List<GaleriaImagenEntity> imagenes = productodb.getImagenes();

        for (GaleriaImagenEntity imagen : imagenes) {
            if (!imagen.getUrlImagen().equals("default.png")) {
                subirArchivoService.eliminarImagen(imagen.getUrlImagen());
            }
        }

        log.info("Producto eliminado {}", productodb);
        productoService.eliminarProducto(id);
        return "redirect:/admin/productos";
    }



}
