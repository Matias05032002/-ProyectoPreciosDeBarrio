package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.RepositorioPromocion;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPromocionImpl implements RepositorioPromocion {

  private static final String SELECT_CON_COMERCIO =
    "SELECT p FROM Promocion p JOIN FETCH p.comercio ";

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioPromocionImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Promocion guardar(Promocion promocion) {
    sessionFactory.getCurrentSession().persist(promocion);
    return promocion;
  }

  @Override
  public Promocion buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Promocion.class, id);
  }

  @Override
  public List<Promocion> listarPorComercio(Long comercioId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        SELECT_CON_COMERCIO + "WHERE p.comercio.id = :comercioId ORDER BY p.fechaFin DESC",
        Promocion.class
      )
      .setParameter("comercioId", comercioId)
      .list();
  }

  @Override
  public List<Promocion> listarProximas() {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        SELECT_CON_COMERCIO +
        "WHERE p.activa = true " +
        "AND p.fechaInicio > :ahora " +
        "ORDER BY p.fechaInicio ASC",
        Promocion.class
      )
      .setParameter("ahora", LocalDateTime.now())
      .list();
  }

  @Override
  public List<Promocion> listarActivas() {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        SELECT_CON_COMERCIO +
        "WHERE p.activa = true " +
        "AND p.fechaInicio <= :ahora " +
        "AND p.fechaFin >= :ahora " +
        "ORDER BY p.fechaFin ASC",
        Promocion.class
      )
      .setParameter("ahora", LocalDateTime.now())
      .list();
  }

  @Override
  public void actualizar(Promocion promocion) {
    sessionFactory.getCurrentSession().merge(promocion);
  }

  @Override
  public List<Promocion> listarIniciadasEntre(LocalDateTime desde, LocalDateTime hasta) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        SELECT_CON_COMERCIO +
        "WHERE p.activa = true AND p.fechaInicio > :desde AND p.fechaInicio <= :hasta " +
        "AND p.fechaFin > :hasta ORDER BY p.fechaInicio ASC",
        Promocion.class
      )
      .setParameter("desde", desde)
      .setParameter("hasta", hasta)
      .list();
  }
}
