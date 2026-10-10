package com.tallerwebi.punta_a_punta.vistas.Reporte;

import com.microsoft.playwright.Page;
import com.tallerwebi.punta_a_punta.vistas.VistaWeb;

public class VistaMisReportes extends VistaWeb {

  private static final String URL = "http://localhost:8080/spring/reporte/mis-reportes";

  public VistaMisReportes(Page page) {
    super(page);
    page.navigate(URL);
  }

  public boolean muestraMensajeSinReportes() {
    return this.page.locator("text=Todavía no publicaste ningún reporte").isVisible();
  }

  public boolean muestraReportes() {
    return this.page.locator("[id^='card-mi-reporte-']").count() > 0;
  }

  public int cantidadDeReportes() {
    return this.page.locator("[id^='card-mi-reporte-']").count();
  }
}
