package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.Reporte.VistaEditarReporte;
import com.tallerwebi.punta_a_punta.vistas.Reporte.VistaMisReportes;
import com.tallerwebi.punta_a_punta.vistas.Reporte.VistaReporte;
import org.junit.jupiter.api.*;

public class VistaMisReportesE2E {

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
  void deberiaVerMensajeCuandoNoTieneReportes() {
    dadoQueElUsuarioEstaLogueado();
    VistaMisReportes vistaMisReportes = new VistaMisReportes(context.pages().get(0));
    assertThat(vistaMisReportes.muestraMensajeSinReportes(), is(true));
  }

  @Test
  void deberiaVerSusReportesCuandoLosPublico() {
    dadoQueElUsuarioEstaLogueado();
    dadoQuePublicoUnReporte();
    VistaMisReportes vistaMisReportes = new VistaMisReportes(context.pages().get(0));
    assertThat(vistaMisReportes.muestraReportes(), is(true));
  }

  @Test
  void deberiaVerUnSoloReporteCuandoPublicoUno() {
    dadoQueElUsuarioEstaLogueado();
    dadoQuePublicoUnReporte();
    VistaMisReportes vistaMisReportes = new VistaMisReportes(context.pages().get(0));
    assertThat(vistaMisReportes.cantidadDeReportes(), is(1));
  }

  @Test
  void deberiaVerBotonEditarCuandoTieneReportesRecientes() {
    dadoQueElUsuarioEstaLogueado();
    dadoQuePublicoUnReporte();
    VistaMisReportes vistaMisReportes = new VistaMisReportes(context.pages().get(0));
    assertThat(vistaMisReportes.hayBotonEditar(), is(true));
  }

  @Test
  void alClickearEditarDeberiaNavegerAlFormularioDeEdicion() {
    dadoQueElUsuarioEstaLogueado();
    dadoQuePublicoUnReporte();
    VistaMisReportes vistaMisReportes = new VistaMisReportes(context.pages().get(0));
    vistaMisReportes.darClickEnPrimerBotonEditar();
    VistaEditarReporte vistaEditar = new VistaEditarReporte(context.pages().get(0));
    assertThat(vistaEditar.urlActual(), containsString("/editar"));
  }

  @Test
  void alEditarUnReporteDeberiaRedirigirAMisReportesConMensaje() {
    dadoQueElUsuarioEstaLogueado();
    dadoQuePublicoUnReporte();
    VistaMisReportes vistaMisReportes = new VistaMisReportes(context.pages().get(0));
    vistaMisReportes.darClickEnPrimerBotonEditar();

    VistaEditarReporte vistaEditar = new VistaEditarReporte(context.pages().get(0));
    vistaEditar.escribirNombreProducto("Manteca");
    vistaEditar.escribirMarcaProducto("La Serenisima");
    vistaEditar.seleccionarCategoriaProducto("Lacteos");
    vistaEditar.escribirUnidadProducto("200g");
    vistaEditar.escribirNombreComercio("Almacen Test");
    vistaEditar.escribirDireccionComercio("Av. Mitre 123");
    vistaEditar.seleccionarLocalidadComercio("Lomas de Zamora");
    vistaEditar.escribirPrecio("350");
    vistaEditar.darClickEnGuardarCambios();

    // No crear nueva VistaMisReportes — el navigate() del constructor pisaría ?editado=true
    assertThat(vistaEditar.urlActual(), containsString("editado=true"));
  }

  private void dadoQueElUsuarioEstaLogueado() {
    VistaReporte vistaReporte = new VistaReporte(context.pages().get(0));
    vistaReporte.iniciarSesionYNavegar("test@unlam.edu.ar", "test");
  }

  private void dadoQuePublicoUnReporte() {
    VistaReporte vistaReporte = new VistaReporte(context.pages().get(0));
    vistaReporte.escribirNombreProducto("Leche");
    vistaReporte.escribirMarcaProducto("La Serenisima");
    vistaReporte.escribirCategoriaProducto("Lacteos");
    vistaReporte.escribirUnidadProducto("1L");
    vistaReporte.escribirNombreComercio("Almacen Test");
    vistaReporte.escribirDireccionComercio("Av. Mitre 123");
    vistaReporte.escribirLocalidadComercio("Lomas de Zamora");
    vistaReporte.escribirPrecio("250");
    vistaReporte.darClickEnGuardarReporte();
  }
}
