package com.tallerwebi.dominio.Recuperacion;

import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;

public interface ServicioRecuperacionContrasena {
  void solicitarRecuperacion(String email);
  boolean esTokenValido(String token);
  void restablecerContrasena(String token, String contrasenaNueva, String confirmacion)
    throws ContrasenaInvalida;
}
