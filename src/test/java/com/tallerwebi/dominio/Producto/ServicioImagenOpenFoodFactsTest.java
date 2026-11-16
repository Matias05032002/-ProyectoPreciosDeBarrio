package com.tallerwebi.dominio.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tallerwebi.dominio.Producto.ServicioImagenDeCatalogo.ClienteHttp;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioImagenOpenFoodFactsTest {

  private static final String FOTO =
    "https://images.openfoodfacts.org/images/products/779/254/025/0450/front_es.8.400.jpg";

  private ClienteHttp cliente;
  private ServicioImagenProducto servicio;

  @BeforeEach
  public void init() {
    cliente = mock(ClienteHttp.class);
    servicio = new ServicioImagenOpenFoodFactsImpl(cliente);
  }

  private static String producto(String nombre, String marca, String tamano, String foto) {
    return (
      "{\"product_name\":\"" +
      nombre +
      "\",\"brands\":\"" +
      marca +
      "\",\"quantity\":\"" +
      tamano +
      "\",\"image_front_url\":\"" +
      foto +
      "\"}"
    );
  }

  private void dadaLaBusqueda(String... productos) throws Exception {
    doReturn("{\"products\":[" + String.join(",", productos) + "]}")
      .when(cliente)
      .buscar(anyString());
  }

  @Test
  public void devuelveLaFotoDelProductoQueCoincide() throws Exception {
    dadaLaBusqueda(producto("Azúcar Superior", "Ledesma", "1 kg", FOTO));

    assertThat(servicio.buscarImagen("Azúcar superior", "Ledesma", "1kg"), is(FOTO));
  }

  @Test
  public void elTamanoSeLeeDelCampoQuantity() throws Exception {
    dadaLaBusqueda(
      producto("Café", "Arlistan", "100 g", "https://images.openfoodfacts.org/a.jpg"),
      producto("Café", "Arlistan", "170 g", FOTO)
    );

    assertThat(servicio.buscarImagen("Café", "Arlistan", "170g"), is(FOTO));
  }

  @Test
  public void noUsaLaFotoDeOtraMarca() throws Exception {
    dadaLaBusqueda(producto("Fideos tirabuzón", "Terrabusi", "500 g", FOTO));

    assertThat(servicio.buscarImagen("Fideos tirabuzón", "Cuquets", "500g"), is(nullValue()));
  }

  @Test
  public void conMarcaRealSinElTamanoExactoUsaLaFotoDelMismoProductoDeOtroTamano()
    throws Exception {
    dadaLaBusqueda(producto("Harina de trigo 000", "Morixe", "1 kg", FOTO));

    assertThat(servicio.buscarImagen("Harina de trigo 000", "Morixe", "5kg"), is(FOTO));
  }

  @Test
  public void soloAceptaFotosDeOpenFoodFactsPorHttps() throws Exception {
    dadaLaBusqueda(
      producto("Azúcar Superior", "Ledesma", "1 kg", "https://sitio-malicioso.com/a.jpg"),
      producto("Azúcar Superior", "Ledesma", "1 kg", "https://openfoodfacts.org.evil.com/b.jpg"),
      producto("Azúcar Superior", "Ledesma", "1 kg", "javascript:alert(1)"),
      producto("Azúcar Superior", "Ledesma", "1 kg", FOTO.replace("https", "http"))
    );

    assertThat(servicio.buscarImagen("Azúcar superior", "Ledesma", "1kg"), is(FOTO));
  }

  @Test
  public void unProductoSinFotoSeSalta() throws Exception {
    dadaLaBusqueda(
      "{\"product_name\":\"Azúcar Superior\",\"brands\":\"Ledesma\",\"quantity\":\"1 kg\"}",
      producto("Azúcar Superior", "Ledesma", "1 kg", FOTO)
    );

    assertThat(servicio.buscarImagen("Azúcar superior", "Ledesma", "1kg"), is(FOTO));
  }

  @Test
  public void siLaRespuestaEsInvalidaODevuelveNull() throws Exception {
    doReturn("<html>503</html>").when(cliente).buscar(anyString());
    assertThat(servicio.buscarImagen("Azúcar", "Ledesma", "1kg"), is(nullValue()));

    doReturn("{}").when(cliente).buscar(anyString());
    assertThat(servicio.buscarImagen("Azúcar", "Ledesma", "1kg"), is(nullValue()));
  }

  @Test
  public void siLaBusquedaFallaDevuelveNullSinLanzar() throws Exception {
    doThrow(new IOException("503")).when(cliente).buscar(anyString());

    assertThat(servicio.buscarImagen("Azúcar", "Ledesma", "1kg"), is(nullValue()));
  }

  @Test
  public void laConsultaLlevaNombreYMarcaCodificados() throws Exception {
    dadaLaBusqueda();
    ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

    servicio.buscarImagen("Fideos moños", "Lucchetti", "500g");

    verify(cliente).buscar(url.capture());
    assertThat(url.getValue(), containsString("https://world.openfoodfacts.org/cgi/search.pl?"));
    assertThat(url.getValue(), containsString("search_terms=Fideos%20mo%C3%B1os%20Lucchetti"));
  }

  @Test
  public void sinNombreNoHaceNingunaLlamada() throws Exception {
    assertThat(servicio.buscarImagen(" ", "Ledesma", "1kg"), is(nullValue()));
    assertThat(servicio.buscarImagen(null, "Ledesma", "1kg"), is(nullValue()));

    verify(cliente, never()).buscar(anyString());
  }
}
