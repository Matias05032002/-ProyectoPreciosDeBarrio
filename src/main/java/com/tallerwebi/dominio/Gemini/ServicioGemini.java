package com.tallerwebi.dominio.Gemini;

@FunctionalInterface
public interface ServicioGemini {
  String llamar(String requestBody) throws Exception;
}
