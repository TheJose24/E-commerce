package com.slamracing.ecommerce.controller;

import com.slamracing.ecommerce.model.PedidoEntity;
import com.slamracing.ecommerce.model.ProductoEntity;
import com.slamracing.ecommerce.model.UsuarioEntity;

import java.util.List;

public class Dashboard {
    private List<PedidoEntity> pedidos;
    private List<ProductoEntity> productos;
    private List<UsuarioEntity> usuarios;

    // Constructor
    public Dashboard(List<PedidoEntity> pedidos, List<ProductoEntity> productos, List<UsuarioEntity> usuarios) {
        this.pedidos = pedidos;
        this.productos = productos;
        this.usuarios = usuarios;
    }

    // Getters y Setters
    public List<PedidoEntity> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<PedidoEntity> pedidos) {
        this.pedidos = pedidos;
    }

    public List<ProductoEntity> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoEntity> productos) {
        this.productos = productos;
    }

    public List<UsuarioEntity> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioEntity> usuarios) {
        this.usuarios = usuarios;
    }
}