package com.tallerwebi.punta_a_punta.vistas.Reporte;

import com.microsoft.playwright.Page;
import com.tallerwebi.punta_a_punta.vistas.VistaWeb;

public class VistaMisProductos extends VistaWeb {

  private static final String URL = "http://localhost:8080/spring/reporte/mis-productos";

  public VistaMisProductos(Page page) {
    super(page);
    page.navigate(URL);
  }

  public boolean muestraMensajeSinProductos() {
    return this.page.locator("text=Todavía no cargaste productos en tu comercio").isVisible();
  }

  public boolean muestraProductos() {
    return this.page.locator(".card").count() > 0;
  }
}
