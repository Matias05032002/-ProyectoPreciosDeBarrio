package com.tallerwebi.punta_a_punta.vistas.Comercio;

import com.microsoft.playwright.Page;
import com.tallerwebi.punta_a_punta.vistas.VistaWeb;
import java.util.List;

public class VistaComercio extends VistaWeb {

  public VistaComercio(Page page) {
    super(page);
    page.navigate("localhost:8080/spring/comercio");
  }

  public String obtenerTituloDeLaPagina() {
    return this.obtenerTextoDelElemento("#titulo-pagina");
  }

  public boolean hayComerciosEnLaPagina() {
    return page.locator("[id^='card-comercio-']").count() > 0;
  }

  public void escribirNombreDelComercioABuscar(String nombre) {
    this.escribirEnElElemento("#comercio-buscar", nombre);
  }

  public void darClickEnBuscar() {
    this.darClickEnElElemento("#btn-buscar-comercio");
  }

  public boolean existeBotonVerDetalle() {
    return page.locator("[id^='btn-ver-detalle-']").count() > 0;
  }

  public String obtenerNombreDelPrimerComercio() {
    return page.locator("[id^='nombre-comercio-']").first().textContent().trim();
  }

  public boolean hayMapaConMarcadorEnElDetalle() {
    try {
      page.waitForSelector(
        ".leaflet-marker-icon",
        new Page.WaitForSelectorOptions().setTimeout(8000)
      );
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public List<String> obtenerNombresDeLosComercios() {
    return page
      .locator("[id^='nombre-comercio-']")
      .allTextContents()
      .stream()
      .map(String::trim)
      .toList();
  }

  public void darClickEnVerDetalle() {
    page.locator("[id^='btn-ver-detalle-']").first().click();
    page.waitForURL("**/comercio/*");
  }

  public boolean hayProductosEnElDetalle() {
    return page.locator("[id^='card-producto-comercio-']").count() > 0;
  }

  public boolean hayMapaEnElDetalle() {
    return page.locator(".leaflet-marker-icon").count() > 0;
  }
}
