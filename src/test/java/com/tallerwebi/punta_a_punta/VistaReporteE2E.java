package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
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

public class VistaReporteE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaReporte vistaReporte;

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
    vistaReporte = new VistaReporte(page);
    vistaReporte.iniciarSesionYNavegar("test@unlam.edu.ar", "test");
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaMostrarElTituloDeReportes() {
    String titulo = vistaReporte.obtenerTitulo();
    assertThat(
      titulo,
      equalToIgnoringCase("Reporta el precio del producto para ayudar a tus vecinos")
    );
  }

  @Test
  void deberiaGuardarUnReporteYMostrarloEnLaTabla() {
    dadoQueElUsuarioCargaUnReporte(
      "Leche",
      "La Serenisima",
      "Tabaco",
      "1L",
      "Supermercado Dia",
      "Av. Mitre 123",
      "Lomas de Zamora",
      "250"
    );
    cuandoElUsuarioGuardaElReporte();
    entoncesDeberiaHaberReportesEnLaTabla();
  }

  @Test
  void deberiaMostrarLaPaginaDeReportesAlIniciarSesion() throws MalformedURLException {
    String url = vistaReporte.obtenerURLActual().getPath();
    assertThat(url.contains("reporte"), is(true));
  }

  @Test
  void deberiaValidarQueLosCamposSonObligatorios() {
    vistaReporte.darClickEnGuardarReporte();
    assertThat(vistaReporte.hayError(), is(true));
  }

  @Test
  void deberiaMostrarPreviewAlConfirmarElRecorte() {
    vistaReporte.subirFotoYConfirmarRecorte("src/test/resources/imagen-test.jpeg");
    assertThat(vistaReporte.hayPreviewDeFoto(), is(true));
  }

  @Test
  void deberiaGuardarElReporteConFotoRecortada() {
    vistaReporte.subirFotoYConfirmarRecorte("src/test/resources/imagen-test.jpeg");
    vistaReporte.escribirNombreProducto("Leche");
    vistaReporte.escribirMarcaProducto("La Serenisima");
    vistaReporte.escribirCategoriaProducto("Lacteos");
    vistaReporte.escribirUnidadProducto("1L");
    vistaReporte.escribirNombreComercio("Almacen De Matias");
    vistaReporte.escribirDireccionComercio("Av. Mitre 123");
    vistaReporte.escribirLocalidadComercio("Lomas de Zamora");
    vistaReporte.escribirPrecio("1000");
    vistaReporte.darClickEnGuardarReporte();
    assertThat(vistaReporte.hayMensajeGuardado(), is(true));
  }

  private static final String SUGERENCIA_CARHUE =
    "[{\"nomenclatura\":\"CARHUE 1526, Mataderos, Ciudad de Buenos Aires\"," +
    "\"direccion\":\"Carhue 1526\",\"localidad\":\"Mataderos\"," +
    "\"latitud\":-34.6595,\"longitud\":-58.5003,\"fuente\":\"georef\"}]";

  @Test
  void alBuscarEnElMapaDeberiaCorregirLaDireccionYConfirmarLaUbicacion() {
    vistaReporte.simularSugerenciasDeUbicacion(SUGERENCIA_CARHUE);
    dadoQueElUsuarioCompletaUnReporteConDireccionMalEscrita();

    vistaReporte.darClickEnBuscarEnElMapa();

    assertThat(vistaReporte.hayOpcionesDeUbicacion(), is(true));
    assertThat(vistaReporte.obtenerDireccionDelComercio(), is("Carhue 1526"));
    assertThat(vistaReporte.obtenerLatitudConfirmada(), is("-34.6595000"));
  }

  @Test
  void alCambiarLaDireccionDeberiaDescartarseLaUbicacionConfirmada() {
    vistaReporte.simularSugerenciasDeUbicacion(SUGERENCIA_CARHUE);
    dadoQueElUsuarioCompletaUnReporteConDireccionMalEscrita();
    vistaReporte.darClickEnBuscarEnElMapa();

    vistaReporte.escribirDireccionComercio(" bis");

    assertThat(vistaReporte.obtenerLatitudConfirmada(), emptyString());
  }

  @Test
  void deberiaGuardarElReporteConLaUbicacionConfirmadaYVerElComercioEnElMapa() {
    vistaReporte.simularSugerenciasDeUbicacion(SUGERENCIA_CARHUE);
    dadoQueElUsuarioCompletaUnReporteConDireccionMalEscrita();
    vistaReporte.darClickEnBuscarEnElMapa();

    vistaReporte.darClickEnGuardarReporte();
    assertThat(vistaReporte.hayMensajeGuardado(), is(true));

    VistaComercio vistaComercio = new VistaComercio(context.pages().get(0));
    vistaComercio.darClickEnVerDetalle();
    assertThat(vistaComercio.hayMapaConMarcadorEnElDetalle(), is(true));
  }

  private void dadoQueElUsuarioCompletaUnReporteConDireccionMalEscrita() {
    vistaReporte.escribirNombreProducto("Leche");
    vistaReporte.escribirMarcaProducto("La Serenisima");
    vistaReporte.escribirCategoriaProducto("Lacteos");
    vistaReporte.escribirUnidadProducto("1L");
    vistaReporte.escribirNombreComercio("Almacen Carhue");
    vistaReporte.escribirDireccionComercio("crhue 1526");
    vistaReporte.escribirLocalidadComercio("Buenos Aires");
    vistaReporte.escribirPrecio("250");
  }

  private void dadoQueElUsuarioCargaUnReporte(
    String nombreProducto,
    String marca,
    String categoria,
    String unidad,
    String nombreComercio,
    String direccion,
    String localidad,
    String precio
  ) {
    vistaReporte.escribirNombreProducto(nombreProducto);
    vistaReporte.escribirMarcaProducto(marca);
    vistaReporte.escribirCategoriaProducto(categoria);
    vistaReporte.escribirUnidadProducto(unidad);
    vistaReporte.escribirNombreComercio(nombreComercio);
    vistaReporte.escribirDireccionComercio(direccion);
    vistaReporte.escribirLocalidadComercio(localidad);
    vistaReporte.escribirPrecio(precio);
  }

  private void cuandoElUsuarioGuardaElReporte() {
    vistaReporte.darClickEnGuardarReporte();
  }

  private void entoncesDeberiaHaberReportesEnLaTabla() {
    assertThat(vistaReporte.hayReportesEnLaTabla(), is(true));
  }
}
