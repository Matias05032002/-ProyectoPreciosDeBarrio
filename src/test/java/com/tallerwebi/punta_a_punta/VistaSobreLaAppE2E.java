package com.tallerwebi.punta_a_punta;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.VistaSobreLaApp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaSobreLaAppE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaSobreLaApp vistaSobreLaApp;

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
    vistaSobreLaApp = new VistaSobreLaApp(context.newPage());
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void unComercioDeberiaVerLasAccionesPensadasParaSuComercio() {
    vistaSobreLaApp.iniciarSesion("test-comercio@unlam.edu.ar", "test");
    vistaSobreLaApp.irASobreLaApp();

    assertThat(vistaSobreLaApp.accionCargarProductos()).isVisible();
    assertThat(vistaSobreLaApp.accionMisPromociones()).isVisible();
    assertThat(vistaSobreLaApp.accionDashboard()).isVisible();
    assertThat(vistaSobreLaApp.accionPerfilComercio()).isVisible();
  }

  @Test
  void unComercioNoDeberiaVerLasAccionesDelVecino() {
    vistaSobreLaApp.iniciarSesion("test-comercio@unlam.edu.ar", "test");
    vistaSobreLaApp.irASobreLaApp();

    assertThat(vistaSobreLaApp.accionConsultarProductos()).hasCount(0);
    assertThat(vistaSobreLaApp.accionExplorarComercios()).hasCount(0);
    assertThat(vistaSobreLaApp.accionArmarListas()).hasCount(0);
  }

  @Test
  void unAdminNoDeberiaVerLasAccionesDelComercio() {
    vistaSobreLaApp.iniciarSesion("test@unlam.edu.ar", "test");
    vistaSobreLaApp.irASobreLaApp();

    assertThat(vistaSobreLaApp.accionCargarProductos()).hasCount(0);
    assertThat(vistaSobreLaApp.accionDashboard()).hasCount(0);
  }
}
