package com.tallerwebi.dominio.Comercio;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioPromocionImpl implements ServicioPromocion {

  private static final double PRECIO_MAXIMO = 10_000_000.0;
  private static final int LARGO_MAXIMO_TITULO = 100;
  private static final int LARGO_MAXIMO_DESCRIPCION = 500;

  private final RepositorioPromocion repositorioPromocion;
  private final RepositorioComercio repositorioComercio;

  @Autowired
  public ServicioPromocionImpl(
    RepositorioPromocion repositorioPromocion,
    RepositorioComercio repositorioComercio
  ) {
    this.repositorioPromocion = repositorioPromocion;
    this.repositorioComercio = repositorioComercio;
  }

  @Override
  public Promocion crearPromocion(Promocion promocion, Long usuarioId) {
    Comercio comercio = repositorioComercio.buscarComercioPorUsuarioId(usuarioId);

    if (comercio == null) {
      throw new IllegalArgumentException("El usuario no tiene un comercio asociado.");
    }

    validarPromocion(promocion);

    // El id nunca viene del formulario: si llegara uno, el guardado pisaría otra promoción
    promocion.setId(null);
    normalizarTextos(promocion);
    promocion.setComercio(comercio);
    promocion.setActiva(true);

    return repositorioPromocion.guardar(promocion);
  }

  @Override
  @Transactional(readOnly = true)
  public Promocion buscarPorId(Long id) {
    return repositorioPromocion.buscarPorId(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Promocion> listarPorComercio(Long comercioId) {
    return repositorioPromocion.listarPorComercio(comercioId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Promocion> listarPromocionesActivas() {
    return repositorioPromocion.listarActivas();
  }

  @Override
  public void actualizarPromocion(Promocion promocion, Long usuarioId) {
    Promocion existente = repositorioPromocion.buscarPorId(promocion.getId());

    validarPropietario(existente, usuarioId);
    validarPromocion(promocion);
    normalizarTextos(promocion);

    existente.setTitulo(promocion.getTitulo());
    existente.setDescripcion(promocion.getDescripcion());
    existente.setPrecioPromocional(promocion.getPrecioPromocional());
    existente.setFechaInicio(promocion.getFechaInicio());
    existente.setFechaFin(promocion.getFechaFin());

    repositorioPromocion.actualizar(existente);
  }

  @Override
  public void desactivarPromocion(Long promocionId, Long usuarioId) {
    Promocion promocion = repositorioPromocion.buscarPorId(promocionId);

    validarPropietario(promocion, usuarioId);

    promocion.setActiva(false);
    repositorioPromocion.actualizar(promocion);
  }

  private void validarPropietario(Promocion promocion, Long usuarioId) {
    if (promocion == null) {
      throw new IllegalArgumentException("La promoción no existe.");
    }

    Comercio comercio = repositorioComercio.buscarComercioPorUsuarioId(usuarioId);

    if (comercio == null || !comercio.getId().equals(promocion.getComercio().getId())) {
      throw new IllegalArgumentException("No tenés permiso para modificar esta promoción.");
    }
  }

  private void validarPromocion(Promocion promocion) {
    validarTitulo(promocion);
    validarPrecio(promocion);
    validarFechas(promocion);
  }

  private void normalizarTextos(Promocion promocion) {
    promocion.setTitulo(promocion.getTitulo().trim());
    if (promocion.getDescripcion() != null) {
      String descripcion = promocion.getDescripcion().trim();
      promocion.setDescripcion(descripcion.isEmpty() ? null : descripcion);
    }
  }

  private void validarTitulo(Promocion promocion) {
    if (promocion.getTitulo() == null || promocion.getTitulo().isBlank()) {
      throw new IllegalArgumentException("El título es obligatorio.");
    }
    if (promocion.getTitulo().trim().length() > LARGO_MAXIMO_TITULO) {
      throw new IllegalArgumentException(
        "El título no puede superar los " + LARGO_MAXIMO_TITULO + " caracteres."
      );
    }
    if (
      promocion.getDescripcion() != null &&
      promocion.getDescripcion().trim().length() > LARGO_MAXIMO_DESCRIPCION
    ) {
      throw new IllegalArgumentException(
        "La descripción no puede superar los " + LARGO_MAXIMO_DESCRIPCION + " caracteres."
      );
    }
  }

  private void validarPrecio(Promocion promocion) {
    Double precio = promocion.getPrecioPromocional();
    if (!esPrecioPositivo(precio)) {
      throw new IllegalArgumentException("El precio promocional debe ser mayor a cero.");
    }
    if (precio > PRECIO_MAXIMO) {
      throw new IllegalArgumentException("El precio promocional no puede superar $10.000.000.");
    }
    if (Math.round(precio * 100) != (long) (precio * 100)) {
      throw new IllegalArgumentException(
        "El precio promocional no puede tener más de 2 decimales."
      );
    }
  }

  // NaN e infinito no son <= 0 pero tampoco son precios: se rechazan junto con el nulo
  private boolean esPrecioPositivo(Double precio) {
    return precio != null && !precio.isNaN() && !precio.isInfinite() && precio > 0;
  }

  private void validarFechas(Promocion promocion) {
    if (promocion.getFechaInicio() == null || promocion.getFechaFin() == null) {
      throw new IllegalArgumentException("Las fechas de inicio y finalización son obligatorias.");
    }

    if (!promocion.getFechaFin().isAfter(promocion.getFechaInicio())) {
      throw new IllegalArgumentException("La fecha de finalización debe ser posterior al inicio.");
    }

    if (promocion.getFechaFin().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("La fecha de finalización no puede estar en el pasado.");
    }
  }
}
