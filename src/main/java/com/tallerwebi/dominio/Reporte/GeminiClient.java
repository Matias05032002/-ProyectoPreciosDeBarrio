package com.tallerwebi.dominio.Reporte;

@FunctionalInterface
public interface GeminiClient {
  String llamar(String requestBody) throws Exception;
}
