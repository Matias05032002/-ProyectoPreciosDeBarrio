package com.tallerwebi.infraestructura.Notificacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.RepositorioPromocion;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.infraestructura.RepositorioPromocionImpl;
import com.tallerwebi.infraestructura.RepositorioUsuarioImpl;
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
public class RepositoriosDeAvisosTest {

  private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 12, 10, 0);

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioPromocion repositorioPromocion;
  private RepositorioUsuario repositorioUsuario;

  @BeforeEach
  public void init() {
    repositorioPromocion = new RepositorioPromocionImpl(sessionFactory);
    repositorioUsuario = new RepositorioUsuarioImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverLosVecinosActivosDeLaLocalidadSinDistinguirMayusculas() {
    Usuario vecino = unUsuario("USER", true, "Ramos Mejía");
    unUsuario("USER", true, "Morón");
    unUsuario("USER", false, "Ramos Mejía");
    unUsuario("COMERCIO", true, "Ramos Mejía");

    List<Long> ids = repositorioUsuario.idsDeVecinosDeLocalidad("ramos mejía");

    assertThat(ids, contains(vecino.getId()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaListarLasPromocionesQueEmpezaronEnLaVentana() {
    Promocion reciente = unaPromocion(AHORA.minusHours(3), AHORA.plusDays(5), true);

    List<Promocion> resultado = repositorioPromocion.listarIniciadasEntre(
      AHORA.minusDays(2),
      AHORA
    );

    assertThat(resultado, contains(reciente));
  }

  @Test
  @Transactional
  @Rollback
  public void noDeberiaListarLasViejasLasFuturasLasVencidasNiLasDesactivadas() {
    unaPromocion(AHORA.minusDays(10), AHORA.plusDays(5), true);
    unaPromocion(AHORA.plusHours(3), AHORA.plusDays(5), true);
    unaPromocion(AHORA.minusHours(3), AHORA.minusHours(1), true);
    unaPromocion(AHORA.minusHours(3), AHORA.plusDays(5), false);

    assertThat(repositorioPromocion.listarIniciadasEntre(AHORA.minusDays(2), AHORA), is(empty()));
  }

  private Usuario unUsuario(String rol, boolean activo, String localidad) {
    Usuario usuario = new Usuario();
    usuario.setEmail("u-" + System.nanoTime() + "@test.com");
    usuario.setNombre("Vecino");
    usuario.setRol(rol);
    usuario.setActivo(activo);
    usuario.setPassword("1234");
    usuario.setLocalidad(localidad);
    sessionFactory.getCurrentSession().persist(usuario);
    return usuario;
  }

  private Promocion unaPromocion(LocalDateTime inicio, LocalDateTime fin, boolean activa) {
    Comercio comercio = new Comercio();
    comercio.setNombre("Almacen " + System.nanoTime());
    comercio.setDireccion("Av. Test " + System.nanoTime());
    comercio.setLocalidad("Ramos Mejía");
    sessionFactory.getCurrentSession().persist(comercio);
    Promocion promocion = new Promocion();
    promocion.setTitulo("Promo");
    promocion.setProductoNombre("Leche");
    promocion.setPrecioPromocional(100.0);
    promocion.setFechaInicio(inicio);
    promocion.setFechaFin(fin);
    promocion.setActiva(activa);
    promocion.setComercio(comercio);
    sessionFactory.getCurrentSession().persist(promocion);
    return promocion;
  }
}
