package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.VistaAdminEstadisticas;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaAdminEstadisticasE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaAdminEstadisticas vistaAdmin;

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
    vistaAdmin = new VistaAdminEstadisticas(page);
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void sinSesionDeberiaRedirigirFueraDeLaPaginaDeEstadisticas() {
    vistaAdmin.navegarDirectamente();
    String url = vistaAdmin.obtenerURLActualPath();
    System.out.println("URL actual: " + url);
    assertThat(url, containsString("home"));
  }

  @Test
  void conRolAdminDeberiaMostrarPanelDeEstadisticas() {
    vistaAdmin.iniciarSesionComoAdmin("test@unlam.edu.ar", "test");
    System.out.println("URL tras login: " + vistaAdmin.obtenerURLActualPath());
    System.out.println("totalUsuarios visible: " + vistaAdmin.mostrarTotalUsuarios());
    System.out.println("totalReportes visible: " + vistaAdmin.mostrarTotalReportes());
    assertThat(vistaAdmin.mostrarTotalUsuarios(), is(true));
    assertThat(vistaAdmin.mostrarTotalReportes(), is(true));
  }

  @Test
  void conRolAdminDeberiaMostrarTituloCorrecto() {
    vistaAdmin.iniciarSesionComoAdmin("test@unlam.edu.ar", "test");
    System.out.println("URL tras login: " + vistaAdmin.obtenerURLActualPath());
    String titulo = vistaAdmin.obtenerTitulo();
    System.out.println("Titulo: " + titulo);
    assertThat(titulo, containsString("Estadísticas"));
  }
}
