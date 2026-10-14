package com.tallerwebi.dominio.Consulta;

import java.util.List;

public interface ServicioConsulta {
  void guardar(Consulta consulta);

  List<Consulta> listarTodas();
}
