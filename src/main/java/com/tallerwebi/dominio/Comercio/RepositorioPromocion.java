package com.tallerwebi.dominio.Comercio;

import java.time.LocalDateTime;
import java.util.List;

public interface RepositorioPromocion {
  Promocion guardar(Promocion promocion);

  Promocion buscarPorId(Long id);

  List<Promocion> listarPorComercio(Long comercioId);

  List<Promocion> listarActivas();

  List<Promocion> listarProximas();

  List<Promocion> listarIniciadasEntre(LocalDateTime desde, LocalDateTime hasta);

  void actualizar(Promocion promocion);
}
