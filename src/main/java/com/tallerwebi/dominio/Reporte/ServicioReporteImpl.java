package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CamposObligatoriosVacios;
import com.tallerwebi.dominio.excepcion.PrecioIncorrecto;
import com.tallerwebi.dominio.excepcion.ReporteExistente;
import com.tallerwebi.dominio.excepcion.UnidadInvalida;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
  public Reporte guardarReporte(Reporte reporte)
    throws ReporteExistente, CamposObligatoriosVacios, UnidadInvalida, PrecioIncorrecto {
    validarCamposObligatorios(reporte);
    validarUnidad(reporte.getProducto().getUnidad());
    validarPrecio(reporte.getPrecio());
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
  public Reporte marcarDudoso(Long id, Usuario usuario) {
    return repositorioReporte.marcarDudoso(id, usuario);
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

  private void validarCamposObligatorios(Reporte reporte) throws CamposObligatoriosVacios {
    if (reporte.getProducto() == null || reporte.getComercio() == null) {
      throw new CamposObligatoriosVacios("Todos los campos son obligatorios");
    }
    List<String> campos = Arrays.asList(
      reporte.getProducto().getNombre(),
      reporte.getProducto().getMarca(),
      reporte.getProducto().getCategoria(),
      reporte.getProducto().getUnidad(),
      reporte.getComercio().getNombre(),
      reporte.getComercio().getDireccion(),
      reporte.getComercio().getLocalidad()
    );
    if (campos.stream().anyMatch(v -> v == null || v.trim().isEmpty())) {
      throw new CamposObligatoriosVacios("Todos los campos son obligatorios");
    }
  }

  private void validarUnidad(String unidad) throws UnidadInvalida {
    if (!unidad.matches("^[0-9]+(\\.[0-9]+)?(?i)(g|kg|gr|ml|l)$")) {
      throw new UnidadInvalida(
        "La unidad debe ser un número seguido de g, kg, ml, L o gr (ej: 500g, 1kg, 250ml)"
      );
    }
  }

  private void validarPrecio(Double precio) throws PrecioIncorrecto {
    if (precio == null || precio <= 0) {
      throw new PrecioIncorrecto("El precio debe ser mayor a cero");
    }
  }
}
