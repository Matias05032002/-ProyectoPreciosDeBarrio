package com.tallerwebi.punta_a_punta.vistas.Comercio;

import com.microsoft.playwright.Page;
import com.tallerwebi.punta_a_punta.vistas.VistaWeb;

public class VistaComercio extends VistaWeb {

  public VistaComercio(Page page) {
    super(page);
    page.navigate("localhost:8080/spring/comercio");
  }

  public String obtenerTituloDeLaPagina() {
    return this.obtenerTextoDelElemento("h2");
  }

  public boolean hayComerciosEnLaPagina() {
    return page.locator("#card-comercio").count() > 0;
  }

  public void escribirNombreDelComercioABuscar(String nombre) {
    this.escribirEnElElemento("#comercio-buscar", nombre);
  }

  public void darClickEnBuscar() {
    this.darClickEnElElemento("#btn-buscar-comercio");
  }

  public boolean existeBotonVerDetalle() {
    return page.locator("#btn-ver-detalle").count() > 0;
  }

  public String obtenerNombreDelPrimerComercio() {
    return page.locator(".card-title").first().textContent().trim();
  }
}
