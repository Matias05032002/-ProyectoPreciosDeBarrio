package com.tallerwebi.dominio.Comercio;

import java.util.List;

public interface RepositorioPromocion {
  Promocion guardar(Promocion promocion);

  Promocion buscarPorId(Long id);

  List<Promocion> listarPorComercio(Long comercioId);

  List<Promocion> listarActivas();

  List<Promocion> listarProximas();

  void actualizar(Promocion promocion);
}
