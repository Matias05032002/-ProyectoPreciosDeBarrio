package com.tallerwebi.dominio.Producto;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import java.util.List;

public interface ServicioProducto {
  Producto guardarProducto(Producto producto);
  List<Producto> listarTodos();
  Producto buscarProductoPorId(Long id);
  List<Producto> buscarPorNombre(String nombre);
  Producto buscarProductoPorNombreExacto(String nombre);
  List<ProductoConPrecio> buscarProductosConPrecio(String nombre, Usuario usuario);
}
