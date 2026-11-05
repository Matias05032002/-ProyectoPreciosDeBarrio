package com.tallerwebi.dominio.Producto;

import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import java.util.List;

public interface ServicioProducto {
  Producto guardarProducto(Producto producto);
  List<Producto> listarTodos();
  Producto buscarProductoPorId(Long id);
  List<Producto> buscarPorNombre(String nombre);
  Producto buscarProductoPorNombreExacto(String nombre);
  List<ProductoConPrecio> buscarProductosConPrecio(
    String nombre,
    Usuario usuario,
    OrdenProducto orden,
    Double latUsuario,
    Double lngUsuario
  );
  // Igual que el anterior pero limitado a un rubro (ej. "Lacteos"); sin rubro trae todos
  List<ProductoConPrecio> buscarProductosConPrecio(
    String nombre,
    String categoria,
    Usuario usuario,
    OrdenProducto orden,
    Double latUsuario,
    Double lngUsuario
  );
  List<Reporte> buscarPreciosDelProductoEnCadaLocal(Long reporteId);
}
