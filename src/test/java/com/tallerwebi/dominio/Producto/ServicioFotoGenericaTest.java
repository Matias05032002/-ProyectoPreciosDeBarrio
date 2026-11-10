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

import com.tallerwebi.dominio.Gemini.ServicioGemini;
import com.tallerwebi.dominio.Producto.ServicioFotoGenericaImpl.ClienteWikipedia;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioFotoGenericaTest {

  private static final String FOTO =
    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ab/Avocado.jpg/500px-Avocado.jpg";

  private ClienteWikipedia wikipedia;
  private ServicioGemini groq;
  private ServicioFotoGenerica servicio;

  @BeforeEach
  public void init() throws Exception {
    wikipedia = mock(ClienteWikipedia.class);
    groq = mock(ServicioGemini.class);
    servicio = new ServicioFotoGenericaImpl(wikipedia, groq);
    dadoQueGroqResponde("avocado");
  }

  private void dadoQueGroqResponde(String contenido) throws Exception {
    doReturn("{\"choices\":[{\"message\":{\"content\":\"" + contenido + "\"}}]}")
      .when(groq)
      .llamar(anyString(), anyString());
  }

  private static String fotos(String... urls) {
    StringBuilder paginas = new StringBuilder();
    int indice = 1;
    for (String url : urls) {
      paginas
        .append(indice == 1 ? "" : ",")
        .append("\"")
        .append(100 + indice)
        .append("\":{\"index\":")
        .append(indice)
        .append(",\"thumbnail\":{\"source\":\"")
        .append(url)
        .append("\"}}");
      indice++;
    }
    return "{\"query\":{\"pages\":{" + paginas + "}}}";
  }

  private void dadaLaBusqueda(String json) throws Exception {
    doReturn(json).when(wikipedia).buscar(anyString());
  }

  @Test
  public void devuelveLaPrimeraFotoDelTerminoQueDevuelveGroq() throws Exception {
    dadaLaBusqueda(
      fotos(
        FOTO,
        "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Otra.jpg/500px-Otra.jpg"
      )
    );

    assertThat(servicio.buscarFoto("PALTA", "Verduras y frutas"), is(FOTO));
  }

  @Test
  public void buscaEnWikipediaConElTermino() throws Exception {
    dadaLaBusqueda(fotos(FOTO));
    ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

    servicio.buscarFoto("PALTA", "Verduras y frutas");

    verify(wikipedia).buscar(url.capture());
    assertThat(url.getValue(), containsString("https://en.wikipedia.org/w/api.php?"));
    assertThat(url.getValue(), containsString("titles=avocado"));
  }

  @Test
  public void leDiceAGroqQueElNombreVieneAbreviadoYQueNoUseMarcas() throws Exception {
    dadaLaBusqueda(fotos(FOTO));
    ArgumentCaptor<String> sistema = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> usuario = ArgumentCaptor.forClass(String.class);

    servicio.buscarFoto("ALGODON FAMILI CLASI", "Farmacia y perfumeria");

    verify(groq).llamar(sistema.capture(), usuario.capture());
    assertThat(sistema.getValue(), containsString("sin marcas"));
    assertThat(usuario.getValue(), containsString("ALGODON FAMILI CLASI"));
    assertThat(usuario.getValue(), containsString("Farmacia y perfumeria"));
  }

  // La foto de la categoría ("Vegetable", "Food") es un collage o un puesto de feria: peor que
  // ninguna, porque parece la foto del producto. Sin foto propia queda el ícono de siempre.
  @Test
  public void siElTerminoNoTieneFotosQuedaSinFotoYNoUsaLaDeLaCategoria() throws Exception {
    doReturn("{\"batchcomplete\":\"\"}").when(wikipedia).buscar(anyString());

    assertThat(servicio.buscarFoto("PALTA", "Verduras y frutas"), is(nullValue()));

    verify(wikipedia, times(1)).buscar(anyString());
    verify(wikipedia, never()).buscar(org.mockito.ArgumentMatchers.contains("Vegetable"));
  }

  @Test
  public void siGroqFallaQuedaSinFotoYNoConsultaPorLaCategoria() throws Exception {
    when(groq.llamar(anyString(), anyString())).thenThrow(new IOException("429"));
    dadaLaBusqueda(fotos(FOTO));

    assertThat(servicio.buscarFoto("PALTA", "Lacteos"), is(nullValue()));

    verify(wikipedia, never()).buscar(anyString());
  }

  @Test
  public void unaRespuestaDeGroqConTextoDeMasSeLimpia() throws Exception {
    dadoQueGroqResponde("\\\"Avocado.\\\"");
    dadaLaBusqueda(fotos(FOTO));
    ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

    servicio.buscarFoto("PALTA", "Verduras y frutas");

    verify(wikipedia).buscar(url.capture());
    assertThat(url.getValue(), containsString("titles=avocado"));
  }

  @Test
  public void unaRespuestaDeGroqLargaOConSimbolosSeIgnora() throws Exception {
    dadoQueGroqResponde("No puedo ayudar con eso porque es una consulta ambigua y larga");
    dadaLaBusqueda(fotos(FOTO));

    assertThat(servicio.buscarFoto("PALTA", "Verduras y frutas"), is(nullValue()));

    verify(wikipedia, never()).buscar(anyString());
  }

  @Test
  public void soloAceptaFotosDeWikimediaPorHttps() throws Exception {
    dadaLaBusqueda(
      fotos(
        "https://sitio-malicioso.com/a.jpg",
        "https://upload.wikimedia.org.evil.com/b.jpg",
        "javascript:alert(1)",
        "http://upload.wikimedia.org/wikipedia/commons/c.jpg"
      )
    );

    assertThat(
      servicio.buscarFoto("PALTA", "Verduras y frutas"),
      is("https://upload.wikimedia.org/wikipedia/commons/c.jpg")
    );
  }

  @Test
  public void tomaElArticuloMasRelevanteQueTengaImagen() throws Exception {
    dadaLaBusqueda(
      "{\"query\":{\"pages\":{" +
      "\"7\":{\"index\":3,\"thumbnail\":{\"source\":\"https://upload.wikimedia.org/tercero.jpg\"}}," +
      "\"8\":{\"index\":1}," +
      "\"9\":{\"index\":2,\"thumbnail\":{\"source\":\"https://upload.wikimedia.org/segundo.jpg\"}}" +
      "}}}"
    );

    assertThat(
      servicio.buscarFoto("PALTA", "Verduras y frutas"),
      is("https://upload.wikimedia.org/segundo.jpg")
    );
  }

  @Test
  public void siNingunaConsultaDaFotosDevuelveNull() throws Exception {
    dadaLaBusqueda("{\"batchcomplete\":\"\"}");

    assertThat(servicio.buscarFoto("PALTA", "Verduras y frutas"), is(nullValue()));
  }

  @Test
  public void siWikipediaFallaDevuelveNullSinLanzar() throws Exception {
    when(wikipedia.buscar(anyString())).thenThrow(new IOException("403"));

    assertThat(servicio.buscarFoto("PALTA", "Verduras y frutas"), is(nullValue()));
  }

  @Test
  public void unJsonInvalidoDevuelveNull() throws Exception {
    dadaLaBusqueda("<html>error</html>");

    assertThat(servicio.buscarFoto("PALTA", "Verduras y frutas"), is(nullValue()));
  }

  @Test
  public void repetirLaMismaConsultaNoVuelveAWikipedia() throws Exception {
    dadaLaBusqueda(fotos(FOTO));

    servicio.buscarFoto("PALTA", "Verduras y frutas");
    servicio.buscarFoto("PALTA HASS", "Verduras y frutas");

    verify(wikipedia, times(1)).buscar(anyString());
  }

  @Test
  public void sinNombreNoBuscaNadaNiLePreguntaAGroq() throws Exception {
    dadaLaBusqueda(fotos(FOTO));

    assertThat(servicio.buscarFoto(" ", "Lacteos"), is(nullValue()));

    verify(wikipedia, never()).buscar(anyString());
    verify(groq, never()).llamar(any(), any());
  }

  @Test
  public void siWikipediaPideEsperarEsperaYReintenta() throws Exception {
    ServicioFotoGenericaImpl.Pausa pausa = mock(ServicioFotoGenericaImpl.Pausa.class);
    servicio = new ServicioFotoGenericaImpl(wikipedia, groq, pausa);
    when(wikipedia.buscar(anyString()))
      .thenThrow(new ServicioFotoGenericaImpl.LimiteExcedido(1500))
      .thenReturn(fotos(FOTO));

    assertThat(servicio.buscarFoto("PALTA", "Verduras y frutas"), is(FOTO));

    verify(pausa).esperar(1500);
    verify(wikipedia, times(2)).buscar(anyString());
  }

  @Test
  public void siWikipediaSigueLimitandoDespuesDeVariosIntentosDevuelveNull() throws Exception {
    ServicioFotoGenericaImpl.Pausa pausa = mock(ServicioFotoGenericaImpl.Pausa.class);
    servicio = new ServicioFotoGenericaImpl(wikipedia, groq, pausa);
    when(wikipedia.buscar(anyString()))
      .thenThrow(new ServicioFotoGenericaImpl.LimiteExcedido(1000));

    assertThat(servicio.buscarFoto("PALTA", "Verduras y frutas"), is(nullValue()));

    verify(pausa, times(3)).esperar(1000);
  }

  private static final String PIXABAY = "https://pixabay.com/get/g1234_640.jpg";

  private ServicioFotoGenerica servicioConPixabay() {
    return new ServicioFotoGenericaImpl(wikipedia, groq, milisegundos -> {}, "mi-clave");
  }

  private static String hits(String... urls) {
    StringBuilder lista = new StringBuilder();
    for (String url : urls) {
      lista
        .append(lista.length() == 0 ? "" : ",")
        .append(hit(url, "avocado, fruit, isolated, white background"));
    }
    return "{\"total\":1,\"hits\":[" + lista + "]}";
  }

  private static String hit(String url, String tags) {
    return "{\"webformatURL\":\"" + url + "\",\"tags\":\"" + tags + "\"}";
  }

  @Test
  public void conClaveBuscaPrimeroEnPixabayYNoLlamaAWikipedia() throws Exception {
    doReturn(hits(PIXABAY, "https://pixabay.com/get/otra.jpg"))
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("pixabay.com"));
    ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

    assertThat(servicioConPixabay().buscarFoto("PALTA", "Verduras y frutas"), is(PIXABAY));

    verify(wikipedia).buscar(url.capture());
    assertThat(url.getValue(), containsString("key=mi-clave"));
    assertThat(url.getValue(), containsString("q=avocado%20isolated"));
    assertThat(url.getValue(), containsString("image_type=photo"));
    assertThat(url.getValue(), containsString("category=food"));
  }

  @Test
  public void paraProductosQueNoSonComidaNoPideLaCategoriaDeComida() throws Exception {
    dadoQueGroqResponde("sponge");
    doReturn("{\"hits\":[" + hit(PIXABAY, "sponge, cleaning, isolated") + "]}")
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("pixabay.com"));
    ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

    servicioConPixabay().buscarFoto("ESPONJA", "Limpieza del hogar");

    verify(wikipedia).buscar(url.capture());
    assertThat(url.getValue(), containsString("q=sponge%20isolated"));
    assertThat(
      org.hamcrest.Matchers.not(containsString("category=")).matches(url.getValue()),
      is(true)
    );
  }

  @Test
  public void siPixabayNoTieneFotosUsaWikipedia() throws Exception {
    doReturn("{\"total\":0,\"hits\":[]}")
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("pixabay.com"));
    doReturn(fotos(FOTO))
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("wikipedia.org"));

    assertThat(servicioConPixabay().buscarFoto("PALTA", "Verduras y frutas"), is(FOTO));
  }

  @Test
  public void sinClaveNoConsultaPixabay() throws Exception {
    dadaLaBusqueda(fotos(FOTO));

    servicio.buscarFoto("PALTA", "Verduras y frutas");

    verify(wikipedia, never()).buscar(org.mockito.ArgumentMatchers.contains("pixabay.com"));
  }

  @Test
  public void ignoraLasFotosDePixabayQueNoSonDeSuDominio() throws Exception {
    doReturn(hits("https://sitio-raro.com/foto.jpg"))
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("pixabay.com"));
    doReturn(fotos(FOTO))
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("wikipedia.org"));

    assertThat(servicioConPixabay().buscarFoto("PALTA", "Verduras y frutas"), is(FOTO));
  }

  @Test
  public void repetirElTerminoNoVuelveAPixabay() throws Exception {
    doReturn(hits(PIXABAY))
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("pixabay.com"));
    ServicioFotoGenerica conClave = servicioConPixabay();

    conClave.buscarFoto("PALTA", "Verduras y frutas");
    conClave.buscarFoto("PALTA HASS", "Verduras y frutas");

    verify(wikipedia, times(1)).buscar(anyString());
  }

  @Test
  public void soloUsaLasFotosCuyosTagsTienenElTerminoYSonDeProductoSolo() throws Exception {
    String artistica = hit("https://pixabay.com/get/arte.jpg", "avocado, dark, knife, art");
    String otraCosa = hit("https://pixabay.com/get/otra.jpg", "pear, isolated");
    String buena = hit(PIXABAY, "Avocado, Fruit, white background");
    doReturn("{\"hits\":[" + artistica + "," + otraCosa + "," + buena + "]}")
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("pixabay.com"));

    assertThat(servicioConPixabay().buscarFoto("PALTA", "Verduras y frutas"), is(PIXABAY));
  }

  @Test
  public void siNingunaFotoDePixabayEsDeProductoSoloUsaWikipedia() throws Exception {
    doReturn(
      "{\"hits\":[" + hit("https://pixabay.com/get/arte.jpg", "avocado, dark, knife, art") + "]}"
    )
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("pixabay.com"));
    doReturn(fotos(FOTO))
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("wikipedia.org"));

    assertThat(servicioConPixabay().buscarFoto("PALTA", "Verduras y frutas"), is(FOTO));
  }

  @Test
  public void entreLasFotosDeProductoSoloPrefiereLaDeFondoBlanco() throws Exception {
    String sobreNegro = hit("https://pixabay.com/get/negro.jpg", "avocado, isolated, studio");
    String sobreBlanco = hit(PIXABAY, "avocado, fruit, white background");
    doReturn("{\"hits\":[" + sobreNegro + "," + sobreBlanco + "]}")
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("pixabay.com"));

    assertThat(servicioConPixabay().buscarFoto("PALTA", "Verduras y frutas"), is(PIXABAY));
  }

  @Test
  public void siNoHayFondoBlancoAceptaUnaIsolatedDeFondoClaro() throws Exception {
    doReturn("{\"hits\":[" + hit(PIXABAY, "avocado, fruit, isolated") + "]}")
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("pixabay.com"));

    assertThat(servicioConPixabay().buscarFoto("PALTA", "Verduras y frutas"), is(PIXABAY));
  }

  @Test
  public void descartaLasFotosDeFondoNegro() throws Exception {
    String sobreNegro = hit(
      "https://pixabay.com/get/negro.jpg",
      "potato, isolated, black background, dark"
    );
    doReturn("{\"hits\":[" + sobreNegro + "]}")
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("pixabay.com"));
    doReturn(fotos(FOTO))
      .when(wikipedia)
      .buscar(org.mockito.ArgumentMatchers.contains("wikipedia.org"));
    dadoQueGroqResponde("potato");

    assertThat(servicioConPixabay().buscarFoto("PAPA NEGRA", "Verduras y frutas"), is(FOTO));
  }

  @Test
  public void alNormalizarUnaMarcaSoloPideElProductoPorElQueSeLaConoce() throws Exception {
    dadaLaBusqueda(fotos(FOTO));
    ArgumentCaptor<String> sistema = ArgumentCaptor.forClass(String.class);

    servicio.buscarFoto("Hellmanns", "Condimentos y salsas");

    verify(groq).llamar(sistema.capture(), anyString());
    assertThat(sistema.getValue(), containsString("Si solo se lee una marca"));
    assertThat(sistema.getValue(), containsString("mayonnaise"));
  }

  @Test
  public void aceptaLasMiniaturasDeThumbWikimedia() throws Exception {
    String miniatura =
      "https://thumb.wikimedia.org/wikipedia/commons/thumb/6/60/Mayo.jpg/500px-Mayo.jpg";
    dadaLaBusqueda(fotos(miniatura));

    assertThat(servicio.buscarFoto("MAYONESA", "Condimentos y salsas"), is(miniatura));
  }

  @Test
  public void tambienPideElArticuloDeLaCosaCuandoElTituloEsUnaDesambiguacion() throws Exception {
    dadoQueGroqResponde("mustard");
    ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
    dadaLaBusqueda(fotos(FOTO));

    servicio.buscarFoto("MOSTAZA", "Condimentos y salsas");

    verify(wikipedia).buscar(url.capture());
    assertThat(url.getValue(), containsString("titles=mustard%7Cmustard%20%28condiment%29"));
  }

  @Test
  public void siElTituloExactoNoTieneFotoUsaElDeLaCondimento() throws Exception {
    dadoQueGroqResponde("mustard");
    dadaLaBusqueda(
      "{\"query\":{\"pages\":{" +
      "\"1\":{\"title\":\"Mustard\"}," +
      "\"2\":{\"title\":\"Mustard (condiment)\",\"thumbnail\":{\"source\":\"" +
      FOTO +
      "\"}}" +
      "}}}"
    );

    assertThat(servicio.buscarFoto("MOSTAZA", "Condimentos y salsas"), is(FOTO));
  }

  @Test
  public void siHayFotoEnElTituloExactoEsaGanaALaDeLaVariante() throws Exception {
    dadoQueGroqResponde("mustard");
    String exacta = "https://upload.wikimedia.org/exacta.jpg";
    dadaLaBusqueda(
      "{\"query\":{\"pages\":{" +
      "\"2\":{\"title\":\"Mustard (condiment)\",\"thumbnail\":{\"source\":\"" +
      FOTO +
      "\"}}," +
      "\"1\":{\"title\":\"Mustard\",\"thumbnail\":{\"source\":\"" +
      exacta +
      "\"}}" +
      "}}}"
    );

    assertThat(servicio.buscarFoto("MOSTAZA", "Condimentos y salsas"), is(exacta));
  }
}
