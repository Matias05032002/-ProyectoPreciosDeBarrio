package com.tallerwebi.dominio.Asistente;

@FunctionalInterface
public interface ServicioAsistente {
  String consultar(String pregunta, Double latitud, Double longitud) throws Exception;
}
