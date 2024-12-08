package com.slamracing.ecommerce.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.slamracing.ecommerce.model.PedidoEntity;

import com.slamracing.ecommerce.repository.PedidoRepository;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<PedidoEntity> listarPedidos() {
        return pedidoRepository.findAll();
    }
    public List<PedidoEntity> buscarPedidos(String query) {
        return pedidoRepository.buscarPorCriterio(query);
    }

}
