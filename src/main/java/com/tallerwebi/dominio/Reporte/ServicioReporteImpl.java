package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.*;
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
  private static final int DIAS_PLAZO_EDICION = 2;

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
    reporte.getProducto().setNombre(reporte.getProducto().getNombre().trim());
    reporte.getProducto().setMarca(reporte.getProducto().getMarca().trim());
    reporte.getProducto().setCategoria(reporte.getProducto().getCategoria().trim());
    reporte.getProducto().setUnidad(reporte.getProducto().getUnidad().trim());
    reporte.getComercio().setNombre(reporte.getComercio().getNombre().trim());
    reporte.getComercio().setDireccion(reporte.getComercio().getDireccion().trim());
    reporte.getComercio().setLocalidad(reporte.getComercio().getLocalidad().trim());
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
    Comercio comercioExistente = null;

    if (comerciosExistentes != null) {
      comercioExistente =
        comerciosExistentes
          .stream()
          .filter(comercio ->
            comercio.getDireccion() != null &&
            reporte.getComercio().getDireccion() != null &&
            comercio
              .getDireccion()
              .trim()
              .equalsIgnoreCase(reporte.getComercio().getDireccion().trim()) &&
            comercio.getLocalidad() != null &&
            reporte.getComercio().getLocalidad() != null &&
            comercio
              .getLocalidad()
              .trim()
              .equalsIgnoreCase(reporte.getComercio().getLocalidad().trim())
          )
          .findFirst()
          .orElse(null);
    }

    if (comercioExistente == null) {
      Comercio comercioGuardado = servicioComercio.guardarComercio(reporte.getComercio());
      reporte.setComercio(comercioGuardado);
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
  public List<Reporte> buscarPorProducto(Long productoId) {
    return repositorioReporte.buscarPorProducto(productoId);
  }

  @Override
  public Reporte marcarDudoso(Long id, Usuario usuario) {
    Reporte reporte = repositorioReporte.buscarReporte(id);
    if (reporte == null) {
      return null;
    }
    // No puede marcar su propio reporte
    if (
      reporte.getUsuario() != null &&
      usuario != null &&
      reporte.getUsuario().getId().equals(usuario.getId())
    ) {
      return reporte;
    }
    // No puede marcar dos veces el mismo reporte
    if (usuario != null) {
      boolean yaMarco = reporte
        .getUsuariosQueMarcaron()
        .stream()
        .anyMatch(u -> u.getId() != null && u.getId().equals(usuario.getId()));
      if (yaMarco) {
        return reporte;
      }
    }
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

  @Override
  public List<Reporte> buscarPorUsuarioId(Long usuarioId) {
    return repositorioReporte.buscarPorUsuarioId(usuarioId);
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
    if (Math.round(precio * 100) != (long) (precio * 100)) {
      throw new PrecioIncorrecto("El precio no puede tener más de 2 decimales");
    }
  }

  @Override
  public boolean puedeEditar(Long reporteId, Long usuarioId) {
    Reporte reporte = repositorioReporte.buscarReporte(reporteId);
    if (reporte == null || reporte.getUsuario() == null) return false;
    if (!reporte.getUsuario().getId().equals(usuarioId)) return false;
    if (reporte.getFechaDeReporte() == null) return false;
    return reporte.getFechaDeReporte().isAfter(LocalDateTime.now().minusDays(DIAS_PLAZO_EDICION));
  }

  @Override
  public void editarReporte(Long id, Reporte datos, Long usuarioId)
    throws CamposObligatoriosVacios, UnidadInvalida, PrecioIncorrecto, FueraDePlazoDeEdicion {
    Reporte reporte = repositorioReporte.buscarReporte(id);
    validarPermisoEdicion(reporte, usuarioId);
    validarCamposObligatorios(datos);
    validarUnidad(datos.getProducto().getUnidad());
    validarPrecio(datos.getPrecio());
    reporte.getProducto().setNombre(datos.getProducto().getNombre());
    reporte.getProducto().setMarca(datos.getProducto().getMarca());
    reporte.getProducto().setCategoria(datos.getProducto().getCategoria());
    reporte.getProducto().setUnidad(datos.getProducto().getUnidad());
    reporte.getComercio().setNombre(datos.getComercio().getNombre());
    reporte.getComercio().setDireccion(datos.getComercio().getDireccion());
    reporte.getComercio().setLocalidad(datos.getComercio().getLocalidad());
    reporte.setPrecio(datos.getPrecio());
    if (datos.getFoto() != null) {
      reporte.setFoto(datos.getFoto());
    }
    repositorioReporte.guardarReporte(reporte);
  }

  private void validarPermisoEdicion(Reporte reporte, Long usuarioId) {
    if (reporte == null || reporte.getUsuario() == null) {
      throw new FueraDePlazoDeEdicion("El reporte no existe o no tiene usuario asignado");
    }
    if (!reporte.getUsuario().getId().equals(usuarioId)) {
      throw new FueraDePlazoDeEdicion("No tenés permiso para editar este reporte");
    }
    if (
      reporte.getFechaDeReporte() == null ||
      reporte.getFechaDeReporte().isBefore(LocalDateTime.now().minusDays(DIAS_PLAZO_EDICION))
    ) {
      throw new FueraDePlazoDeEdicion(
        "El reporte ya no puede editarse porque superó el plazo de " + DIAS_PLAZO_EDICION + " días"
      );
    }
  }

  @Override
  public List<Object[]> contarUsuariosDistintosPorComercio(Long comercioId) {
    return repositorioReporte.contarUsuariosDistintosPorComercio(comercioId);
  }

  @Override
  public Long contarReportesDudososPorComercio(Long comercioId) {
    return repositorioReporte.contarReportesDudososPorComercio(comercioId);
  }

  @Override
  public List<Object[]> rangoEtariosVisitantes(Long comercioId) {
    return repositorioReporte.rangoEtariosVisitantes(comercioId);
  }
}
