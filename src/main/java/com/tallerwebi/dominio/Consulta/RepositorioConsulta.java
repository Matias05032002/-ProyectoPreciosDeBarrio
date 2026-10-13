package com.tallerwebi.dominio.Consulta;

import java.util.List;

public interface RepositorioConsulta {
  void guardar(Consulta consulta);

  List<Consulta> listarTodas();
}
