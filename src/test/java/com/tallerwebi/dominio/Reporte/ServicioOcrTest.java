package com.tallerwebi.dominio.Reporte;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Gemini.ServicioGemini;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioOcrTest {

  private ServicioOcr servicioOcr;
  private ServicioGemini geminiClientMock;

  @BeforeEach
  public void init() {
    geminiClientMock = mock(ServicioGemini.class);
    servicioOcr = new ServicioOcr(geminiClientMock);
  }

  // --- ItemTicket ---

  @Test
  public void itemTicketGuardaNombreYPrecio() {
    ItemTicket item = new ItemTicket("Leche", 500.0);
    assertThat(item.getNombre(), equalTo("Leche"));
    assertThat(item.getPrecio(), equalTo(500.0));
  }

  @Test
  public void itemTicketSettersActualizanValores() {
    ItemTicket item = new ItemTicket("Leche", 500.0);
    item.setNombre("Aceite");
    item.setPrecio(1200.0);
    assertThat(item.getNombre(), equalTo("Aceite"));
    assertThat(item.getPrecio(), equalTo(1200.0));
  }

  @Test
  public void itemTicketGuardaCategoriaYMarca() {
    ItemTicket item = new ItemTicket("Leche", 500.0, "Lacteos", "La Serenisima");
    assertThat(item.getCategoria(), equalTo("Lacteos"));
    assertThat(item.getMarca(), equalTo("La Serenisima"));
  }

  @Test
  public void itemTicketUsaValoresPorDefectoCuandoFaltanCampos() {
    ItemTicket item = new ItemTicket("Leche", 500.0, null, null);
    assertThat(item.getCategoria(), equalTo("Varios"));
    assertThat(item.getMarca(), equalTo("Sin marca"));
  }

  @Test
  public void itemTicketGuardaUnidadConConstructorCompleto() {
    ItemTicket item = new ItemTicket("Leche", 500.0, "Lacteos", "La Serenisima", "1L");
    assertThat(item.getUnidad(), equalTo("1L"));
  }

  @Test
  public void itemTicketUsaUnidadPorDefectoCuandoSeRecibNull() {
    ItemTicket item = new ItemTicket("Aceite", 1200.0, "Almacen", "Natura", null);
    assertThat(item.getUnidad(), equalTo("1u"));
  }

  @Test
  public void itemTicketConstructorDosArgsUsaUnidadPorDefecto() {
    ItemTicket item = new ItemTicket("Pan", 300.0);
    assertThat(item.getUnidad(), equalTo("1u"));
  }

  @Test
  public void itemTicketConstructorCuatroArgsUsaUnidadPorDefecto() {
    ItemTicket item = new ItemTicket("Arroz", 800.0, "Cereales y legumbres", "Gallo");
    assertThat(item.getUnidad(), equalTo("1u"));
  }

  @Test
  public void itemTicketSetterUnidadActualizaValor() {
    ItemTicket item = new ItemTicket("Fideos", 400.0, "Almacen", "Don Satur", "500g");
    item.setUnidad("1kg");
    assertThat(item.getUnidad(), equalTo("1kg"));
  }

  @Test
  public void itemTicketSetterCategoriaActualizaValor() {
    ItemTicket item = new ItemTicket("Leche", 500.0, "Lacteos", "La Serenisima", "1L");
    item.setCategoria("Bebidas sin alcohol");
    assertThat(item.getCategoria(), equalTo("Bebidas sin alcohol"));
  }

  @Test
  public void itemTicketSetterMarcaActualizaValor() {
    ItemTicket item = new ItemTicket("Leche", 500.0, "Lacteos", "La Serenisima", "1L");
    item.setMarca("Ilolay");
    assertThat(item.getMarca(), equalTo("Ilolay"));
  }

  // --- ServicioOcr ---

  @Test
  public void extraerProductosDeTicket_conRespuestaValida_devuelveItems() throws Exception {
    String respuestaFake =
      "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"[{\\\"nombre\\\":\\\"Leche\\\",\\\"precio\\\":500,\\\"categoria\\\":\\\"Lacteos\\\",\\\"marca\\\":\\\"Serenisima\\\",\\\"unidad\\\":\\\"1L\\\"}]\"}]}}]}";
    when(geminiClientMock.llamar(anyString())).thenReturn(respuestaFake);

    List<ItemTicket> items = servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    assertThat(items.size(), equalTo(1));
    assertThat(items.get(0).getNombre(), equalTo("Leche"));
    assertThat(items.get(0).getUnidad(), equalTo("1L"));
  }

  @Test
  public void extraerProductosDeTicket_conErrorDeApi_devuelveListaVacia() throws Exception {
    when(geminiClientMock.llamar(anyString())).thenThrow(new Exception("Error de red"));

    List<ItemTicket> items = servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    assertThat(items.size(), equalTo(0));
  }

  @Test
  public void extraerProductos_ignoraProductosConPrecioCero() throws Exception {
    String respuestaFake =
      "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"[{\\\"nombre\\\":\\\"Aceite\\\",\\\"precio\\\":0,\\\"categoria\\\":\\\"Almacen\\\",\\\"marca\\\":null,\\\"unidad\\\":null}]\"}]}}]}";
    when(geminiClientMock.llamar(anyString())).thenReturn(respuestaFake);

    List<ItemTicket> items = servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    assertThat(items.size(), equalTo(0));
  }

  @Test
  public void reconocerProducto_conRespuestaValida_devuelveItemTicket() throws Exception {
    String respuestaFake =
      "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"nombre\\\":\\\"Fideos\\\",\\\"marca\\\":\\\"Don Satur\\\",\\\"categoria\\\":\\\"Almacen\\\",\\\"unidad\\\":\\\"500g\\\"}\"}]}}]}";
    when(geminiClientMock.llamar(anyString())).thenReturn(respuestaFake);

    ItemTicket item = servicioOcr.reconocerProducto(new byte[] { 1, 2, 3 });

    assertThat(item.getNombre(), equalTo("Fideos"));
    assertThat(item.getMarca(), equalTo("Don Satur"));
    assertThat(item.getUnidad(), equalTo("500g"));
  }

  @Test
  public void reconocerProducto_conCamposNull_usaValoresPorDefecto() throws Exception {
    String respuestaFake =
      "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"nombre\\\":\\\"Pan\\\",\\\"marca\\\":null,\\\"categoria\\\":null,\\\"unidad\\\":null}\"}]}}]}";
    when(geminiClientMock.llamar(anyString())).thenReturn(respuestaFake);

    ItemTicket item = servicioOcr.reconocerProducto(new byte[] { 1, 2, 3 });

    assertThat(item.getMarca(), equalTo("Sin marca"));
    assertThat(item.getCategoria(), equalTo("Varios"));
    assertThat(item.getUnidad(), equalTo("1u"));
  }
}
