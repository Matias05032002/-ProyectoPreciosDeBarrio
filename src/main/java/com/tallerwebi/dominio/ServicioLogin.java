package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.FotoInvalida;
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

  boolean actualizarUbicacion(Long usuarioId, String direccion, String localidad);

  /** Guarda la foto de perfil; lanza FotoInvalida si no es JPG/PNG/WebP o pesa más de 5 MB. */
  void guardarFotoPerfil(Long usuarioId, byte[] foto) throws FotoInvalida;
}
