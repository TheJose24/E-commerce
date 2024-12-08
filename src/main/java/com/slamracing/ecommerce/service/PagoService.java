package com.slamracing.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slamracing.ecommerce.model.PagoEntity;
import com.slamracing.ecommerce.model.enums.EstadoTransaccion;
import com.slamracing.ecommerce.model.enums.MetodoPago;
import com.slamracing.ecommerce.repository.PagoRepository;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    // Método para listar todos los pagos
    public List<PagoEntity> listarPagos() {
        return pagoRepository.findAll();
    }
    public List<PagoEntity> buscarPagos(String query) {
    try {
        EstadoTransaccion estado = EstadoTransaccion.valueOf(query.toUpperCase());
        return pagoRepository.buscarPorEstadoTransaccion(estado);
    } catch (IllegalArgumentException e1) {
        try {
            MetodoPago metodo = MetodoPago.valueOf(query.toUpperCase());
            return pagoRepository.buscarPorMetodoPago(metodo);
        } catch (IllegalArgumentException e2) {
            return pagoRepository.buscarPorCriterio("%" + query + "%");
        }
    }
}

}
