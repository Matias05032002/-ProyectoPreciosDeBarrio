package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioUsuario")
public class RepositorioUsuarioImpl implements RepositorioUsuario {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioUsuarioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Usuario buscarUsuario(String email, String password) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Usuario where email = :email and password = :password", Usuario.class)
      .setParameter("email", email)
      .setParameter("password", password)
      .uniqueResult();
  }

  @Override
  public void guardar(Usuario usuario) {
    sessionFactory.getCurrentSession().persist(usuario);
  }

  @Override
  public Usuario buscar(String email) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Usuario where email = :email", Usuario.class)
      .setParameter("email", email)
      .uniqueResult();
  }

  @Override
  public void modificar(Usuario usuario) {
    Usuario existente = sessionFactory
      .getCurrentSession()
      .createQuery("from Usuario where id = :id", Usuario.class)
      .setParameter("id", usuario.getId())
      .uniqueResult();
    if (existente == null) {
      throw new UsuarioNoEncontrado();
    }
    sessionFactory.getCurrentSession().merge(usuario);
  }

  @Override
  public Usuario buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Usuario.class, id);
  }

  @Override
  public Long contarTodos() {
    return (Long) sessionFactory
      .getCurrentSession()
      .createQuery("SELECT COUNT(u) FROM Usuario u")
      .uniqueResult();
  }

  @Override
  public List<Object[]> usuariosMasActivos(int limite) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT u.nombre, u.apellido, COUNT(r) FROM Reporte r " +
        "JOIN r.usuario u GROUP BY u.id, u.nombre, u.apellido " +
        "ORDER BY COUNT(r) DESC"
      )
      .setMaxResults(limite)
      .list();
  }

  @Override
  public List<Object[]> distribucionPorRangoEtario() {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT " +
        "CASE " +
        "  WHEN YEAR(CURRENT_DATE) - YEAR(u.fechaNacimiento) < 18 THEN 'Menor de 18' " +
        "  WHEN YEAR(CURRENT_DATE) - YEAR(u.fechaNacimiento) < 25 THEN '18-24' " +
        "  WHEN YEAR(CURRENT_DATE) - YEAR(u.fechaNacimiento) < 35 THEN '25-34' " +
        "  WHEN YEAR(CURRENT_DATE) - YEAR(u.fechaNacimiento) < 50 THEN '35-49' " +
        "  ELSE '50+' " +
        "END, COUNT(u) " +
        "FROM Usuario u WHERE u.fechaNacimiento IS NOT NULL " +
        "GROUP BY " +
        "CASE " +
        "  WHEN YEAR(CURRENT_DATE) - YEAR(u.fechaNacimiento) < 18 THEN 'Menor de 18' " +
        "  WHEN YEAR(CURRENT_DATE) - YEAR(u.fechaNacimiento) < 25 THEN '18-24' " +
        "  WHEN YEAR(CURRENT_DATE) - YEAR(u.fechaNacimiento) < 35 THEN '25-34' " +
        "  WHEN YEAR(CURRENT_DATE) - YEAR(u.fechaNacimiento) < 50 THEN '35-49' " +
        "  ELSE '50+' " +
        "END"
      )
      .list();
  }
}
