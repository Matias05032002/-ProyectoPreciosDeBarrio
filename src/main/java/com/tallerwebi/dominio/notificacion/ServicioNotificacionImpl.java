package com.tallerwebi.dominio.notificacion;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioNotificacionImpl implements ServicioNotificacion {

  static final int MAXIMO_A_LISTAR = 50;

  private final RepositorioNotificacion repositorio;

  @Autowired
  public ServicioNotificacionImpl(RepositorioNotificacion repositorio) {
    this.repositorio = repositorio;
  }

  @Override
  public boolean notificar(
    Long usuarioId,
    TipoNotificacion tipo,
    String mensaje,
    String enlace,
    String clave
  ) {
    if (usuarioId == null || tipo == null || mensaje == null || mensaje.isBlank()) {
      return false;
    }
    if (clave != null && repositorio.existe(usuarioId, clave)) {
      return false;
    }
    Notificacion notificacion = new Notificacion();
    notificacion.setUsuarioId(usuarioId);
    notificacion.setTipo(tipo);
    notificacion.setMensaje(recortar(mensaje.trim()));
    notificacion.setEnlace(enlaceInterno(enlace));
    notificacion.setClave(clave);
    notificacion.setFechaCreacion(LocalDateTime.now());
    repositorio.guardar(notificacion);
    return true;
  }

  @Override
  public List<Notificacion> listar(Long usuarioId) {
    return repositorio.listarDelUsuario(usuarioId, MAXIMO_A_LISTAR);
  }

  @Override
  public long contarNoLeidas(Long usuarioId) {
    return repositorio.contarNoLeidas(usuarioId);
  }

  @Override
  public boolean marcarComoLeida(Long id, Long usuarioId) {
    return repositorio.marcarComoLeida(id, usuarioId) > 0;
  }

  @Override
  public void marcarTodasComoLeidas(Long usuarioId) {
    repositorio.marcarTodasComoLeidas(usuarioId);
  }

  @Override
  public String enlaceDe(Long id, Long usuarioId) {
    Notificacion notificacion = repositorio.buscarPorId(id);
    if (notificacion == null || !usuarioId.equals(notificacion.getUsuarioId())) {
      return null;
    }
    return notificacion.getEnlace();
  }

  private String recortar(String mensaje) {
    return mensaje.length() > Notificacion.LARGO_MAXIMO_MENSAJE
      ? mensaje.substring(0, Notificacion.LARGO_MAXIMO_MENSAJE)
      : mensaje;
  }

  // Solo rutas de la propia app: un enlace externo se descarta para que nadie use el aviso de
  // trampolín hacia otro sitio
  private String enlaceInterno(String enlace) {
    if (enlace == null || !enlace.startsWith("/") || enlace.startsWith("//")) {
      return null;
    }
    return enlace;
  }
}
