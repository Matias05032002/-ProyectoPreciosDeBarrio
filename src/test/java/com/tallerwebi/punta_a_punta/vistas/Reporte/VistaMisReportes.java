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

  public boolean hayBotonEditar() {
    return this.page.locator("a[href*='/editar']").count() > 0;
  }

  public void darClickEnPrimerBotonEditar() {
    this.page.locator("a[href*='/editar']").first().click();
  }

  public boolean hayBotonEliminar() {
    return this.page.locator("[id^='btn-eliminar-reporte-']").count() > 0;
  }

  public void darClickEnPrimerBotonEliminar() {
    this.page.locator("[id^='btn-eliminar-reporte-']").first().click();
  }

  public void confirmarEliminacion() {
    this.page.locator("[id^='btn-confirmar-eliminar-reporte-']:visible").first().click();
  }

  public void cancelarEliminacion() {
    this.page.locator("[id^='btn-cancelar-eliminar-reporte-']:visible").first().click();
  }

  public boolean hayMensajeEliminado() {
    try {
      this.page.waitForSelector(
          "#alerta-reporte-eliminado",
          new Page.WaitForSelectorOptions().setTimeout(3000)
        );
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hayMensajeEditado() {
    try {
      this.page.waitForSelector(
          ".alert-success",
          new Page.WaitForSelectorOptions().setTimeout(3000)
        );
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
