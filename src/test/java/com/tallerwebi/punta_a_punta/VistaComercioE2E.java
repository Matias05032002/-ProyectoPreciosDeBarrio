package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.Comercio.VistaComercio;
import com.tallerwebi.punta_a_punta.vistas.Reporte.VistaReporte;
import java.net.MalformedURLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaComercioE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaComercio vistaComercio;

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
    vistaComercio = new VistaComercio(page);
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaDecirComerciosDelBarrioEnElTitulo() {
    String titulo = vistaComercio.obtenerTituloDeLaPagina();
    assertThat(titulo, equalToIgnoringCase("Comercios del barrio"));
  }

  @Test
  void deberiaPoderBuscarUnComercioPorNombre() throws MalformedURLException {
    dadoQueElUsuarioBuscaUnComercioPorNombre("Almacen Matias");
    entoncesDeberiaVerLaPaginaDeComercios();
  }

  @Test
  void deberiaMostrarComerciosCuandoHayDatosEnLaBase() {
    dadoQueExisteUnReporteCargado();
    vistaComercio = new VistaComercio(context.pages().get(0));
    assertThat(vistaComercio.hayComerciosEnLaPagina(), is(true));
  }

  @Test
  void deberiaMostrarElBotonVerDetalleEnCadaComercio() {
    dadoQueExisteUnReporteCargado();
    vistaComercio = new VistaComercio(context.pages().get(0));
    assertThat(vistaComercio.existeBotonVerDetalle(), is(true));
  }

  private void dadoQueElUsuarioBuscaUnComercioPorNombre(String nombre) {
    vistaComercio.escribirNombreDelComercioABuscar(nombre);
    vistaComercio.darClickEnBuscar();
  }

  private void entoncesDeberiaVerLaPaginaDeComercios() throws MalformedURLException {
    String url = vistaComercio.obtenerURLActual().getPath();
    assertThat(url.contains("comercio"), is(true));
  }

  private void dadoQueExisteUnReporteCargado() {
    VistaReporte vistaReporte = new VistaReporte(context.pages().get(0));
    vistaReporte.iniciarSesionYNavegar("test@unlam.edu.ar", "test");
    vistaReporte.escribirNombreProducto("Leche");
    vistaReporte.escribirMarcaProducto("La Serenisima");
    vistaReporte.escribirCategoriaProducto("Lacteos");
    vistaReporte.escribirUnidadProducto("1L");
    vistaReporte.escribirNombreComercio("Almacen De Matias");
    vistaReporte.escribirDireccionComercio("Av. Mitre 123");
    vistaReporte.escribirLocalidadComercio("Lomas de Zamora");
    vistaReporte.escribirPrecio("250");
    vistaReporte.darClickEnGuardarReporte();
  }
}
