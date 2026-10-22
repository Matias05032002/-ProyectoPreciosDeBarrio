package com.tallerwebi.infraestructura.Admin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.infraestructura.RepositorioReporteImpl;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
public class RepositorioAdminTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioReporte repositorioReporte;

  @BeforeEach
  public void init() {
    repositorioReporte = new RepositorioReporteImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void contarTodosDevuelveCantidadCorrectaDespuesDeGuardar() {
    dadoQueExisteUnReporte("Leche", "Almacen Test", "test@test.com");
    Long total = repositorioReporte.contarTodos();
    assertThat(total, greaterThanOrEqualTo(1L));
  }

  @Test
  @Transactional
  @Rollback
  public void productosMasReportadosDevuelveResultados() {
    dadoQueExisteUnReporte("Aceite", "Almacen Test", "u1@test.com");
    List<Object[]> resultado = repositorioReporte.productosMasReportados(5);
    assertThat(resultado, not(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void comerciosMasReportadosDevuelveResultados() {
    dadoQueExisteUnReporte("Pan", "Almacen Central", "u2@test.com");
    List<Object[]> resultado = repositorioReporte.comerciosMasReportados(5);
    assertThat(resultado, not(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void reportesPorMesDevuelveResultados() {
    dadoQueExisteUnReporte("Fideos", "Almacen Norte", "u3@test.com");
    List<Object[]> resultado = repositorioReporte.reportesPorMes();
    assertThat(resultado, not(empty()));
  }

  private void dadoQueExisteUnReporte(String nombreProducto, String nombreComercio, String email) {
    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setPassword("1234");
    usuario.setRol("USER");
    sessionFactory.getCurrentSession().persist(usuario);

    Producto producto = new Producto();
    producto.setNombre(nombreProducto);
    producto.setMarca("Marca Test");
    producto.setCategoria("General");
    producto.setUnidad("1u");
    sessionFactory.getCurrentSession().persist(producto);

    Comercio comercio = new Comercio();
    comercio.setNombre(nombreComercio);
    comercio.setDireccion("Calle Falsa 123");
    comercio.setLocalidad("Lomas");
    sessionFactory.getCurrentSession().persist(comercio);

    Reporte reporte = new Reporte();
    reporte.setUsuario(usuario);
    reporte.setProducto(producto);
    reporte.setComercio(comercio);
    reporte.setPrecio(100.0);
    reporte.setFechaDeReporte(LocalDateTime.now());
    sessionFactory.getCurrentSession().persist(reporte);
  }
}
