package com.tallerwebi.dominio.Producto;

import java.util.List;

public interface ServicioProducto {
    Producto guardarProducto(Producto producto);
    List<Producto> listarTodos();
    Producto buscarProductoPorId(Long id);
    List<Producto> buscarPorNombre(String nombre);
    Producto buscarProductoPorNombreExacto(String nombre);
}
