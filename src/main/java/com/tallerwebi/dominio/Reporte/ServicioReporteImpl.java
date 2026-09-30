package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.excepcion.ReporteExistente;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    if (
      productoExistente == null ||
      !Objects.equals(productoExistente.getMarca(), reporte.getProducto().getMarca())
    ) {
      servicioProducto.guardarProducto(reporte.getProducto());
    } else {
      reporte.setProducto(productoExistente);
    }

    List<Comercio> comerciosExistentes = servicioComercio.buscarComercioPorNombre(
      reporte.getComercio().getNombre()
    );
    if (comerciosExistentes == null || comerciosExistentes.isEmpty()) {
      servicioComercio.guardarComercio(reporte.getComercio());
    } else {
      reporte.setComercio(comerciosExistentes.get(0));
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
  public List<Reporte> buscarPorProducto(Long productoId) {
    return repositorioReporte.buscarPorProducto(productoId);
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
  @Override
  public List<Reporte> buscarPorComercio(Long comercioId) {
    return repositorioReporte.buscarPorComercio(comercioId);
  }
}
