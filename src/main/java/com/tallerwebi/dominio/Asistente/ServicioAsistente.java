package com.tallerwebi.dominio.Asistente;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface ServicioAsistente {
  String consultar(
    String pregunta,
    Double latitud,
    Double longitud,
    Long usuarioId,
    List<Map<String, String>> historial
  ) throws Exception;
}
