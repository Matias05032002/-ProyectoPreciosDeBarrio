package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.Comercio.VistaPerfil;
import com.tallerwebi.punta_a_punta.vistas.Reporte.VistaMisProductos;
import com.tallerwebi.punta_a_punta.vistas.Reporte.VistaReporte;
import org.junit.jupiter.api.*;

public class VistaMisProductosE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;

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
    context.newPage();
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaVerMensajeCuandoElComercioNoTieneProductos() {
    dadoQueEstoyLogueadoComoComercio();
    Page page = context.pages().get(0);
    VistaMisProductos vistaMisProductos = new VistaMisProductos(page);
    assertThat(vistaMisProductos.muestraMensajeSinProductos(), is(true));
  }

  @Test
  void deberiaVerLaPaginaDeMisProductos() {
    dadoQueEstoyLogueadoComoComercio();
    VistaMisProductos vistaMisProductos = new VistaMisProductos(context.pages().get(0));
    Page page = context.pages().get(0);
    assertThat(page.url(), org.hamcrest.Matchers.containsString("/mis-productos"));
  }

  @Test
  void deberiaVerProductosCuandoElComercioTieneReportes() {
    dadoQueElComercioRegistroSuPerfil();
    dadoQueUnVecinoReportoUnProductoEnMiComercio();
    dadoQueEstoyLogueadoComoComercio();
    VistaMisProductos vistaMisProductos = new VistaMisProductos(context.pages().get(0));
    assertThat(vistaMisProductos.muestraProductos(), is(true));
  }

  private void dadoQueEstoyLogueadoComoComercio() {
    Page page = context.pages().get(0);
    page.navigate("http://localhost:8080/spring/login");
    page.locator("#email").fill("test-comercio@unlam.edu.ar");
    page.locator("#password").fill("TestComercio1");
    page.locator("#btn-login").click();
  }

  private void dadoQueElComercioRegistroSuPerfil() {
    Page page = context.pages().get(0);
    page.navigate("http://localhost:8080/spring/login");
    page.locator("#email").fill("test-comercio@unlam.edu.ar");
    page.locator("#password").fill("TestComercio1");
    page.locator("#btn-login").click();
    VistaPerfil vistaPerfil = new VistaPerfil(page);
    vistaPerfil.escribirNombreComercio("Mi Almacen Test");
    vistaPerfil.seleccionarTipo("Almacen");
    vistaPerfil.escribirDireccion("Av. Mitre 123");
    vistaPerfil.escribirLocalidad("Lomas de Zamora");
    vistaPerfil.escribirDescripcion("Almacen de barrio");
    vistaPerfil.darClickEnGuardar();
    page.navigate("http://localhost:8080/spring/logout");
  }

  private void dadoQueUnVecinoReportoUnProductoEnMiComercio() {
    Page page = context.pages().get(0);
    VistaReporte vistaReporte = new VistaReporte(page);
    vistaReporte.iniciarSesionYNavegar("test@unlam.edu.ar", "test");
    vistaReporte.escribirNombreProducto("Leche");
    vistaReporte.escribirMarcaProducto("La Serenisima");
    vistaReporte.escribirCategoriaProducto("Lacteos");
    vistaReporte.escribirUnidadProducto("1L");
    vistaReporte.escribirNombreComercio("Mi Almacen Test");
    vistaReporte.escribirDireccionComercio("Av. Mitre 123");
    vistaReporte.escribirLocalidadComercio("Lomas de Zamora");
    vistaReporte.escribirPrecio("250");
    vistaReporte.darClickEnGuardarReporte();
    page.navigate("http://localhost:8080/spring/logout");
  }
}