package com.tallerwebi.dominio.Comercio;

import java.util.List;

public interface RepositorioHorario {
  void guardarHorarios(List<HorarioComercio> horarios);
  List<HorarioComercio> buscarPorComercioId(Long comercioId);
  void eliminarPorComercioId(Long comercioId);
}
