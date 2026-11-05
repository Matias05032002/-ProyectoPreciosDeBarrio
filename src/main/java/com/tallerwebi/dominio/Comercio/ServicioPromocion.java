package com.tallerwebi.dominio.Comercio;

import java.util.List;

public interface ServicioPromocion {
  Promocion crearPromocion(Promocion promocion, Long usuarioId);

  Promocion buscarPorId(Long id);

  List<Promocion> listarPorComercio(Long comercioId);

  List<Promocion> listarPromocionesActivas();

  List<Promocion> listarPromocionesProximas();

  void actualizarPromocion(Promocion promocion, Long usuarioId);

  void desactivarPromocion(Long promocionId, Long usuarioId);
}
