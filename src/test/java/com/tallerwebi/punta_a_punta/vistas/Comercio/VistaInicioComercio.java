package com.tallerwebi.punta_a_punta.vistas.Comercio;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tallerwebi.punta_a_punta.vistas.VistaWeb;

public class VistaInicioComercio extends VistaWeb {

  private static final String URL_HOME = "http://localhost:8080/spring/home";

  public VistaInicioComercio(Page page) {
    super(page);
  }

  public void iniciarSesion(String email, String clave) {
    page.navigate("http://localhost:8080/spring/login");
    page.locator("#email").fill(email);
    page.locator("#password").fill(clave);
    page.locator("#btn-login").click();
    page.waitForLoadState();
  }

  public void irAlInicio() {
    page.navigate(URL_HOME);
  }

  public Locator nombreDelComercio() {
    return page.locator("#ic-nombre");
  }

  public Locator cifraDePrecios() {
    return page.locator("#ic-cifra-precios");
  }

  public Locator cifraDeVecinos() {
    return page.locator("#ic-cifra-vecinos");
  }

  public Locator cifraDePromociones() {
    return page.locator("#ic-cifra-promos");
  }

  public Locator avisoSinComercio() {
    return page.locator("#ic-sin-comercio");
  }

  public Locator contenido() {
    return page.locator("#ic-contenido");
  }
}
