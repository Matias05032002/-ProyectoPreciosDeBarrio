package com.tallerwebi.dominio.Reporte;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Gemini.ServicioGemini;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
    when(geminiClientMock.llamarGemini(anyString())).thenReturn(respuestaFake);

    List<ItemTicket> items = servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    assertThat(items.size(), equalTo(1));
    assertThat(items.get(0).getNombre(), equalTo("Leche"));
    assertThat(items.get(0).getUnidad(), equalTo("1L"));
  }

  @Test
  public void extraerProductosDeTicket_conErrorDeApi_propagaLaExcepcion() throws Exception {
    when(geminiClientMock.llamarGemini(anyString())).thenThrow(new Exception("Error de red"));

    assertThrows(
      Exception.class,
      () -> servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 })
    );
  }

  @Test
  public void extraerProductos_ignoraProductosConPrecioCero() throws Exception {
    String respuestaFake =
      "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"[{\\\"nombre\\\":\\\"Aceite\\\",\\\"precio\\\":0,\\\"categoria\\\":\\\"Almacen\\\",\\\"marca\\\":null,\\\"unidad\\\":null}]\"}]}}]}";
    when(geminiClientMock.llamarGemini(anyString())).thenReturn(respuestaFake);

    List<ItemTicket> items = servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    assertThat(items.size(), equalTo(0));
  }

  @Test
  public void extraerProductos_conPrecioComoTextoConFormatoArgentino_loConvierte()
    throws Exception {
    String respuestaFake =
      "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"[{\\\"nombre\\\":\\\"Queso\\\",\\\"precio\\\":\\\"$10.190,50\\\"}]\"}]}}]}";
    when(geminiClientMock.llamarGemini(anyString())).thenReturn(respuestaFake);

    List<ItemTicket> items = servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    assertThat(items.get(0).getPrecio(), equalTo(10190.50));
  }

  @Test
  public void extraerProductos_ignoraLasPartesDePensamientoDelModelo() throws Exception {
    String respuestaFake =
      "{\"candidates\":[{\"content\":{\"parts\":[" +
      "{\"text\":\"analizando el ticket...\",\"thought\":true}," +
      "{\"text\":\"[{\\\"nombre\\\":\\\"Ajo\\\",\\\"precio\\\":2490}]\"}]}}]}";
    when(geminiClientMock.llamarGemini(anyString())).thenReturn(respuestaFake);

    List<ItemTicket> items = servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    assertThat(items.size(), equalTo(1));
    assertThat(items.get(0).getNombre(), equalTo("Ajo"));
  }

  @Test
  public void extraerProductos_conListaEnvueltaEnUnObjeto_igualLaLee() throws Exception {
    String respuestaFake =
      "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"productos\\\":[{\\\"nombre\\\":\\\"Aquarius\\\",\\\"precio\\\":3100}]}\"}]}}]}";
    when(geminiClientMock.llamarGemini(anyString())).thenReturn(respuestaFake);

    List<ItemTicket> items = servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    assertThat(items.size(), equalTo(1));
    assertThat(items.get(0).getPrecio(), equalTo(3100.0));
  }

  @Test
  public void extraerProductos_siGeminiBloqueaLaImagen_lanzaExcepcion() throws Exception {
    when(geminiClientMock.llamarGemini(anyString()))
      .thenReturn("{\"promptFeedback\":{\"blockReason\":\"OTHER\"}}");

    assertThrows(
      IllegalStateException.class,
      () -> servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 })
    );
  }

  @Test
  public void extraerProductos_conFotoGrande_laAchicaAntesDeEnviarlaAGemini() throws Exception {
    byte[] fotoGrande = crearFotoJpeg(3000, 4000);
    when(geminiClientMock.llamarGemini(anyString()))
      .thenReturn("{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"[]\"}]}}]}");

    servicioOcr.extraerProductosDeTicket(fotoGrande);

    ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
    verify(geminiClientMock).llamarGemini(body.capture());
    int tamanioOriginalEnBase64 = (fotoGrande.length * 4) / 3;
    assertThat(body.getValue().length(), lessThan(tamanioOriginalEnBase64 / 3));
  }

  @Test
  public void extraerProductos_normalizaLaUnidadYUsaUnaUnidadSiNoEsValida() throws Exception {
    String respuestaFake =
      "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"[" +
      "{\\\"nombre\\\":\\\"Aquarius\\\",\\\"precio\\\":3100,\\\"unidad\\\":\\\"1,5 L\\\"}," +
      "{\\\"nombre\\\":\\\"Ajo\\\",\\\"precio\\\":2490,\\\"unidad\\\":\\\"por unidad\\\"}" +
      "]\"}]}}]}";
    when(geminiClientMock.llamarGemini(anyString())).thenReturn(respuestaFake);

    List<ItemTicket> items = servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    assertThat(items.get(0).getUnidad(), equalTo("1.5L"));
    assertThat(items.get(1).getUnidad(), equalTo("1u"));
  }

  @Test
  public void reconocerProducto_conRespuestaValida_devuelveItemTicket() throws Exception {
    String respuestaFake =
      "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"nombre\\\":\\\"Fideos\\\",\\\"marca\\\":\\\"Don Satur\\\",\\\"categoria\\\":\\\"Almacen\\\",\\\"unidad\\\":\\\"500g\\\"}\"}]}}]}";
    when(geminiClientMock.llamarGemini(anyString())).thenReturn(respuestaFake);

    ItemTicket item = servicioOcr.reconocerProducto(new byte[] { 1, 2, 3 });

    assertThat(item.getNombre(), equalTo("Fideos"));
    assertThat(item.getMarca(), equalTo("Don Satur"));
    assertThat(item.getUnidad(), equalTo("500g"));
  }

  @Test
  public void reconocerProducto_conCamposNull_usaValoresPorDefecto() throws Exception {
    String respuestaFake =
      "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"nombre\\\":\\\"Pan\\\",\\\"marca\\\":null,\\\"categoria\\\":null,\\\"unidad\\\":null}\"}]}}]}";
    when(geminiClientMock.llamarGemini(anyString())).thenReturn(respuestaFake);

    ItemTicket item = servicioOcr.reconocerProducto(new byte[] { 1, 2, 3 });

    assertThat(item.getMarca(), equalTo("Sin marca"));
    assertThat(item.getCategoria(), equalTo("Varios"));
    assertThat(item.getUnidad(), equalTo("1u"));
  }

  @Test
  public void extraerProductos_elPromptPideSoloElNombreRealDelProducto() throws Exception {
    when(geminiClientMock.llamarGemini(anyString()))
      .thenReturn("{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"[]\"}]}}]}");

    servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    ArgumentCaptor<String> pedido = ArgumentCaptor.forClass(String.class);
    verify(geminiClientMock).llamarGemini(pedido.capture());
    assertThat(pedido.getValue(), containsString("SOLO el nombre del producto real"));
    assertThat(pedido.getValue(), containsString("palabras completas"));
    assertThat(pedido.getValue(), containsString("abreviad"));
    assertThat(pedido.getValue(), containsString("MELBA RELLENA"));
    assertThat(pedido.getValue(), containsString("LECHE SEMIDESCRE"));
  }

  @Test
  public void extraerProductos_devuelveElNombreTalComoLoEntregaElModelo() throws Exception {
    String respuestaFake =
      "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"[{\\\"nombre\\\":\\\"Papa negra\\\",\\\"precio\\\":1200}]\"}]}}]}";
    when(geminiClientMock.llamarGemini(anyString())).thenReturn(respuestaFake);

    List<ItemTicket> items = servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    assertThat(items.get(0).getNombre(), equalTo("Papa negra"));
  }

  // --- helpers ---

  private byte[] crearFotoJpeg(int ancho, int alto) throws Exception {
    BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
    Random random = new Random(1);
    for (int y = 0; y < alto; y += 2) {
      for (int x = 0; x < ancho; x += 2) {
        imagen.setRGB(x, y, random.nextInt());
      }
    }
    ByteArrayOutputStream salida = new ByteArrayOutputStream();
    ImageIO.write(imagen, "jpg", salida);
    return salida.toByteArray();
  }

  @Test
  public void extraerProductos_siSoloSeLeeLaMarcaElPromptPideElTipoDeProducto() throws Exception {
    when(geminiClientMock.llamarGemini(anyString()))
      .thenReturn("{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"[]\"}]}}]}");

    servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    ArgumentCaptor<String> pedido = ArgumentCaptor.forClass(String.class);
    verify(geminiClientMock).llamarGemini(pedido.capture());
    assertThat(pedido.getValue(), containsString("Si en la línea solo aparece una marca"));
    assertThat(pedido.getValue(), containsString("'HELLMANNS' -> 'Mayonesa'"));
  }

  @Test
  public void extraerProductos_elPromptPideUnidadPorKiloParaLosProductosQueSePesan()
    throws Exception {
    when(geminiClientMock.llamarGemini(anyString()))
      .thenReturn("{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"[]\"}]}}]}");

    servicioOcr.extraerProductosDeTicket(new byte[] { 1, 2, 3 });

    ArgumentCaptor<String> pedido = ArgumentCaptor.forClass(String.class);
    verify(geminiClientMock).llamarGemini(pedido.capture());
    assertThat(pedido.getValue(), containsString("se vende por peso"));
    assertThat(pedido.getValue(), containsString("la unidad es '1kg'"));
  }
}
