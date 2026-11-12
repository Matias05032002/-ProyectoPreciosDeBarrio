package com.tallerwebi.infraestructura.Reporte;

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
public class RepositorioReporteInactividadTest {

  private static final LocalDateTime LIMITE = LocalDateTime.of(2026, 11, 1, 0, 0);

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
  public void deberiaDevolverAlVecinoCuyoUltimoReporteEsAnteriorAlLimite() {
    Usuario vecino = unUsuario("USER", true);
    unReporte(vecino, LIMITE.minusDays(20));
    unReporte(vecino, LIMITE.minusDays(5));

    List<Object[]> resultado = repositorioReporte.vecinosSinReportarDesde(LIMITE);

    assertThat(resultado, hasSize(1));
    assertThat(resultado.get(0)[0], is(vecino.getId()));
    assertThat(resultado.get(0)[1], is(LIMITE.minusDays(5)));
  }

  @Test
  @Transactional
  @Rollback
  public void noDeberiaDevolverAlQueReportoDespuesDelLimite() {
    Usuario vecino = unUsuario("USER", true);
    unReporte(vecino, LIMITE.minusDays(20));
    unReporte(vecino, LIMITE.plusDays(1));

    assertThat(repositorioReporte.vecinosSinReportarDesde(LIMITE), is(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void noDeberiaDevolverComerciosNiCuentasInactivas() {
    unReporte(unUsuario("COMERCIO", true), LIMITE.minusDays(20));
    unReporte(unUsuario("USER", false), LIMITE.minusDays(20));

    assertThat(repositorioReporte.vecinosSinReportarDesde(LIMITE), is(empty()));
  }

  private Usuario unUsuario(String rol, boolean activo) {
    Usuario usuario = new Usuario();
    usuario.setEmail("u-" + System.nanoTime() + "@test.com");
    usuario.setNombre("Vecino");
    usuario.setRol(rol);
    usuario.setActivo(activo);
    usuario.setPassword("1234");
    sessionFactory.getCurrentSession().persist(usuario);
    return usuario;
  }

  private void unReporte(Usuario usuario, LocalDateTime fecha) {
    Producto producto = new Producto();
    producto.setNombre("Leche " + System.nanoTime());
    sessionFactory.getCurrentSession().persist(producto);
    Comercio comercio = new Comercio();
    comercio.setNombre("Almacen " + System.nanoTime());
    comercio.setDireccion("Av. Test " + System.nanoTime());
    comercio.setLocalidad("Localidad Test");
    sessionFactory.getCurrentSession().persist(comercio);
    Reporte reporte = new Reporte();
    reporte.setUsuario(usuario);
    reporte.setProducto(producto);
    reporte.setComercio(comercio);
    reporte.setPrecio(100.0);
    reporte.setFechaDeReporte(fecha);
    sessionFactory.getCurrentSession().persist(reporte);
  }
}
