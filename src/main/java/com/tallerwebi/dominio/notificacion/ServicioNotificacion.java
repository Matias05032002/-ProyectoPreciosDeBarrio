package com.tallerwebi.dominio.notificacion;

import java.util.List;

public interface ServicioNotificacion {
  /**
   * Le deja un aviso a un usuario. Devuelve false si no se creó: faltan datos o ya existía un aviso
   * con la misma clave para ese usuario.
   */
  boolean notificar(
    Long usuarioId,
    TipoNotificacion tipo,
    String mensaje,
    String enlace,
    String clave
  );

  List<Notificacion> listar(Long usuarioId);

  long contarNoLeidas(Long usuarioId);

  boolean marcarComoLeida(Long id, Long usuarioId);

  void marcarTodasComoLeidas(Long usuarioId);

  // El enlace de una notificación, solo si es del usuario; null en cualquier otro caso
  String enlaceDe(Long id, Long usuarioId);
}
