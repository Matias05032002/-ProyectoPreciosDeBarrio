package com.tallerwebi.dominio.Producto;

import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioProducto")
@Transactional
public class ServicioProductoImpl implements ServicioProducto {

  private RepositorioProducto repositorioProducto;
  private RepositorioReporte repositorioReporte;

  @Autowired
  public ServicioProductoImpl(
    RepositorioProducto repositorioProducto,
    RepositorioReporte repositorioReporte
  ) {
    this.repositorioProducto = repositorioProducto;
    this.repositorioReporte = repositorioReporte;
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

  @Override
  public List<ProductoConPrecio> buscarProductosConPrecio(String nombre, Usuario usuario) {
    List<Reporte> reportes = repositorioReporte.buscarPorNombre(nombre);
    if (reportes == null) reportes = Collections.emptyList();

    List<ProductoConPrecio> resultado = new ArrayList<>();
    for (Reporte reporte : reportes) {
      Producto producto = reporte.getProducto();
      if (producto == null) continue;

      String nombreComercio = reporte.getComercio() != null
        ? reporte.getComercio().getNombre()
        : null;
      ProductoConPrecio dto = new ProductoConPrecio(
        producto.getNombre(),
        producto.getMarca(),
        producto.getUnidad(),
        producto.getCategoria(),
        reporte.getPrecio(),
        nombreComercio,
        reporte.getId(),
        reporte.getFechaDeReporte(),
        reporte.getPuntuacion()
      );
      if (usuario != null) {
        dto.setYaMarcoDudoso(reporte.getUsuariosQueMarcaron().contains(usuario));
      }
      resultado.add(dto);
    }
    resultado.sort(Comparator.comparingDouble(ProductoConPrecio::getPrecioMinimo));
    return resultado;
  }
}
