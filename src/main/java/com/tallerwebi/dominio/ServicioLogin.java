package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;

public interface ServicioLogin {
  Usuario consultarUsuario(String email, String password);

  /** Indica si el email está bloqueado temporalmente por demasiados intentos fallidos. */
  boolean estaBloqueado(String email);

  void registrar(Usuario usuario) throws UsuarioExistente, EmailInvalido, ContrasenaInvalida;
  Usuario buscarPorEmail(String email);

  boolean actualizarEmail(Long usuarioId, String nuevoEmail);
  boolean cambiarContrasena(Long usuarioId, String contrasenaActual, String contrasenaNueva);
  void restablecerContrasena(Long usuarioId, String contrasenaNueva) throws ContrasenaInvalida;
  Usuario buscarPorId(Long id);
}
