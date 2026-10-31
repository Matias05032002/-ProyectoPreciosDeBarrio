package com.tallerwebi.dominio.Gemini;

public interface ServicioGemini {
  String llamar(String requestBody) throws Exception;
  String llamar(String systemPrompt, String userPrompt) throws Exception;
}
