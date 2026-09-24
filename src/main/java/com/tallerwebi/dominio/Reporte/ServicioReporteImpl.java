package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.excepcion.ReporteExistente;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service("servicioReporte")
@Transactional
public class ServicioReporteImpl implements ServicioReporte {

  private RepositorioReporte repositorioReporte;
  private ServicioComercio servicioComercio;
  private ServicioProducto servicioProducto;

  @Autowired
  public ServicioReporteImpl(
    ServicioComercio servicioComercio,
    ServicioProducto servicioProducto,
    RepositorioReporte repositorioReporte
  ) {
    this.servicioComercio = servicioComercio;
    this.servicioProducto = servicioProducto;
    this.repositorioReporte = repositorioReporte;
  }

  @Override
  public Reporte guardarReporte(Reporte reporte) throws ReporteExistente {
    Producto productoExistente = servicioProducto.buscarProductoPorNombreExacto(
      reporte.getProducto().getNombre()
    );
    if (productoExistente == null) {
      servicioProducto.guardarProducto(reporte.getProducto());
    } else {
      reporte.setProducto(productoExistente);
    }

    Comercio comercioExistente = servicioComercio.buscarComercioPorNombre(
      reporte.getComercio().getNombre()
    );
    if (comercioExistente == null) {
      servicioComercio.guardarComercio(reporte.getComercio());
    } else {
      reporte.setComercio(comercioExistente);
    }

    reporte.setFechaDeReporte(LocalDateTime.now());

    if (reporte.getUsuario() != null) {
      if (
        repositorioReporte.buscarReporteDuplicado(
          reporte.getUsuario().getId(),
          reporte.getProducto().getId(),
          reporte.getComercio().getId(),
          reporte.getFechaDeReporte().toLocalDate()
        ) !=
        null
      ) {
        throw new ReporteExistente();
      }
    }

    return repositorioReporte.guardarReporte(reporte);
  }

  @Override
  public Reporte buscarReporte(Long id) {
    return repositorioReporte.buscarReporte(id);
  }

  @Override
  public List<Reporte> buscarPorPorducto(Long productoId) {
    return repositorioReporte.buscarPorPorducto(productoId);
  }

  @Override
  public Reporte marcarDudoso(Long id) {
    return repositorioReporte.marcarDudoso(id);
  }

  @Override
  public List<Reporte> listarTodos() {
    return repositorioReporte.listarTodos();
  }

  @Override
  public Reporte buscarReporteDuplicado(
    Long usuarioId,
    Long productoId,
    Long comercioId,
    LocalDate fecha
  ) {
    return repositorioReporte.buscarReporteDuplicado(usuarioId, productoId, comercioId, fecha);
  }

  @Override
  public List<Reporte> buscarPorNombre(String nombreProducto) {
    return repositorioReporte.buscarPorNombre(nombreProducto);
  }
}
