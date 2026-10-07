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
  public List<ProductoConPrecio> buscarProductosConPrecio(
    String nombre,
    Usuario usuario,
    OrdenProducto orden,
    Double latUsuario,
    Double lngUsuario
  ) {
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
      asignarDistancia(dto, reporte, latUsuario, lngUsuario);
      resultado.add(dto);
    }

    if (OrdenProducto.CERCANIA.equals(orden)) {
      resultado.sort(
        Comparator.comparing(
          ProductoConPrecio::getDistanciaKm,
          Comparator.nullsLast(Comparator.naturalOrder())
        )
      );
    } else if (OrdenProducto.RECIENTE.equals(orden)) {
      resultado.sort(
        Comparator.comparing(
          ProductoConPrecio::getFechaDeReporte,
          Comparator.nullsLast(Comparator.reverseOrder())
        )
      );
    } else {
      resultado.sort(Comparator.comparingDouble(ProductoConPrecio::getPrecioMinimo));
    }

    return resultado;
  }

  private void asignarDistancia(
    ProductoConPrecio dto,
    Reporte reporte,
    Double latUsuario,
    Double lngUsuario
  ) {
    if (latUsuario == null || lngUsuario == null || reporte.getComercio() == null) {
      return;
    }
    Double latC = reporte.getComercio().getLatitud();
    Double lngC = reporte.getComercio().getLongitud();
    if (latC != null && lngC != null) {
      dto.setDistanciaKm(haversine(latUsuario, lngUsuario, latC, lngC));
    }
  }

  private double haversine(double lat1, double lon1, double lat2, double lon2) {
    final int radioTierraKm = 6371;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double sinLat = Math.sin(dLat / 2);
    double sinLon = Math.sin(dLon / 2);
    double haversineA =
      sinLat * sinLat +
      Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * sinLon * sinLon;
    return radioTierraKm * 2 * Math.atan2(Math.sqrt(haversineA), Math.sqrt(1 - haversineA));
  }
}
