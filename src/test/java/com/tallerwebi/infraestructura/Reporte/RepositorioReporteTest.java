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
import java.time.LocalDate;
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

  @Test
  @Transactional
  @Rollback
  public void productosMasBuscadosPorLocalidadDeberiaRetornarProductosOrdenados() {
    // 2 reportes de Leche y 1 de Arroz en la misma localidad
    Reporte r1 = dadoQueTengoUnReporteConLocalidad("Leche", "Almacen", 100.0, "Matanza");
    Reporte r2 = dadoQueTengoUnReporteConLocalidad("Leche", "Kiosco", 110.0, "Matanza");
    Reporte r3 = dadoQueTengoUnReporteConLocalidad("Arroz", "Almacen", 90.0, "Matanza");
    dadoQueExisteElReporte(r1);
    dadoQueExisteElReporte(r2);
    dadoQueExisteElReporte(r3);

    List<Object[]> resultado = repositorioReporte.productosMasBuscadosPorLocalidad("Matanza", 5);

    assertThat(resultado, not(empty()));
    // el primero debe ser Leche (más reportes)
    assertThat(resultado.get(0)[0].toString(), equalTo("Leche"));
  }

  @Test
  @Transactional
  @Rollback
  public void productosMasBuscadosPorLocalidadNoDeberiaIncluirOtraLocalidad() {
    Reporte r1 = dadoQueTengoUnReporteConLocalidad("Leche", "Almacen", 100.0, "Matanza");
    Reporte r2 = dadoQueTengoUnReporteConLocalidad("Leche", "Kiosco", 110.0, "Lomas");
    dadoQueExisteElReporte(r1);
    dadoQueExisteElReporte(r2);

    List<Object[]> resultado = repositorioReporte.productosMasBuscadosPorLocalidad("Matanza", 5);

    // solo debe aparecer el de Matanza
    assertThat(resultado.size(), is(1));
  }

  @Test
  @Transactional
  @Rollback
  public void productosMasBuscadosPorLocalidadDeberiaRespetarElLimite() {
    dadoQueExisteElReporte(dadoQueTengoUnReporteConLocalidad("Leche", "Almacen", 100.0, "Matanza"));
    dadoQueExisteElReporte(dadoQueTengoUnReporteConLocalidad("Arroz", "Kiosco", 50.0, "Matanza"));
    dadoQueExisteElReporte(dadoQueTengoUnReporteConLocalidad("Fideos", "Super", 80.0, "Matanza"));

    List<Object[]> resultado = repositorioReporte.productosMasBuscadosPorLocalidad("Matanza", 2);

    assertThat(resultado.size(), is(lessThanOrEqualTo(2)));
  }

  @Test
  @Transactional
  @Rollback
  public void listarRecientesDeberiaRetornarReportesUltimos7Dias() {
    dadoQueExisteElReporte(dadoQueTengoUnReporte("Leche", "Almacen", 100.0));
    dadoQueExisteElReporte(dadoQueTengoUnReporte("Arroz", "Kiosco", 200.0));

    List<Reporte> resultado = repositorioReporte.listarRecientes(7);

    assertThat(resultado, not(empty()));
    assertThat(resultado.size(), is(greaterThanOrEqualTo(2)));
  }

  @Test
  @Transactional
  @Rollback
  public void listarRecientesNoDeberiaRetornarReportesViejos() {
    Reporte reporteViejo = dadoQueTengoUnReporte("Fideos", "Super", 80.0);
    reporteViejo.setFechaDeReporte(LocalDateTime.now().minusDays(10));
    dadoQueExisteElReporte(reporteViejo);

    List<Reporte> resultado = repositorioReporte.listarRecientes(7);

    // el reporte de hace 10 días no debe aparecer
    boolean contieneViejo = resultado
      .stream()
      .anyMatch(r -> r.getId().equals(reporteViejo.getId()));
    assertThat(contieneViejo, is(false));
  }

  @Test
  @Transactional
  @Rollback
  public void listarRecientesDeberiaRespetarLimiteDeVeinteResultados() {
    for (int i = 0; i < 25; i++) {
      dadoQueExisteElReporte(
        dadoQueTengoUnReporteConLocalidad("Producto" + i, "Comercio" + i, 100.0 + i, "Matanza")
      );
    }

    List<Reporte> resultado = repositorioReporte.listarRecientes(7);

    assertThat(resultado.size(), is(lessThanOrEqualTo(20)));
  }

  @Test
  @Transactional
  @Rollback
  public void buscarReporteDuplicadoDeberiaEncontrarUnReporteDeEsaFecha() {
    LocalDate dia = LocalDate.of(2026, 11, 2);
    Reporte reporte = dadoQueExisteUnReporteDeUnUsuarioEn(dia.atTime(10, 0));

    Reporte resultado = cuandoBuscoElDuplicadoEn(reporte, dia);

    assertThat(resultado, is(notNullValue()));
  }

  @Test
  @Transactional
  @Rollback
  public void buscarReporteDuplicadoDeberiaEncontrarUnReporteHechoDeNoche() {
    // Entre las 21 y las 24 hora argentina el día en UTC ya es el siguiente
    LocalDate dia = LocalDate.of(2026, 11, 2);
    Reporte reporte = dadoQueExisteUnReporteDeUnUsuarioEn(dia.atTime(23, 30));

    Reporte resultado = cuandoBuscoElDuplicadoEn(reporte, dia);

    assertThat(resultado, is(notNullValue()));
  }

  @Test
  @Transactional
  @Rollback
  public void buscarReporteDuplicadoNoDeberiaEncontrarUnReporteDelDiaAnterior() {
    LocalDate dia = LocalDate.of(2026, 11, 2);
    Reporte reporte = dadoQueExisteUnReporteDeUnUsuarioEn(dia.atTime(23, 30));

    Reporte resultado = cuandoBuscoElDuplicadoEn(reporte, dia.plusDays(1));

    assertThat(resultado, is(nullValue()));
  }

  @Test
  @Transactional
  @Rollback
  public void buscarReporteDuplicadoNoDeberiaEncontrarUnReporteDelDiaSiguiente() {
    LocalDate dia = LocalDate.of(2026, 11, 2);
    Reporte reporte = dadoQueExisteUnReporteDeUnUsuarioEn(dia.plusDays(1).atTime(0, 10));

    Reporte resultado = cuandoBuscoElDuplicadoEn(reporte, dia);

    assertThat(resultado, is(nullValue()));
  }

  @Test
  @Transactional
  @Rollback
  public void buscarReporteDuplicadoNoDeberiaEncontrarElReporteDeOtroUsuario() {
    LocalDate dia = LocalDate.of(2026, 11, 2);
    Reporte reporte = dadoQueExisteUnReporteDeUnUsuarioEn(dia.atTime(10, 0));

    Reporte resultado = repositorioReporte.buscarReporteDuplicado(
      reporte.getUsuario().getId() + 1000,
      reporte.getProducto().getId(),
      reporte.getComercio().getId(),
      dia
    );

    assertThat(resultado, is(nullValue()));
  }

  @Test
  @Transactional
  @Rollback
  public void buscarReporteDuplicadoNoDeberiaFallarSiYaHayDosReportesDelMismoDia() {
    // Pasaba con los datos cargados antes de corregir la detección por zona horaria
    LocalDate dia = LocalDate.of(2026, 11, 2);
    Reporte primero = dadoQueExisteUnReporteDeUnUsuarioEn(dia.atTime(10, 0));
    Reporte segundo = new Reporte();
    segundo.setUsuario(primero.getUsuario());
    segundo.setProducto(primero.getProducto());
    segundo.setComercio(primero.getComercio());
    segundo.setPrecio(120.0);
    segundo.setFechaDeReporte(dia.atTime(23, 30));
    dadoQueExisteElReporte(segundo);

    Reporte resultado = cuandoBuscoElDuplicadoEn(primero, dia);

    assertThat(resultado, is(notNullValue()));
  }

  private Reporte dadoQueExisteUnReporteDeUnUsuarioEn(LocalDateTime fecha) {
    com.tallerwebi.dominio.Usuario usuario = new com.tallerwebi.dominio.Usuario();
    usuario.setEmail("duplicado" + System.nanoTime() + "@test.com");
    usuario.setNombre("Test");
    usuario.setRol("USER");
    usuario.setActivo(true);
    usuario.setPassword("1234");
    sessionFactory.getCurrentSession().persist(usuario);

    Reporte reporte = dadoQueTengoUnReporte("Leche", "Almacen", 100.0);
    reporte.setUsuario(usuario);
    reporte.setFechaDeReporte(fecha);
    dadoQueExisteElReporte(reporte);
    return reporte;
  }

  private Reporte cuandoBuscoElDuplicadoEn(Reporte reporte, LocalDate fecha) {
    return repositorioReporte.buscarReporteDuplicado(
      reporte.getUsuario().getId(),
      reporte.getProducto().getId(),
      reporte.getComercio().getId(),
      fecha
    );
  }

  // Helper adicional — misma firma que dadoQueTengoUnReporte pero acepta localidad
  private Reporte dadoQueTengoUnReporteConLocalidad(
    String nombreProducto,
    String nombreComercio,
    Double precio,
    String localidad
  ) {
    Producto producto = new Producto();
    producto.setNombre(nombreProducto);
    sessionFactory.getCurrentSession().persist(producto);

    Comercio comercio = new Comercio();
    comercio.setNombre(nombreComercio);
    comercio.setDireccion("Av. Test " + nombreComercio + "-" + localidad + "-" + System.nanoTime());
    comercio.setLocalidad(localidad);
    sessionFactory.getCurrentSession().persist(comercio);

    Reporte reporte = new Reporte();
    reporte.setProducto(producto);
    reporte.setComercio(comercio);
    reporte.setPrecio(precio);
    reporte.setFechaDeReporte(java.time.LocalDateTime.now());
    return reporte;
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaEliminarUnReporte() {
    Reporte reporte = dadoQueTengoUnReporte("Yerba", "Almacen", 100.0);
    dadoQueExisteElReporte(reporte);

    repositorioReporte.eliminarReporte(reporte.getId());
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    assertThat(repositorioReporte.buscarReporte(reporte.getId()), is(nullValue()));
  }

  @Test
  @Transactional
  @Rollback
  public void eliminarUnReporteNoBorraAlProductoNiAlComercio() {
    Reporte reporte = dadoQueTengoUnReporte("Azucar", "Kiosco", 90.0);
    dadoQueExisteElReporte(reporte);
    Long productoId = reporte.getProducto().getId();
    Long comercioId = reporte.getComercio().getId();

    repositorioReporte.eliminarReporte(reporte.getId());
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    assertThat(
      sessionFactory.getCurrentSession().get(Producto.class, productoId),
      is(notNullValue())
    );
    assertThat(
      sessionFactory.getCurrentSession().get(Comercio.class, comercioId),
      is(notNullValue())
    );
  }

  @Test
  @Transactional
  @Rollback
  public void eliminarUnReporteConMarcasDeDudosoNoFalla() {
    Reporte reporte = dadoQueTengoUnReporte("Aceite", "Almacen", 300.0);
    dadoQueExisteElReporte(reporte);
    com.tallerwebi.dominio.Usuario vecino = new com.tallerwebi.dominio.Usuario();
    vecino.setEmail("vecino-dudoso-" + System.nanoTime() + "@test.com");
    vecino.setNombre("Vecino");
    vecino.setRol("USER");
    vecino.setActivo(true);
    vecino.setPassword("1234");
    sessionFactory.getCurrentSession().persist(vecino);
    repositorioReporte.marcarDudoso(reporte.getId(), vecino);

    repositorioReporte.eliminarReporte(reporte.getId());
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    assertThat(repositorioReporte.buscarReporte(reporte.getId()), is(nullValue()));
    assertThat(
      sessionFactory.getCurrentSession().get(com.tallerwebi.dominio.Usuario.class, vecino.getId()),
      is(notNullValue())
    );
  }

  @Test
  @Transactional
  @Rollback
  public void desmarcarDudosoDeberiaRestarUnoYQuitarAlUsuarioDeLaLista() {
    Reporte reporte = dadoQueTengoUnReporte("Leche", "Almacen", 100.0);
    reporte.setPuntuacion(3);
    dadoQueExisteElReporte(reporte);
    Usuario vecino = dadoQueExisteUnVecino();
    repositorioReporte.marcarDudoso(reporte.getId(), vecino);

    Reporte resultado = repositorioReporte.desmarcarDudoso(reporte.getId(), vecino);

    assertThat(resultado.getPuntuacion(), is(3));
    assertThat(resultado.getUsuariosQueMarcaron(), not(hasItem(vecino)));
  }

  @Test
  @Transactional
  @Rollback
  public void despuesDeDesmarcarDudosoElVecinoPuedeVolverAMarcarlo() {
    Reporte reporte = dadoQueTengoUnReporte("Leche", "Almacen", 100.0);
    reporte.setPuntuacion(3);
    dadoQueExisteElReporte(reporte);
    Usuario vecino = dadoQueExisteUnVecino();
    repositorioReporte.marcarDudoso(reporte.getId(), vecino);
    repositorioReporte.desmarcarDudoso(reporte.getId(), vecino);

    Reporte resultado = repositorioReporte.marcarDudoso(reporte.getId(), vecino);

    assertThat(resultado.getPuntuacion(), is(4));
    assertThat(resultado.getUsuariosQueMarcaron(), hasItem(vecino));
  }

  @Test
  @Transactional
  @Rollback
  public void desmarcarDudosoDeQuienNoLoMarcoNoDeberiaCambiarLaPuntuacion() {
    Reporte reporte = dadoQueTengoUnReporte("Leche", "Almacen", 100.0);
    reporte.setPuntuacion(3);
    dadoQueExisteElReporte(reporte);
    Usuario vecino = dadoQueExisteUnVecino();

    Reporte resultado = repositorioReporte.desmarcarDudoso(reporte.getId(), vecino);

    assertThat(resultado.getPuntuacion(), is(3));
  }

  @Test
  @Transactional
  @Rollback
  public void desmarcarDudosoNuncaDejaLaPuntuacionPorDebajoDeCero() {
    Reporte reporte = dadoQueTengoUnReporte("Leche", "Almacen", 100.0);
    Usuario vecino = dadoQueExisteUnVecino();
    reporte.getUsuariosQueMarcaron().add(vecino);
    reporte.setPuntuacion(0);
    dadoQueExisteElReporte(reporte);

    Reporte resultado = repositorioReporte.desmarcarDudoso(reporte.getId(), vecino);

    assertThat(resultado.getPuntuacion(), is(0));
  }

  @Test
  @Transactional
  @Rollback
  public void desmarcarDudosoDeUnReporteInexistenteDeberiaDevolverNull() {
    assertThat(
      repositorioReporte.desmarcarDudoso(99999L, dadoQueExisteUnVecino()),
      is(nullValue())
    );
  }

  private Usuario dadoQueExisteUnVecino() {
    Usuario vecino = new Usuario();
    vecino.setEmail("vecino-" + System.nanoTime() + "@test.com");
    vecino.setNombre("Vecino");
    vecino.setRol("USER");
    vecino.setActivo(true);
    vecino.setPassword("1234");
    sessionFactory.getCurrentSession().persist(vecino);
    return vecino;
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
