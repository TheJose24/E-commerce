package com.slamracing.ecommerce.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slamracing.ecommerce.model.UsuarioEntity;
import com.slamracing.ecommerce.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioEntity> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<UsuarioEntity> buscarUsuarios(String termino) {
        if (termino == null || termino.isBlank()) {
            return listarUsuarios(); // Devuelve todos si no hay término
        }
        return usuarioRepository.findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(termino, termino);
    }

    public UsuarioEntity buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public void actualizarUsuario(UsuarioEntity usuario) {
        if (usuario.getUsuarioId() == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        UsuarioEntity usuarioDb = buscarUsuarioPorId(usuario.getUsuarioId());

        // Actualizar las propiedades del usuario
        usuarioDb.setNombre(usuario.getNombre() != null ? usuario.getNombre() : usuarioDb.getNombre());
        usuarioDb.setEmail(usuario.getEmail() != null ? usuario.getEmail() : usuarioDb.getEmail());
        usuarioDb.setContraseña(usuario.getContraseña() != null ? usuario.getContraseña() : usuarioDb.getContraseña());
        usuarioDb.setUltimaActualizacion(LocalDateTime.now());

        // Actualizar las direcciones asociadas, si es necesario
        if (usuario.getDirecciones() != null && !usuario.getDirecciones().isEmpty()) {
            usuarioDb.getDirecciones().clear();
            usuarioDb.getDirecciones().addAll(usuario.getDirecciones());
            usuario.getDirecciones().forEach(direccion -> direccion.setUsuario(usuarioDb));
        }

        try {
            usuarioRepository.save(usuarioDb);
            log.info("Usuario actualizado desde el servicio: {}", usuarioDb);
        } catch (Exception e) {
            log.error("Error al actualizar el usuario con ID: {}", usuario.getUsuarioId(), e);
            throw new RuntimeException("Error al actualizar el usuario. Consulta los registros para más detalles.", e);
        }
    }

    public void eliminarUsuario(Long id) {
        UsuarioEntity usuario = buscarUsuarioPorId(id);

        // Si el usuario tiene direcciones asociadas, eliminarlas primero
        if (usuario.getDirecciones() != null && !usuario.getDirecciones().isEmpty()) {
            usuario.getDirecciones().clear();
        }

        usuarioRepository.deleteById(id);
        log.info("Usuario eliminado con ID: {}", id);
    }
}