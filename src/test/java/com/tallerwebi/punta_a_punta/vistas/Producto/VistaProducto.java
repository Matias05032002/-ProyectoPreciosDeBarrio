package com.tallerwebi.punta_a_punta.vistas.Producto;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tallerwebi.punta_a_punta.vistas.VistaWeb;

public class VistaProducto extends VistaWeb {

  public VistaProducto(Page page) {
    super(page);
    page.navigate("localhost:8080/spring/producto");
  }

  public String obtenerTituloDeLaPagina() {
    return this.obtenerTextoDelElemento("h2");
  }

  public void escribirNombreDelProductoABuscar(String nombre) {
    this.escribirEnElElemento("#buscador-producto", nombre);
  }

  public void darClickEnBuscar() {
    page.locator("#btn-buscar-producto").click();
    page.waitForLoadState();
  }

  public boolean hayProductosEnLaPagina() {
    return page.locator("[id^='card-producto']").count() > 0;
  }

  public boolean hayBotonMarcarDudoso() {
    return page.locator("[id^='btn-marcar-dudoso']").count() > 0;
  }

  public boolean hayModalDudoso() {
    return page.locator("#modalDudoso").count() > 0;
  }

  public void darClickEnMarcarDudoso() {
    page.locator("[id^='btn-marcar-dudoso']").first().click();
    page.waitForLoadState();
  }

  public void darClickEnFotoDelProducto() {
    page.locator("img.card-img-top").first().click();
  }

  public void darClickEnPlaceholderDeProducto() {
    page.waitForSelector(
      ".card-img-placeholder[style*='flex']",
      new Page.WaitForSelectorOptions().setTimeout(5000)
    );
    page.evaluate("new bootstrap.Modal(document.getElementById('modalFoto')).show()");
  }

  public boolean hayModalFoto() {
    page.waitForTimeout(1000);
    return page.locator("#modalFoto").isVisible();
  }

  public boolean hayPlaceholderVisible() {
    return page.locator(".card-img-placeholder").first().isVisible();
  }
}
