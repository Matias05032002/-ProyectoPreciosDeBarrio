package com.tallerwebi.punta_a_punta;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.Comercio.VistaInicioComercio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaInicioComercioE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaInicioComercio vistaInicio;

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
    vistaInicio = new VistaInicioComercio(context.newPage());
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void conComercioDeberiaMostrarSuNombreYLasCifrasEnCero() {
    ReiniciarDB.sembrarComercioDelUsuarioComercio();
    vistaInicio.iniciarSesion("test-comercio@unlam.edu.ar", "test");
    vistaInicio.irAlInicio();

    assertThat(vistaInicio.nombreDelComercio()).hasText("Almacen Promo");
    assertThat(vistaInicio.cifraDePrecios()).hasText("0");
    assertThat(vistaInicio.cifraDeVecinos()).hasText("0");
    assertThat(vistaInicio.cifraDePromociones()).hasText("0");
    assertThat(vistaInicio.avisoSinComercio()).isHidden();
  }

  @Test
  void sinComercioDeberiaInvitarACrearlo() {
    vistaInicio.iniciarSesion("test-comercio@unlam.edu.ar", "test");
    vistaInicio.irAlInicio();

    assertThat(vistaInicio.avisoSinComercio()).isVisible();
    assertThat(vistaInicio.contenido()).isHidden();
  }

  @Test
  void unAdminNoDeberiaVerElInicioDeComercio() {
    vistaInicio.iniciarSesion("test@unlam.edu.ar", "test");
    vistaInicio.irAlInicio();

    assertThat(vistaInicio.nombreDelComercio()).hasCount(0);
  }
}
