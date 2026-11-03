package com.tallerwebi.dominio.Email;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface ServicioEmail {
  void enviar(String destinatario, String asunto, String cuerpo);
}
