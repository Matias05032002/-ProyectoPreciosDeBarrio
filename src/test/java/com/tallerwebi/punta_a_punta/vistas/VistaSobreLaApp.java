package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VistaSobreLaApp extends VistaWeb {

  private static final String URL_SOBRE_LA_APP = "http://localhost:8080/spring/sobre-la-app";

  public VistaSobreLaApp(Page page) {
    super(page);
  }

  public void iniciarSesion(String email, String clave) {
    page.navigate("http://localhost:8080/spring/login");
    page.locator("#email").fill(email);
    page.locator("#password").fill(clave);
    page.locator("#btn-login").click();
    page.waitForLoadState();
  }

  public void irASobreLaApp() {
    page.navigate(URL_SOBRE_LA_APP);
  }

  public Locator accionCargarProductos() {
    return page.locator("#accion-cargar-productos");
  }

  public Locator accionMisPromociones() {
    return page.locator("#accion-mis-promociones");
  }

  public Locator accionDashboard() {
    return page.locator("#accion-dashboard");
  }

  public Locator accionPerfilComercio() {
    return page.locator("#accion-perfil-comercio");
  }

  public Locator accionConsultarProductos() {
    return page.locator("#accion-consultar-productos");
  }

  public Locator accionExplorarComercios() {
    return page.locator("#accion-explorar-comercios");
  }

  public Locator accionArmarListas() {
    return page.locator("#accion-armar-listas");
  }
}
