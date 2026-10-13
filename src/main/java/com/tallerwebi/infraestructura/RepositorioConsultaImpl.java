package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Consulta.Consulta;
import com.tallerwebi.dominio.Consulta.RepositorioConsulta;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioConsulta")
public class RepositorioConsultaImpl implements RepositorioConsulta {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioConsultaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Consulta consulta) {
    sessionFactory.getCurrentSession().persist(consulta);
  }

  @Override
  public List<Consulta> listarTodas() {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Consulta order by fecha desc", Consulta.class)
      .list();
  }
}
