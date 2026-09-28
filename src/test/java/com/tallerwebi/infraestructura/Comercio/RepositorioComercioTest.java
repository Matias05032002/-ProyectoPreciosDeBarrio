package com.tallerwebi.infraestructura.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.RepositorioComercio;
import com.tallerwebi.infraestructura.RepositorioComercioImpl;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
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
public class RepositorioComercioTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioComercio repositorioComercio;

  @BeforeEach
  public void init() {
    repositorioComercio = new RepositorioComercioImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnNuevoComercio() {
    Comercio comercio = dadoQueTengoUnComercio("Almacen De Matias");

    Comercio resultado = cuandoGuardoUnComercio(comercio);

    entoncesElComercioSeGuardo(resultado, "Almacen De Matias");
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarUnComercioPorId() {
    Comercio comercio = dadoQueTengoUnComercio("Verduleria Lopez");
    dadoQueExisteElComercio(comercio);

    Comercio resultado = cuandoBuscoPorId(comercio.getId());

    entoncesElComercioEsCorrecto(resultado, "Verduleria Lopez");
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarUnComercioPorNombre() {
    Comercio comercio = dadoQueTengoUnComercio("Carniceria Llanos");
    dadoQueExisteElComercio(comercio);

    List<Comercio> resultado = cuandoBuscoPorNombre("Carniceria Llanos");

    entoncesElComercioEsCorrecto(resultado.get(0), "Carniceria Llanos");
  }

  @Test
  @Transactional
  public void noDeberiaBuscarUnComercioInexistentePorNombre() {
    List<Comercio> resultado = cuandoBuscoPorNombre("ComercioInexistente");
    assertThat(resultado, is(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaListarTodosLosComercios() {
    dadoQueExisteElComercio(dadoQueTengoUnComercio("Almacen A"));
    dadoQueExisteElComercio(dadoQueTengoUnComercio("Almacen B"));

    List<Comercio> resultado = repositorioComercio.listarTodos();

    assertThat(resultado.size(), is(greaterThanOrEqualTo(2)));
  }

  private Comercio dadoQueTengoUnComercio(String nombre) {
    Comercio comercio = new Comercio();
    comercio.setNombre(nombre);
    return comercio;
  }

  private void dadoQueExisteElComercio(Comercio comercio) {
    sessionFactory.getCurrentSession().persist(comercio);
  }

  private Comercio cuandoGuardoUnComercio(Comercio comercio) {
    return repositorioComercio.guardarComercio(comercio);
  }

  private Comercio cuandoBuscoPorId(Long id) {
    return repositorioComercio.buscarComercio(id);
  }

  private List<Comercio> cuandoBuscoPorNombre(String nombre) {
    return repositorioComercio.buscarComercioPorNombre(nombre);
  }

  private void entoncesElComercioSeGuardo(Comercio resultado, String nombreEsperado) {
    assertThat(resultado, is(notNullValue()));
    assertThat(resultado.getNombre(), is(equalTo(nombreEsperado)));
  }

  private void entoncesElComercioEsCorrecto(Comercio resultado, String nombreEsperado) {
    assertThat(resultado, is(notNullValue()));
    assertThat(resultado.getNombre(), is(equalTo(nombreEsperado)));
  }
}
