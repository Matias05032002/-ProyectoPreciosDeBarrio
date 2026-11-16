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

public class ServicioImagenSupermercadoTest {

  private static final String FOTO = "https://carrefourar.vteximg.com.br/arquivos/ids/1/azucar.jpg";

  private ClienteHttp cliente;
  private ServicioImagenProducto servicio;

  @BeforeEach
  public void init() {
    cliente = mock(ClienteHttp.class);
    servicio = new ServicioImagenSupermercadoImpl("https://www.carrefour.com.ar", cliente);
  }

  private static String producto(String nombre, String marca, String foto) {
    return (
      "{\"productName\":\"" +
      nombre +
      "\",\"brand\":\"" +
      marca +
      "\",\"items\":[{\"images\":[{\"imageUrl\":\"" +
      foto +
      "\"}]}]}"
    );
  }

  private void dadaLaBusqueda(String... productos) throws Exception {
    doReturn("[" + String.join(",", productos) + "]").when(cliente).buscar(anyString());
  }

  @Test
  public void devuelveLaFotoDelProductoQueCoincide() throws Exception {
    dadaLaBusqueda(producto("Azúcar Ledesma molida superior bolsa 1 kg.", "Ledesma", FOTO));

    assertThat(servicio.buscarImagen("Azúcar molida superior", "Ledesma", "1kg"), is(FOTO));
  }

  @Test
  public void saltaElProductoDeOtraMarcaYTomaElQueCoincide() throws Exception {
    dadaLaBusqueda(
      producto("Azúcar común tipo A La Perla 1 kg", "La Perla", "https://x.vteximg.com.br/a.jpg"),
      producto("Azúcar común Bulnez 1 kg", "Bulnez", FOTO)
    );

    assertThat(servicio.buscarImagen("Azúcar común", "Bulnez", "1kg"), is(FOTO));
  }

  @Test
  public void noUsaLaFotoDeOtraMarca() throws Exception {
    dadaLaBusqueda(producto("Fideos tirabuzón Terrabusi 500 g.", "Terrabusi", FOTO));

    assertThat(servicio.buscarImagen("Fideos tirabuzón", "Cuquets", "500g"), is(nullValue()));
  }

  @Test
  public void conMarcaRealSinElTamanoExactoUsaLaFotoDelMismoProductoDeOtroTamano()
    throws Exception {
    dadaLaBusqueda(producto("Manteca Ilolay 200 g.", "Ilolay", FOTO));

    assertThat(servicio.buscarImagen("Manteca", "Ilolay", "100g"), is(FOTO));
  }

  @Test
  public void soloAceptaFotosDeVtexPorHttps() throws Exception {
    dadaLaBusqueda(
      producto("Azúcar común Bulnez 1 kg", "Bulnez", "https://sitio-malicioso.com/a.jpg"),
      producto("Azúcar común Bulnez 1 kg", "Bulnez", "https://vteximg.com.br.evil.com/b.jpg"),
      producto("Azúcar común Bulnez 1 kg", "Bulnez", "javascript:alert(1)"),
      producto("Azúcar común Bulnez 1 kg", "Bulnez", FOTO.replace("https", "http"))
    );

    assertThat(servicio.buscarImagen("Azúcar común", "Bulnez", "1kg"), is(FOTO));
  }

  @Test
  public void siLaRespuestaNoEsUnaListaODevuelveNull() throws Exception {
    doReturn("{\"error\":\"x\"}").when(cliente).buscar(anyString());
    assertThat(servicio.buscarImagen("Azúcar", "Bulnez", "1kg"), is(nullValue()));

    doReturn("<html>error</html>").when(cliente).buscar(anyString());
    assertThat(servicio.buscarImagen("Azúcar", "Bulnez", "1kg"), is(nullValue()));

    doReturn("[]").when(cliente).buscar(anyString());
    assertThat(servicio.buscarImagen("Azúcar", "Bulnez", "1kg"), is(nullValue()));
  }

  @Test
  public void siLaBusquedaFallaDevuelveNullSinLanzar() throws Exception {
    doThrow(new IOException("caido")).when(cliente).buscar(anyString());

    assertThat(servicio.buscarImagen("Azúcar", "Bulnez", "1kg"), is(nullValue()));
  }

  @Test
  public void laConsultaLlevaNombreYMarcaCodificados() throws Exception {
    dadaLaBusqueda();
    ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

    servicio.buscarImagen("Galletitas vainilla", "Porteñitas", "139g");

    verify(cliente).buscar(url.capture());
    assertThat(
      url.getValue(),
      containsString("https://www.carrefour.com.ar/api/catalog_system/pub/products/search?")
    );
    assertThat(url.getValue(), containsString("ft=Galletitas%20vainilla%20Porte%C3%B1itas"));
  }

  @Test
  public void sinMarcaBuscaSoloPorNombre() throws Exception {
    dadaLaBusqueda();
    ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

    servicio.buscarImagen("Leche", "Sin marca", null);

    verify(cliente).buscar(url.capture());
    assertThat(url.getValue().endsWith("ft=Leche"), is(true));
  }

  @Test
  public void sinNombreNoHaceNingunaLlamada() throws Exception {
    assertThat(servicio.buscarImagen(" ", "Bulnez", "1kg"), is(nullValue()));
    assertThat(servicio.buscarImagen(null, "Bulnez", "1kg"), is(nullValue()));

    verify(cliente, never()).buscar(anyString());
  }
}
