package com.tallerwebi.dominio.notificacion;

import java.util.List;

public interface RepositorioNotificacion {
  void guardar(Notificacion notificacion);

  Notificacion buscarPorId(Long id);

  boolean existe(Long usuarioId, String clave);

  List<Notificacion> listarDelUsuario(Long usuarioId, int limite);

  long contarNoLeidas(Long usuarioId);

  // Devuelve cuántas filas cambió: 0 si no existe o es de otro usuario
  int marcarComoLeida(Long id, Long usuarioId);

  int marcarTodasComoLeidas(Long usuarioId);
}
