package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.notificacion.Notificacion;
import com.tallerwebi.dominio.notificacion.RepositorioNotificacion;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioNotificacion")
public class RepositorioNotificacionImpl implements RepositorioNotificacion {

  private static final String USUARIO_ID = "usuarioId";

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioNotificacionImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Notificacion notificacion) {
    sessionFactory.getCurrentSession().persist(notificacion);
  }

  @Override
  public Notificacion buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Notificacion.class, id);
  }

  @Override
  public boolean existe(Long usuarioId, String clave) {
    Long cantidad = sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT COUNT(n) FROM Notificacion n WHERE n.usuarioId = :usuarioId AND n.clave = :clave",
        Long.class
      )
      .setParameter(USUARIO_ID, usuarioId)
      .setParameter("clave", clave)
      .uniqueResult();
    return cantidad != null && cantidad > 0;
  }

  @Override
  public List<Notificacion> listarDelUsuario(Long usuarioId, int limite) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM Notificacion n WHERE n.usuarioId = :usuarioId ORDER BY n.fechaCreacion DESC, n.id DESC",
        Notificacion.class
      )
      .setParameter(USUARIO_ID, usuarioId)
      .setMaxResults(limite)
      .list();
  }

  @Override
  public long contarNoLeidas(Long usuarioId) {
    Long cantidad = sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT COUNT(n) FROM Notificacion n WHERE n.usuarioId = :usuarioId AND n.leida = false",
        Long.class
      )
      .setParameter(USUARIO_ID, usuarioId)
      .uniqueResult();
    return cantidad == null ? 0 : cantidad;
  }

  @Override
  public int marcarComoLeida(Long id, Long usuarioId) {
    return sessionFactory
      .getCurrentSession()
      .createMutationQuery(
        "UPDATE Notificacion n SET n.leida = true WHERE n.id = :id AND n.usuarioId = :usuarioId"
      )
      .setParameter("id", id)
      .setParameter(USUARIO_ID, usuarioId)
      .executeUpdate();
  }

  @Override
  public int marcarTodasComoLeidas(Long usuarioId) {
    return sessionFactory
      .getCurrentSession()
      .createMutationQuery(
        "UPDATE Notificacion n SET n.leida = true WHERE n.usuarioId = :usuarioId AND n.leida = false"
      )
      .setParameter(USUARIO_ID, usuarioId)
      .executeUpdate();
  }
}
