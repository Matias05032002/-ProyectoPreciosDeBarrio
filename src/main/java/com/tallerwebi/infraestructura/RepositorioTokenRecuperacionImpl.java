package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Recuperacion.RepositorioTokenRecuperacion;
import com.tallerwebi.dominio.Recuperacion.TokenRecuperacion;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioTokenRecuperacion")
public class RepositorioTokenRecuperacionImpl implements RepositorioTokenRecuperacion {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioTokenRecuperacionImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(TokenRecuperacion token) {
    sessionFactory.getCurrentSession().persist(token);
  }

  @Override
  public TokenRecuperacion buscarPorHash(String tokenHash) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("FROM TokenRecuperacion t WHERE t.tokenHash = :hash", TokenRecuperacion.class)
      .setParameter("hash", tokenHash)
      .uniqueResult();
  }

  @Override
  public List<TokenRecuperacion> buscarVigentesDelUsuario(Long usuarioId, LocalDateTime ahora) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM TokenRecuperacion t WHERE t.usuarioId = :usuarioId " +
        "AND t.usado = false AND t.fechaVencimiento > :ahora " +
        "ORDER BY t.fechaCreacion DESC",
        TokenRecuperacion.class
      )
      .setParameter("usuarioId", usuarioId)
      .setParameter("ahora", ahora)
      .list();
  }

  @Override
  public void actualizar(TokenRecuperacion token) {
    sessionFactory.getCurrentSession().merge(token);
  }
}
