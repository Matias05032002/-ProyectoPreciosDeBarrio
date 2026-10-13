package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.RepositorioComercio;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioComercioImpl implements RepositorioComercio {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioComercioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Comercio guardarComercio(Comercio comercio) {
    sessionFactory.getCurrentSession().persist(comercio);
    return comercio;
  }

  @Override
  public List<Comercio> listarTodos() {
    return sessionFactory.getCurrentSession().createQuery("from Comercio", Comercio.class).list();
  }

  @Override
  public Comercio buscarComercio(Long id) {
    return sessionFactory.getCurrentSession().get(Comercio.class, id);
  }

  @Override
  public List<Comercio> buscarComercioPorNombre(String nombre) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Comercio where nombre like :nombre", Comercio.class)
      .setParameter("nombre", "%" + nombre + "%")
      .list();
  }

  @Override
  public Comercio buscarComercioPorUsuarioId(Long usuarioId) {
    return (Comercio) sessionFactory
      .getCurrentSession()
      .createQuery("FROM Comercio WHERE usuarioId = :usuarioId")
      .setParameter("usuarioId", usuarioId)
      .uniqueResult();
  }

  @Override
  public Comercio buscarComercioPorNombreDireccionYLocalidad(
    String nombre,
    String direccion,
    String localidad
  ) {
    List<Comercio> comercios = sessionFactory
      .getCurrentSession()
      .createQuery(
        "from Comercio where lower(nombre) = lower(:nombre) " +
        "and lower(direccion) = lower(:direccion) " +
        "and lower(localidad) = lower(:localidad)",
        Comercio.class
      )
      .setParameter("nombre", nombre.trim())
      .setParameter("direccion", direccion.trim())
      .setParameter("localidad", localidad.trim())
      .list();

    return comercios.isEmpty() ? null : comercios.get(0);
  }
}
