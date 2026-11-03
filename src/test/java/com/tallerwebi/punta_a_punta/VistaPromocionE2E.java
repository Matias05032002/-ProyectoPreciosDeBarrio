package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.Comercio.VistaPromociones;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaPromocionE2E {

  private static final String TITULO = "2x1 en leche";
  private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern(
    "yyyy-MM-dd'T'HH:mm"
  );

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaPromociones vistaPromociones;

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
    ReiniciarDB.sembrarComercioDelUsuarioComercio();
    context = browser.newContext();
    vistaPromociones = new VistaPromociones(context.newPage());
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaMostrarMensajeCuandoElComercioNoTienePromociones() {
    dadoQueEstoyLogueadoComoComercio();
    vistaPromociones.irAMisPromociones();

    assertThat(vistaPromociones.obtenerTitulo(), containsString("Mis promociones"));
    assertThat(vistaPromociones.muestraMensajeSinPromociones(), is(true));
  }

  @Test
  void deberiaCrearUnaPromocionYVerlaEnMisPromociones() {
    dadoQueEstoyLogueadoComoComercio();
    vistaPromociones.irAMisPromociones();

    cuandoCreaUnaPromocionVigente();

    assertThat(vistaPromociones.hayPromocionConTitulo(TITULO), is(true));
    assertThat(vistaPromociones.obtenerEstadoDeLaPrimeraPromocion(), is("Activa"));
    assertThat(vistaPromociones.muestraMensajeSinPromociones(), is(false));
  }

  @Test
  void deberiaVerLaPromocionCreadaEnElListadoDePromocionesVigentes() {
    dadoQueEstoyLogueadoComoComercio();
    vistaPromociones.irAMisPromociones();
    cuandoCreaUnaPromocionVigente();

    vistaPromociones.irAPromocionesVigentes();

    assertThat(vistaPromociones.obtenerTitulo(), containsString("Promociones de comercios"));
    assertThat(vistaPromociones.hayPromocionConTitulo(TITULO), is(true));
  }

  @Test
  void deberiaDesactivarUnaPromocionYSacarlaDelListadoVigente() {
    dadoQueEstoyLogueadoComoComercio();
    vistaPromociones.irAMisPromociones();
    cuandoCreaUnaPromocionVigente();

    vistaPromociones.darClickEnDesactivarLaPrimeraPromocion();

    assertThat(vistaPromociones.obtenerEstadoDeLaPrimeraPromocion(), is("Inactiva"));
    assertThat(vistaPromociones.hayBotonDesactivar(), is(false));

    vistaPromociones.irAPromocionesVigentes();
    assertThat(vistaPromociones.hayPromocionConTitulo(TITULO), is(false));
  }

  @Test
  void deberiaMostrarErrorSiLaFechaDeFinEstaEnElPasado() {
    dadoQueEstoyLogueadoComoComercio();
    vistaPromociones.irAMisPromociones();

    LocalDateTime ahora = LocalDateTime.now();
    vistaPromociones.completarFormulario(
      TITULO,
      "1500",
      "Llevás dos y pagás uno",
      ahora.minusDays(5).format(FORMATO),
      ahora.minusDays(1).format(FORMATO)
    );
    vistaPromociones.darClickEnCrearPromocion();

    assertThat(vistaPromociones.muestraErrorDePromocion(), is(true));
    assertThat(vistaPromociones.hayPromocionConTitulo(TITULO), is(false));
  }

  @Test
  void deberiaRedirigirAlInicioSiQuienEntraNoEsUnComercio() {
    vistaPromociones.iniciarSesion("test@unlam.edu.ar", "test");
    vistaPromociones.irAMisPromociones();

    assertThat(context.pages().get(0).url(), containsString("/home"));
  }

  private void dadoQueEstoyLogueadoComoComercio() {
    vistaPromociones.iniciarSesion("test-comercio@unlam.edu.ar", "test");
  }

  private void cuandoCreaUnaPromocionVigente() {
    LocalDateTime ahora = LocalDateTime.now();
    vistaPromociones.completarFormulario(
      TITULO,
      "1500",
      "Llevás dos y pagás uno",
      ahora.minusDays(1).format(FORMATO),
      ahora.plusDays(7).format(FORMATO)
    );
    vistaPromociones.darClickEnCrearPromocion();
  }
}
