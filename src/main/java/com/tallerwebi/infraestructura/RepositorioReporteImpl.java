package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioReporteImpl implements RepositorioReporte {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioReporteImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Reporte guardarReporte(Reporte reporte) {
    sessionFactory.getCurrentSession().persist(reporte);
    return reporte;
  }

  @Override
  public Reporte buscarReporte(Long id) {
    return sessionFactory.getCurrentSession().get(Reporte.class, id);
  }

  @Override
  public List<Reporte> buscarPorProducto(Long productoId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "from Reporte where producto.id = :productoId order by precio asc",
        Reporte.class
      )
      .setParameter("productoId", productoId)
      .list();
  }

  @Override
  public List<Reporte> buscarPorNombre(String nombreProducto) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "from Reporte where producto.nombre like :nombreProducto order by precio asc",
        Reporte.class
      )
      .setParameter("nombreProducto", "%" + nombreProducto + "%")
      .list();
  }

  @Override
  public Reporte marcarDudoso(Long id) {
    Reporte reporte = sessionFactory.getCurrentSession().get(Reporte.class, id);
    if (reporte != null) {
      int puntuacionActual = reporte.getPuntuacion() != null ? reporte.getPuntuacion() : 0;
      reporte.setPuntuacion(puntuacionActual + 1);
      sessionFactory.getCurrentSession().merge(reporte);
    }
    return reporte;
  }

  @Override
  public List<Reporte> listarTodos() {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Reporte order by precio asc", Reporte.class)
      .list();
  }

  @Override
  public Reporte buscarReporteDuplicado(
    Long usuarioId,
    Long productoId,
    Long comercioId,
    LocalDate fecha
  ) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "from Reporte where usuario.id = :usuarioId " +
        "and producto.id = :productoId " +
        "and comercio.id = :comercioId " +
        "and cast(fechaDeReporte as date) = :fecha",
        Reporte.class
      )
      .setParameter("usuarioId", usuarioId)
      .setParameter("productoId", productoId)
      .setParameter("comercioId", comercioId)
      .setParameter("fecha", fecha)
      .uniqueResult();
  }
   @Override
  public List<Reporte> buscarPorComercio(Long comercioId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "from Reporte where comercio.id = :comercioId order by precio asc",
        Reporte.class
      )
      .setParameter("comercioId", comercioId)
      .list();
  }
}
