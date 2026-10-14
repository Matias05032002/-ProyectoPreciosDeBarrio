package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.Lista.VistaLista;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaListaE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaLista vistaLista;

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
    vistaLista = new VistaLista(page, context);
    vistaLista.iniciarSesionYNavegar("test@unlam.edu.ar", "test");
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaMostrarLaPaginaMisListas() {
    assertThat(vistaLista.estaEnMisListas(), is(true));
  }

  @Test
  void deberiaCrearUnaListaYRedirigirAlDetalle() {
    vistaLista.darClickEnCrearLista();
    vistaLista.escribirNombreLista("Lista del súper");
    vistaLista.confirmarCrearLista();
    assertThat(vistaLista.estaEnDetalleLista(), is(true));
  }

  @Test
  void deberiaVerElNombreDeLaListaEnElDetalle() {
    vistaLista.darClickEnCrearLista();
    vistaLista.escribirNombreLista("Lista del súper");
    vistaLista.confirmarCrearLista();
    assertThat(vistaLista.elNombreDeLaListaEs("Lista del súper"), is(true));
  }

  @Test
  void deberiaVerLaListaEnMisListasDespuesDeCrearla() {
    vistaLista.darClickEnCrearLista();
    vistaLista.escribirNombreLista("Lista de prueba");
    vistaLista.confirmarCrearLista();
    vistaLista.volverAMisListas();
    assertThat(vistaLista.laListaExiste("Lista de prueba"), is(true));
  }

  @Test
  void deberiaVerListaVaciaAlCrearla() {
    vistaLista.darClickEnCrearLista();
    vistaLista.escribirNombreLista("Lista nueva");
    vistaLista.confirmarCrearLista();
    assertThat(vistaLista.laListaEstaVacia(), is(true));
  }

  @Test
  void deberiaEliminarUnItemDeLaLista() {
    ReiniciarDB.sembrarListaConItem();
    vistaLista.navegarADetalleLista(1L);
    assertThat(vistaLista.elItemEstaEnLaLista("Leche"), is(true));
    vistaLista.eliminarItem(1L);
    assertThat(vistaLista.laListaMuestraVacia(), is(true));
  }

  @Test
  void deberiaAgregarUnProductoAUnaListaYVerloEnElDetalle() {
    ReiniciarDB.sembrarProductoConReporte();
    vistaLista.darClickEnCrearLista();
    vistaLista.escribirNombreLista("Mi lista");
    vistaLista.confirmarCrearLista();
    vistaLista.buscarProducto("Leche");
    vistaLista.abrirModalAgregarLista(1L);
    vistaLista.seleccionarListaEnModal(1L);
    vistaLista.confirmarAgregarALista();
    assertThat(vistaLista.estaEnDetalleLista(), is(true));
    assertThat(vistaLista.elItemEstaEnLaLista("Leche"), is(true));
  }

  @Test
  void elTotalDeLaListaDeberiaActualizarseAlAgregarUnProducto() {
    ReiniciarDB.sembrarProductoConReporte();
    vistaLista.darClickEnCrearLista();
    vistaLista.escribirNombreLista("Lista total");
    vistaLista.confirmarCrearLista();
    vistaLista.buscarProducto("Leche");
    vistaLista.abrirModalAgregarLista(1L);
    vistaLista.seleccionarListaEnModal(1L);
    vistaLista.confirmarAgregarALista();
    assertThat(vistaLista.obtenerTotalDeLaLista(), containsString("100"));
  }

  @Test
  void deberiaEliminarUnaListaYNoApareceEnMisListas() {
    vistaLista.darClickEnCrearLista();
    vistaLista.escribirNombreLista("Lista a eliminar");
    vistaLista.confirmarCrearLista();
    vistaLista.volverAMisListas();
    assertThat(vistaLista.laListaExiste("Lista a eliminar"), is(true));
    vistaLista.eliminarLista(1L);
    assertThat(vistaLista.laListaExiste("Lista a eliminar"), is(false));
  }

  @Test
  void deberiaEditarLaCantidadDeUnItemYActualizarElTotal() {
    ReiniciarDB.sembrarProductoConReporte();
    vistaLista.darClickEnCrearLista();
    vistaLista.escribirNombreLista("Lista editar");
    vistaLista.confirmarCrearLista();
    vistaLista.agregarProductoDirecto(1L, 1L, 1);
    vistaLista.navegarADetalleLista(1L);
    assertThat(vistaLista.obtenerTotalDeLaLista(), containsString("100"));
    vistaLista.editarCantidadItem(1L, 3);
    assertThat(vistaLista.obtenerTotalDeLaLista(), containsString("300"));
  }
}
