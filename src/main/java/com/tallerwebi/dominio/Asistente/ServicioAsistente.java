package com.tallerwebi.dominio.Asistente;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface ServicioAsistente {
  String consultar(
    String pregunta,
    Double latitud,
    Double longitud,
    List<Map<String, String>> historial
  ) throws Exception;
}
