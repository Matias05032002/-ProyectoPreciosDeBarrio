package com.tallerwebi.dominio.Producto;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioProducto")
@Transactional
public class ServicioProductoImpl implements ServicioProducto {

    private RepositorioProducto repositorioProducto;

    @Autowired
    public ServicioProductoImpl(RepositorioProducto repositorioProducto) {
        this.repositorioProducto = repositorioProducto;
    }

    @Override
    public Producto guardarProducto(Producto producto) {
        return repositorioProducto.guardarProducto(producto);
    }

    @Override
    public List<Producto> listarTodos() {
        return repositorioProducto.listarTodos();
    }

    @Override
    public Producto buscarProductoPorId(Long id) {
        return repositorioProducto.buscarProductoPorId(id);
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        return repositorioProducto.buscarPorNombre(nombre);
    }

    @Override
    public Producto buscarProductoPorNombreExacto(String nombre) {
        return repositorioProducto.buscarProductoPorNombreExacto(nombre);
    }
}
