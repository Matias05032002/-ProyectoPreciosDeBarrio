package com.tallerwebi.dominio.Asistente;

import com.tallerwebi.dominio.excepcion.LimiteDeConsultasExcedido;

@FunctionalInterface
public interface ServicioLimiteConsultas {
  // Anota una consulta de quien tiene esa clave; si ya agotó su cupo, lanza la excepción
  void registrarConsulta(String clave) throws LimiteDeConsultasExcedido;
}
