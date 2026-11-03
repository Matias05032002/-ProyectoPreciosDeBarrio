package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Comercio.HorarioComercio;
import com.tallerwebi.dominio.Comercio.RepositorioHorario;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("RepositorioHorario")
public class RepositorioHorarioImpl implements RepositorioHorario {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioHorarioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardarHorarios(List<HorarioComercio> horarios) {
    for (HorarioComercio h : horarios) {
      sessionFactory.getCurrentSession().saveOrUpdate(h);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<HorarioComercio> buscarPorComercioId(Long comercioId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("FROM HorarioComercio WHERE comercioId = :cid ORDER BY diaSemana")
      .setParameter("cid", comercioId)
      .list();
  }

  @Override
  public void eliminarPorComercioId(Long comercioId) {
    sessionFactory
      .getCurrentSession()
      .createQuery("DELETE FROM HorarioComercio WHERE comercioId = :cid")
      .setParameter("cid", comercioId)
      .executeUpdate();
  }
}
