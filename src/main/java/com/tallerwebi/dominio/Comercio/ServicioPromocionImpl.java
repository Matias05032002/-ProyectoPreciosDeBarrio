package com.tallerwebi.dominio.Comercio;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioPromocionImpl implements ServicioPromocion {

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

  private void validarTitulo(Promocion promocion) {
    if (promocion.getTitulo() == null || promocion.getTitulo().isBlank()) {
      throw new IllegalArgumentException("El título es obligatorio.");
    }
  }

  private void validarPrecio(Promocion promocion) {
    if (promocion.getPrecioPromocional() == null || promocion.getPrecioPromocional() <= 0) {
      throw new IllegalArgumentException("El precio promocional debe ser mayor a cero.");
    }
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
