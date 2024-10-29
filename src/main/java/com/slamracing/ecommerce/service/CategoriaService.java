package com.slamracing.ecommerce.service;

import com.slamracing.ecommerce.model.CategoriaEntity;
import com.slamracing.ecommerce.repository.CategoriaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public void agregarCategoria(CategoriaEntity categoria) {
        categoriaRepository.save(categoria);
        log.info("Categoria agregada {}", categoria);
    }

    public CategoriaEntity buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
    }

    public void eliminarCategoria(Long id) {
        categoriaRepository.deleteById(id);
        log.info("Categoria eliminada con éxito");
    }

    public void actualizarCategoria(CategoriaEntity categoria) {
        categoriaRepository.save(categoria);
        log.info("Categoria actualizada {}", categoria);
    }

    public List<CategoriaEntity> listarCategorias() {
        return categoriaRepository.findAll();
    }
}
