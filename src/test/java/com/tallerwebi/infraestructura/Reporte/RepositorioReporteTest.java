package com.tallerwebi.infraestructura.Reporte;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
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
public class RepositorioReporteTest {

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
  public void deberiaGuardarUnNuevoReporte() {
    Reporte reporte = dadoQueTengoUnReporte("Leche", "Almacen", 100.0);

    Reporte resultado = cuandoGuardoUnReporte(reporte);

    entoncesElReporteSeGuardo(resultado);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarUnReportePorId() {
    Reporte reporte = dadoQueTengoUnReporte("Arroz", "Kiosco", 200.0);
    dadoQueExisteElReporte(reporte);

    Reporte resultado = cuandoBuscoPorId(reporte.getId());

    entoncesElReporteEsCorrecto(resultado);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarReportesPorNombreDeProducto() {
    Reporte reporte = dadoQueTengoUnReporte("Fideos", "Almacen", 150.0);
    dadoQueExisteElReporte(reporte);

    List<Reporte> resultado = cuandoBuscoPorNombre("Fideos");

    assertThat(resultado, not(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaMarcarUnReporteComoDudoso() {
    Reporte reporte = dadoQueTengoUnReporte("Leche", "Almacen", 100.0);
    dadoQueExisteElReporte(reporte);

    int puntuacionAntes = reporte.getPuntuacion() != null ? reporte.getPuntuacion() : 0;
    Reporte resultado = repositorioReporte.marcarDudoso(reporte.getId(), null);

    assertThat(resultado.getPuntuacion(), is(equalTo(puntuacionAntes + 1)));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaListarTodosLosReportes() {
    dadoQueExisteElReporte(dadoQueTengoUnReporte("Leche", "Almacen", 100.0));
    dadoQueExisteElReporte(dadoQueTengoUnReporte("Arroz", "Kiosco", 200.0));

    List<Reporte> resultado = repositorioReporte.listarTodos();

    assertThat(resultado.size(), is(greaterThanOrEqualTo(2)));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarReportesPorProductoId() {
    Reporte reporte = dadoQueTengoUnReporte("Fideos", "Almacen", 150.0);
    dadoQueExisteElReporte(reporte);

    List<Reporte> resultado = repositorioReporte.buscarPorProducto(reporte.getProducto().getId());

    assertThat(resultado, not(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarReportesPorComercioId() {
    Reporte reporte = dadoQueTengoUnReporte("Arroz", "Kiosco", 200.0);
    dadoQueExisteElReporte(reporte);

    List<Reporte> resultado = repositorioReporte.buscarPorComercio(reporte.getComercio().getId());

    assertThat(resultado, not(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaContarTodosLosReportes() {
    dadoQueExisteElReporte(dadoQueTengoUnReporte("Leche", "Almacen", 100.0));

    Long total = repositorioReporte.contarTodos();

    assertThat(total, is(greaterThanOrEqualTo(1L)));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerProductosMasReportados() {
    dadoQueExisteElReporte(dadoQueTengoUnReporte("Leche", "Almacen", 100.0));
    dadoQueExisteElReporte(dadoQueTengoUnReporte("Leche", "Kiosco", 120.0));

    List<Object[]> resultado = repositorioReporte.productosMasReportados(5);

    assertThat(resultado, not(empty()));
    assertThat(resultado.get(0)[0].toString(), equalTo("Leche"));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerComerciosMasReportados() {
    dadoQueExisteElReporte(dadoQueTengoUnReporte("Leche", "Almacen", 100.0));

    List<Object[]> resultado = repositorioReporte.comerciosMasReportados(5);

    assertThat(resultado, not(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerReportesPorMes() {
    dadoQueExisteElReporte(dadoQueTengoUnReporte("Leche", "Almacen", 100.0));

    List<Object[]> resultado = repositorioReporte.reportesPorMes();

    assertThat(resultado, not(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarReportesPorUsuarioId() {
    com.tallerwebi.dominio.Usuario usuario = new com.tallerwebi.dominio.Usuario();
    usuario.setEmail("test@test.com");
    usuario.setNombre("Test");
    usuario.setRol("USER");
    usuario.setActivo(true);
    usuario.setPassword("1234");
    sessionFactory.getCurrentSession().persist(usuario);

    Reporte reporte = dadoQueTengoUnReporte("Pan", "Almacen", 50.0);
    reporte.setUsuario(usuario);
    dadoQueExisteElReporte(reporte);

    List<Reporte> resultado = repositorioReporte.buscarPorUsuarioId(usuario.getId());

    assertThat(resultado, not(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaContarReportesDudososPorComercio() {
    Reporte reporte = dadoQueTengoUnReporte("Leche", "Almacen", 100.0);
    dadoQueExisteElReporte(reporte);

    Long resultado = repositorioReporte.contarReportesDudososPorComercio(
      reporte.getComercio().getId()
    );

    assertThat(resultado, is(greaterThanOrEqualTo(0L)));
  }

  @Test
  @Transactional
  @Rollback
  public void marcarDudosoDeberiaRetornarNullCuandoSuperaPuntuacionMaxima() {
    Reporte reporte = dadoQueTengoUnReporte("Leche", "Almacen", 100.0);
    reporte.setPuntuacion(Reporte.PUNTUACION_MAXIMA);
    dadoQueExisteElReporte(reporte);

    Reporte resultado = repositorioReporte.marcarDudoso(reporte.getId(), null);

    assertThat(resultado, is(nullValue()));
  }

  private Reporte dadoQueTengoUnReporte(
    String nombreProducto,
    String nombreComercio,
    Double precio
  ) {
    Producto producto = new Producto();
    producto.setNombre(nombreProducto);
    sessionFactory.getCurrentSession().persist(producto);

    Comercio comercio = new Comercio();
    comercio.setNombre(nombreComercio);
    comercio.setDireccion("Av. Test " + nombreComercio);
    comercio.setLocalidad("Localidad Test");
    sessionFactory.getCurrentSession().persist(comercio);

    Reporte reporte = new Reporte();
    reporte.setProducto(producto);
    reporte.setComercio(comercio);
    reporte.setPrecio(precio);
    reporte.setFechaDeReporte(LocalDateTime.now());
    return reporte;
  }

  private void dadoQueExisteElReporte(Reporte reporte) {
    sessionFactory.getCurrentSession().persist(reporte);
  }

  private Reporte cuandoGuardoUnReporte(Reporte reporte) {
    return repositorioReporte.guardarReporte(reporte);
  }

  private Reporte cuandoBuscoPorId(Long id) {
    return repositorioReporte.buscarReporte(id);
  }

  private List<Reporte> cuandoBuscoPorNombre(String nombre) {
    return repositorioReporte.buscarPorNombre(nombre);
  }

  private void entoncesElReporteSeGuardo(Reporte resultado) {
    assertThat(resultado, is(notNullValue()));
    assertThat(resultado.getId(), is(notNullValue()));
  }

  private void entoncesElReporteEsCorrecto(Reporte resultado) {
    assertThat(resultado, is(notNullValue()));
  }
}
