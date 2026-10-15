package com.tallerwebi.dominio.Reporte;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.util.List;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.Test;

public class ServicioOcrTest {

  private final ServicioOcr servicioOcr = new ServicioOcr();

  @Test
  public void extraerProductosConBytesInvalidosDevuelveListaVacia() throws TesseractException {
    byte[] bytesInvalidos = new byte[] { 0, 1, 2, 3 };
    List<ItemTicket> resultado = servicioOcr.extraerProductosDeTicket(bytesInvalidos);
    assertThat(resultado, is(empty()));
  }

  @Test
  public void itemTicketGuardaNombreYPrecio() {
    ItemTicket item = new ItemTicket("Leche", 150.0);
    assertThat(item.getNombre(), is("Leche"));
    assertThat(item.getPrecio(), is(150.0));
  }

  @Test
  public void itemTicketSettersActualizanValores() {
    ItemTicket item = new ItemTicket("Leche", 150.0);
    item.setNombre("Agua");
    item.setPrecio(200.0);
    assertThat(item.getNombre(), is("Agua"));
    assertThat(item.getPrecio(), is(200.0));
  }

  @Test
  public void parsearTextoConLineaValidaDevuelveItem() {
    List<ItemTicket> items = servicioOcr.parsearTexto("Leche 150,00");
    assertThat(items.size(), is(1));
    assertThat(items.get(0).getNombre(), is("Leche"));
    assertThat(items.get(0).getPrecio(), is(150.0));
  }

  @Test
  public void parsearTextoConLineaVaciaDevuelveListaVacia() {
    List<ItemTicket> items = servicioOcr.parsearTexto("");
    assertThat(items, is(empty()));
  }

  @Test
  public void parsearTextoConPrecioFueraDeRangoNoAgregaItem() {
    List<ItemTicket> items = servicioOcr.parsearTexto("ProductoCaro 200000,00");
    assertThat(items, is(empty()));
  }

  @Test
  public void parsearTextoConMultiplesLineasDevuelveVariosItems() {
    String texto = "Leche 150,00\nAceite 250,50\nLinea sin precio";
    List<ItemTicket> items = servicioOcr.parsearTexto(texto);
    assertThat(items.size(), is(2));
  }
}
