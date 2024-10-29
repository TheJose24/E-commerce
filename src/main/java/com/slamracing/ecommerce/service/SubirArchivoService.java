package com.slamracing.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SubirArchivoService {
    private final String folder = "productoImages//";

    public String guardarImagen(MultipartFile file) {
        if (!file.isEmpty()) {
            try {

                File directorio = new File(folder);
                if (!directorio.exists()) {
                    directorio.mkdirs();
                }

                byte[] bytes = file.getBytes();
                Path path = Paths.get(folder + file.getOriginalFilename());
                Files.write(path, bytes);
                return file.getOriginalFilename();
            } catch (IOException e) {
                System.out.println("Error al guardar la imagen");
                e.printStackTrace();
            }
        }

        return "default.png";
    }

    public void eliminarImagen(String nombre) {
        String ruta = folder;
        File file = new File(ruta + nombre);
        file.delete();
    }
}

