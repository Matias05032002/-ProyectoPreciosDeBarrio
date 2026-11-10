package com.tallerwebi.dominio.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Producto.ServicioImagenProductoImpl.ClienteHttp;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioImagenProductoTest {

  private static final String TOKEN_VALIDO =
    "{\"access_token\":\"APP_USR-123\",\"expires_in\":21600}";
  private static final String TOKEN_VENCIDO =
    "{\"access_token\":\"APP_USR-viejo\",\"expires_in\":0}";
  private static final String FOTO_HTTP = "http://http2.mlstatic.com/D_NQ_NP_841456-MLA1111-F.jpg";
  private static final String FOTO = "https://http2.mlstatic.com/D_NQ_NP_841456-MLA1111-F.jpg";

  private ClienteHttp cliente;
  private ServicioImagenProducto servicio;

  @BeforeEach
  public void init() throws Exception {
    cliente = mock(ClienteHttp.class);
    when(cliente.pedirToken("mi-id", "mi-secreto")).thenReturn(TOKEN_VALIDO);
    servicio = new ServicioImagenProductoImpl("mi-id", "mi-secreto", cliente);
  }

  private static String resultado(String... fotos) {
    return resultadoDe("Leche entera La Serenisima", fotos);
  }

  private static String resultadoConMarca(String nombre, String marca, String foto) {
    return (
      "{\"name\":\"" +
      nombre +
      "\",\"attributes\":[{\"id\":\"BRAND\",\"value_name\":\"" +
      marca +
      "\"}],\"pictures\":[{\"url\":\"" +
      foto +
      "\"}]}"
    );
  }

  private static String resultadoDe(String nombre, String... fotos) {
    StringBuilder pictures = new StringBuilder();
    for (String foto : fotos) {
      pictures
        .append(pictures.length() == 0 ? "" : ",")
        .append("{\"url\":\"")
        .append(foto)
        .append("\"}");
    }
    return "{\"name\":\"" + nombre + "\",\"pictures\":[" + pictures + "]}";
  }

  private static String busqueda(String... resultados) {
    return "{\"results\":[" + String.join(",", resultados) + "]}";
  }

  private void dadaLaBusqueda(String json) throws Exception {
    doReturn(json).when(cliente).buscar(anyString(), anyString());
  }

  @Test
  public void devuelveLaPrimeraFotoDelPrimerResultado() throws Exception {
    dadaLaBusqueda(busqueda(resultado(FOTO, "https://http2.mlstatic.com/otra-F.jpg")));

    assertThat(servicio.buscarImagen("Leche", "La Serenisima", null), is(FOTO));
  }

  @Test
  public void laFotoSiempreSeDevuelvePorHttps() throws Exception {
    dadaLaBusqueda(busqueda(resultado(FOTO_HTTP)));

    assertThat(servicio.buscarImagen("Leche", "La Serenisima", null), is(FOTO));
  }

  @Test
  public void saltaLasFotosInvalidasDentroDeUnMismoResultado() throws Exception {
    dadaLaBusqueda(busqueda(resultado("https://sitio-malicioso.com/a-F.jpg", FOTO)));

    assertThat(servicio.buscarImagen("Leche", "La Serenisima", null), is(FOTO));
  }

  @Test
  public void saltaLosResultadosSinImagenValida() throws Exception {
    dadaLaBusqueda(
      busqueda(
        "{\"name\":\"sin fotos\"}",
        "{\"name\":\"fotos vacias\",\"pictures\":[]}",
        resultado("https://sitio-malicioso.com/a-F.jpg"),
        resultado("https://http2.mlstatic.com.evil.com/b-F.jpg"),
        resultado("javascript:alert(1)"),
        resultado(FOTO)
      )
    );

    assertThat(servicio.buscarImagen("Leche", "La Serenisima", null), is(FOTO));
  }

  @Test
  public void siNingunResultadoTieneImagenValidaDevuelveNull() throws Exception {
    dadaLaBusqueda(busqueda("{\"name\":\"sin fotos\"}", resultado("ftp://x.com/a.jpg")));

    assertThat(servicio.buscarImagen("Leche", "La Serenisima", null), is(nullValue()));
  }

  @Test
  public void sinResultadosODevuelveNull() throws Exception {
    dadaLaBusqueda(busqueda());
    assertThat(servicio.buscarImagen("Leche", "La Serenisima", null), is(nullValue()));

    dadaLaBusqueda("{}");
    assertThat(servicio.buscarImagen("Leche", "La Serenisima", null), is(nullValue()));
  }

  @Test
  public void unJsonInvalidoDevuelveNullSinLanzar() throws Exception {
    dadaLaBusqueda("<html>error</html>");

    assertThat(servicio.buscarImagen("Leche", "La Serenisima", null), is(nullValue()));
  }

  @Test
  public void noUsaLaFotoDeUnProductoDeOtroNombre() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Auriculares KZ Zs10 Red Negro", FOTO)));

    assertThat(servicio.buscarImagen("ZAPALLITO RED GRANEL", null, null), is(nullValue()));
  }

  @Test
  public void saltaElResultadoDeOtroProductoYTomaElQueCoincide() throws Exception {
    dadaLaBusqueda(
      busqueda(
        resultadoDe("Pepas de batata Palluzzi", "https://http2.mlstatic.com/otro-F.jpg"),
        resultadoDe("Bombilla de acero inoxidable", "https://http2.mlstatic.com/kit-F.jpg"),
        resultadoDe("Yerba Mate Taragui 4flex 1kg", FOTO)
      )
    );

    assertThat(servicio.buscarImagen("Yerba Taragui", null, null), is(FOTO));
  }

  @Test
  public void elNombreDelTicketAbreviadoCoincidePorPrefijo() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Toallitas Femeninas Nocturnas con Alas Nosotras", FOTO)));

    assertThat(servicio.buscarImagen("TOALL FEM NOCT ALAS", null, null), is(FOTO));
  }

  @Test
  public void ignoraMayusculasYTildes() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Café Instantáneo Dolca 500 ml", FOTO)));

    assertThat(servicio.buscarImagen("CAFE", "Dolca", null), is(FOTO));
  }

  @Test
  public void laPalabraPrincipalDelNombreTieneQueCoincidir() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Chocolate triple Milka 100 g", FOTO)));

    assertThat(servicio.buscarImagen("Alfajor triple chocolate", null, null), is(nullValue()));
  }

  @Test
  public void todasLasPalabrasDelNombreTienenQueCoincidir() throws Exception {
    dadaLaBusqueda(busqueda(resultadoConMarca("Gorra Melba Rosa", "Melba", FOTO)));

    assertThat(servicio.buscarImagen("MELBA RELLENA", "MELBA", "360g"), is(nullValue()));
  }

  @Test
  public void noUsaUnProductoMuchoMasLargoQueElBuscado() throws Exception {
    dadaLaBusqueda(
      busqueda(resultadoDe("Vinagre De Manzana Capsulas Con Bicarbonato Y Jengibre Life180", FOTO))
    );

    assertThat(servicio.buscarImagen("BICARBONATO", null, "1u"), is(nullValue()));
  }

  @Test
  public void aceptaUnResultadoConPocasPalabrasDeMas() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Bicarbonato De Sodio Puro 500 g", FOTO)));

    assertThat(servicio.buscarImagen("BICARBONATO", null, "500g"), is(FOTO));
  }

  @Test
  public void unNombreSinPalabrasUtilesNoBuscaFoto() throws Exception {
    dadaLaBusqueda(busqueda(resultado(FOTO)));

    assertThat(servicio.buscarImagen("S/N", null, null), is(nullValue()));
  }

  @Test
  public void noUsaLaFotoDeOtraMarca() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Yerba Mate Playadito 1kg", FOTO)));

    assertThat(servicio.buscarImagen("Yerba Mate", "Taragui", "1kg"), is(nullValue()));
  }

  @Test
  public void aceptaLaMarcaEnElNombreSinTildesNiMayusculas() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Yerba Mate TARAGÜÍ 1kg", FOTO)));

    assertThat(servicio.buscarImagen("Yerba Mate", "Taragui", "1kg"), is(FOTO));
  }

  @Test
  public void aceptaLaMarcaQueSoloFiguraEnLosAtributosDelCatalogo() throws Exception {
    dadaLaBusqueda(busqueda(resultadoConMarca("Yerba Mate Energia 1kg", "Taragüí", FOTO)));

    assertThat(servicio.buscarImagen("Yerba Mate", "Taragui", "1kg"), is(FOTO));
  }

  @Test
  public void saltaLaMarcaEquivocadaYTomaLaCorrecta() throws Exception {
    dadaLaBusqueda(
      busqueda(
        resultadoDe("Yerba Mate Playadito 1kg", "https://http2.mlstatic.com/otra-F.jpg"),
        resultadoDe("Yerba Mate Taragui 1kg", FOTO)
      )
    );

    assertThat(servicio.buscarImagen("Yerba Mate", "Taragui", "1kg"), is(FOTO));
  }

  @Test
  public void sinMarcaONoExigeMarca() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Yerba Mate Playadito 1kg", FOTO)));

    assertThat(servicio.buscarImagen("Yerba Mate", null, "1kg"), is(FOTO));
    assertThat(servicio.buscarImagen("Yerba Mate", "Sin marca", "1kg"), is(FOTO));
    assertThat(servicio.buscarImagen("Yerba Mate", " ", "1kg"), is(FOTO));
  }

  @Test
  public void conMarcaRealSinElTamanoExactoUsaLaFotoDelMismoProductoDeOtroTamano()
    throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Yerba Mate Taragui 500 g", FOTO)));

    assertThat(servicio.buscarImagen("Yerba Mate", "Taragui", "1kg"), is(FOTO));
  }

  @Test
  public void siHayUnResultadoDelTamanoExactoGanaAlDeOtroTamano() throws Exception {
    String otroTamano = "https://http2.mlstatic.com/otro-F.jpg";
    dadaLaBusqueda(
      busqueda(
        resultadoDe("Yerba Mate Taragui 500 g", otroTamano),
        resultadoDe("Yerba Mate Taragui 1 kg", FOTO)
      )
    );

    assertThat(servicio.buscarImagen("Yerba Mate", "Taragui", "1kg"), is(FOTO));
  }

  @Test
  public void sinMarcaRealSigueExigiendoElTamanoExacto() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Yerba Mate Taragui 500 g", FOTO)));

    assertThat(servicio.buscarImagen("Yerba Mate", null, "1kg"), is(nullValue()));
    assertThat(servicio.buscarImagen("Yerba Mate", "Sin marca", "1kg"), is(nullValue()));
  }

  @Test
  public void aunSinElTamanoExactoNoUsaLaFotoDeOtraMarca() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Yerba Mate Playadito 500 g", FOTO)));

    assertThat(servicio.buscarImagen("Yerba Mate", "Taragui", "1kg"), is(nullValue()));
  }

  @Test
  public void aceptaLaMismaMedidaEscritaDeOtraForma() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Yerba Mate Taragui 1000 gr", FOTO)));
    assertThat(servicio.buscarImagen("Yerba Mate", "Taragui", "1kg"), is(FOTO));

    dadaLaBusqueda(busqueda(resultadoDe("Aceite Cocinero 1.5 L", FOTO)));
    assertThat(servicio.buscarImagen("Aceite", "Cocinero", "1500ml"), is(FOTO));

    dadaLaBusqueda(busqueda(resultadoDe("Leche Larga Vida 1 litro", FOTO)));
    assertThat(servicio.buscarImagen("Leche", null, "1l"), is(FOTO));
  }

  @Test
  public void siElResultadoNoDiceElTamanoYNoHayMarcaNoLoUsa() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Yerba Mate Taragui", FOTO)));

    assertThat(servicio.buscarImagen("Yerba Mate", null, "1kg"), is(nullValue()));
  }

  @Test
  public void alcanzaConQueUnaDeLasMedidasDelResultadoCoincida() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Yerba Mate Taragui 500 g y 1 kg", FOTO)));

    assertThat(servicio.buscarImagen("Yerba Mate", "Taragui", "1kg"), is(FOTO));
  }

  @Test
  public void unaUnidadSinMedidaONulaNoExigeTamano() throws Exception {
    dadaLaBusqueda(busqueda(resultadoDe("Yerba Mate Taragui 500 g", FOTO)));

    assertThat(servicio.buscarImagen("Yerba Mate", "Taragui", "1u"), is(FOTO));
    assertThat(servicio.buscarImagen("Yerba Mate", "Taragui", null), is(FOTO));
  }

  @Test
  public void buscaConElTokenDeLaAplicacion() throws Exception {
    dadaLaBusqueda(busqueda(resultado(FOTO)));

    servicio.buscarImagen("Leche", "La Serenisima", null);

    verify(cliente).buscar(anyString(), eq("APP_USR-123"));
  }

  @Test
  public void reutilizaElTokenEntreBusquedas() throws Exception {
    dadaLaBusqueda(busqueda(resultado(FOTO)));

    servicio.buscarImagen("Leche", "La Serenisima", null);
    servicio.buscarImagen("Yerba", null, null);

    verify(cliente, times(1)).pedirToken(anyString(), anyString());
    verify(cliente, times(2)).buscar(anyString(), anyString());
  }

  @Test
  public void renuevaElTokenCuandoYaVencio() throws Exception {
    when(cliente.pedirToken("mi-id", "mi-secreto")).thenReturn(TOKEN_VENCIDO);
    dadaLaBusqueda(busqueda(resultado(FOTO)));

    servicio.buscarImagen("Leche", "La Serenisima", null);
    servicio.buscarImagen("Yerba", null, null);

    verify(cliente, times(2)).pedirToken(anyString(), anyString());
  }

  @Test
  public void siLaBusquedaFallaDevuelveNullYPideTokenNuevoLaProximaVez() throws Exception {
    when(cliente.buscar(anyString(), anyString())).thenThrow(new IOException("403"));

    assertThat(servicio.buscarImagen("Leche", "La Serenisima", null), is(nullValue()));

    dadaLaBusqueda(busqueda(resultado(FOTO)));
    assertThat(servicio.buscarImagen("Leche", "La Serenisima", null), is(FOTO));
    verify(cliente, times(2)).pedirToken(anyString(), anyString());
  }

  @Test
  public void siNoSePuedePedirElTokenDevuelveNullSinBuscar() throws Exception {
    when(cliente.pedirToken(anyString(), anyString())).thenThrow(new IOException("401"));

    assertThat(servicio.buscarImagen("Leche", "La Serenisima", null), is(nullValue()));
    verify(cliente, never()).buscar(anyString(), anyString());
  }

  @Test
  public void unTokenSinAccessTokenSeIgnora() throws Exception {
    when(cliente.pedirToken(anyString(), anyString())).thenReturn("{\"error\":\"invalid_client\"}");

    assertThat(servicio.buscarImagen("Leche", "La Serenisima", null), is(nullValue()));
    verify(cliente, never()).buscar(anyString(), anyString());
  }

  @Test
  public void sinCredencialesNoHaceNingunaLlamada() throws Exception {
    ServicioImagenProducto sinConfigurar = new ServicioImagenProductoImpl(null, null, cliente);
    ServicioImagenProducto soloId = new ServicioImagenProductoImpl("id", " ", cliente);

    assertThat(sinConfigurar.buscarImagen("Leche", "La Serenisima", null), is(nullValue()));
    assertThat(soloId.buscarImagen("Leche", "La Serenisima", null), is(nullValue()));
    verify(cliente, never()).pedirToken(any(), any());
    verify(cliente, never()).buscar(any(), any());
  }

  @Test
  public void sinNombreNoLlamaAMercadoLibre() throws Exception {
    assertThat(servicio.buscarImagen("  ", "La Serenisima", null), is(nullValue()));
    assertThat(servicio.buscarImagen(null, "La Serenisima", null), is(nullValue()));
    verify(cliente, never()).pedirToken(any(), any());
    verify(cliente, never()).buscar(any(), any());
  }

  @Test
  public void laConsultaBuscaEnElCatalogoConNombreYMarcaCodificados() throws Exception {
    dadaLaBusqueda(busqueda());
    ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

    servicio.buscarImagen("Leche entera & más", "La Serenísima", null);

    verify(cliente).buscar(url.capture(), anyString());
    assertThat(url.getValue(), containsString("https://api.mercadolibre.com/products/search?"));
    assertThat(
      url.getValue(),
      containsString("q=Leche%20entera%20%26%20m%C3%A1s%20La%20Seren%C3%ADsima")
    );
    assertThat(url.getValue(), containsString("site_id=MLA"));
    assertThat(url.getValue(), containsString("status=active"));
  }

  @Test
  public void laConsultaFuncionaSinMarca() throws Exception {
    dadaLaBusqueda(busqueda());
    ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

    servicio.buscarImagen("Yerba", null, null);

    verify(cliente).buscar(url.capture(), anyString());
    assertThat(url.getValue(), containsString("q=Yerba"));
  }
}
