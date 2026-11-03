package com.tallerwebi.punta_a_punta.vistas.Comercio;

import com.microsoft.playwright.Page;
import com.tallerwebi.punta_a_punta.vistas.VistaWeb;

public class VistaPromociones extends VistaWeb {

  private static final String URL_MIS_PROMOCIONES =
    "http://localhost:8080/spring/promociones/mis-promociones";
  private static final String URL_PROMOCIONES = "http://localhost:8080/spring/promociones";

  public VistaPromociones(Page page) {
    super(page);
  }

  public void iniciarSesion(String email, String clave) {
    page.navigate("http://localhost:8080/spring/login");
    page.locator("#email").fill(email);
    page.locator("#password").fill(clave);
    page.locator("#btn-login").click();
  }

  public void irAMisPromociones() {
    page.navigate(URL_MIS_PROMOCIONES);
  }

  public void irAPromocionesVigentes() {
    page.navigate(URL_PROMOCIONES);
  }

  public String obtenerTitulo() {
    return this.obtenerTextoDelElemento("#titulo-pagina");
  }

  public void completarFormulario(
    String titulo,
    String precio,
    String descripcion,
    String fechaInicio,
    String fechaFin
  ) {
    page.locator("#titulo").fill(titulo);
    page.locator("#precioPromocional").fill(precio);
    page.locator("#descripcion").fill(descripcion);
    page.locator("#fechaInicio").fill(fechaInicio);
    page.locator("#fechaFin").fill(fechaFin);
  }

  public void darClickEnCrearPromocion() {
    page.locator("#btn-crear-promocion").click();
    page.waitForLoadState();
  }

  public boolean hayPromocionConTitulo(String titulo) {
    return page
      .locator("[id^='titulo-promocion-']")
      .allTextContents()
      .stream()
      .anyMatch(t -> t.contains(titulo));
  }

  public boolean muestraMensajeSinPromociones() {
    return page.locator("#mensaje-sin-promociones").isVisible();
  }

  public boolean muestraErrorDePromocion() {
    return page.locator("#error-promocion").isVisible();
  }

  public String obtenerEstadoDeLaPrimeraPromocion() {
    return page.locator("[id^='estado-promocion-']").first().textContent().trim();
  }

  public void darClickEnDesactivarLaPrimeraPromocion() {
    page.locator("[id^='btn-desactivar-promocion-']").first().click();
    page.waitForLoadState();
  }

  public boolean hayBotonDesactivar() {
    return page.locator("[id^='btn-desactivar-promocion-']").count() > 0;
  }
}
