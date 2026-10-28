package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.Producto.VistaProducto;
import com.tallerwebi.punta_a_punta.vistas.Reporte.VistaReporte;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import java.net.MalformedURLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaProductoE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaProducto vistaProducto;

  @BeforeAll
  static void abrirNavegador() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch();
  }

  @AfterAll
  static void cerrarNavegador() {
    playwright.close();
  }

  @BeforeEach
  void crearContextoYPagina() {
    ReiniciarDB.limpiarBaseDeDatos();
    context = browser.newContext();
    Page page = context.newPage();
    VistaLogin vistaLogin = new VistaLogin(page);
    vistaLogin.escribirEMAIL("test@unlam.edu.ar");
    vistaLogin.escribirClave("test");
    vistaLogin.darClickEnIniciarSesion();
    vistaProducto = new VistaProducto(page);
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaDecirProductosDelBarrioEnElTitulo() {
    String titulo = vistaProducto.obtenerTituloDeLaPagina();
    assertThat(titulo, equalToIgnoringCase("Productos del barrio"));
  }

  @Test
  void deberiaPoderBuscarUnProductoPorNombre() throws MalformedURLException {
    dadoQueElUsuarioBuscaUnProductoPorNombre("Leche");
    entoncesDeberiaVerLaPaginaDeProductos();
  }

  @Test
  void deberiaMostrarProductosCuandoHayDatosEnLaBase() {
    dadoQueExisteUnReporteCargado();
    vistaProducto.escribirNombreDelProductoABuscar("Leche");
    vistaProducto.darClickEnBuscar();
    assertThat(vistaProducto.hayProductosEnLaPagina(), is(true));
  }

  @Test
  void deberiaMostrarElBotonMarcarDudosoEnCadaProducto() {
    dadoQueExisteUnReporteCargado();
    vistaProducto.escribirNombreDelProductoABuscar("Leche");
    vistaProducto.darClickEnBuscar();
    assertThat(vistaProducto.hayBotonMarcarDudoso(), is(true));
  }

  @Test
  public void deberiaMostrarElModalCuandoSeMarcaComoDudoso() {
    dadoQueExisteUnReporteCargado();
    vistaProducto.escribirNombreDelProductoABuscar("Leche");
    vistaProducto.darClickEnBuscar();
    vistaProducto.darClickEnMarcarDudoso();
    assertThat(vistaProducto.hayModalDudoso(), is(true));
  }

  @Test
  void deberiaMostrarPlaceholderCuandoElReporteNoTieneFoto() {
    dadoQueExisteUnReporteCargado();
    vistaProducto.escribirNombreDelProductoABuscar("Leche");
    vistaProducto.darClickEnBuscar();
    assertThat(vistaProducto.hayPlaceholderVisible(), is(true));
  }

  @Test
  void deberiaMostrarElModalCuandoSeClickeaElPlaceholder() {
    dadoQueExisteUnReporteCargado();
    vistaProducto.escribirNombreDelProductoABuscar("Leche");
    vistaProducto.darClickEnBuscar();
    vistaProducto.darClickEnPlaceholderDeProducto();
    assertThat(vistaProducto.hayModalFoto(), is(true));
  }

  private void dadoQueElUsuarioBuscaUnProductoPorNombre(String nombre) {
    vistaProducto.escribirNombreDelProductoABuscar(nombre);
    vistaProducto.darClickEnBuscar();
  }

  private void entoncesDeberiaVerLaPaginaDeProductos() throws MalformedURLException {
    String url = vistaProducto.obtenerURLActual().getPath();
    assertThat(url.contains("producto"), is(true));
  }

  private void dadoQueExisteUnReporteCargado() {
    ReiniciarDB.sembrarProductoConReporte();
  }
}
