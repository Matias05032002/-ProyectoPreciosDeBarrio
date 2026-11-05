package com.tallerwebi.dominio.Asistente;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.HorarioComercio;
import com.tallerwebi.dominio.Comercio.MetodoPago;
import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.RepositorioComercio;
import com.tallerwebi.dominio.Comercio.RepositorioHorario;
import com.tallerwebi.dominio.Comercio.RepositorioPromocion;
import com.tallerwebi.dominio.Comercio.TipoPromocion;
import com.tallerwebi.dominio.Gemini.ServicioGemini;
import com.tallerwebi.dominio.Lista.ItemLista;
import com.tallerwebi.dominio.Lista.Lista;
import com.tallerwebi.dominio.Lista.RepositorioLista;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.RepositorioProducto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioAsistenteTest {

  private static final String RESPUESTA_OK = "{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}";

  private ServicioAsistente servicioAsistente;
  private RepositorioReporte repositorioReporteMock;
  private ServicioGemini servicioGeminiMock;
  private RepositorioPromocion repositorioPromocionMock;
  private RepositorioLista repositorioListaMock;
  private RepositorioProducto repositorioProductoMock;
  private RepositorioComercio repositorioComercioMock;
  private RepositorioHorario repositorioHorarioMock;

  @BeforeEach
  public void init() {
    this.repositorioReporteMock = mock(RepositorioReporte.class);
    this.servicioGeminiMock = mock(ServicioGemini.class);
    this.repositorioPromocionMock = mock(RepositorioPromocion.class);
    this.repositorioListaMock = mock(RepositorioLista.class);
    this.repositorioProductoMock = mock(RepositorioProducto.class);
    this.repositorioComercioMock = mock(RepositorioComercio.class);
    this.repositorioHorarioMock = mock(RepositorioHorario.class);
    this.servicioAsistente =
      new ServicioAsistenteImpl(
        repositorioReporteMock,
        servicioGeminiMock,
        repositorioPromocionMock,
        repositorioListaMock,
        repositorioProductoMock,
        repositorioComercioMock,
        repositorioHorarioMock
      );
  }

  @Test
  public void consultarSinReportesDevuelveRespuestaDeGemini() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    String respuestaJson =
      "{\"choices\":[{\"message\":{\"content\":\"No hay productos disponibles.\"}}]}";
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(respuestaJson);

    String resultado = servicioAsistente.consultar(
      "leche",
      null,
      null,
      null,
      Collections.emptyList()
    );

    assertThat(resultado, equalTo("No hay productos disponibles."));
  }

  @Test
  public void consultarConReportesEnviaContextoAGemini() throws Exception {
    Reporte reporte = reporteValido(
      "Leche",
      100.0,
      "Almacen Don Jose",
      "Av. Siempreviva 123",
      null,
      null
    );
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Arrays.asList(reporte));
    String respuestaJson = "{\"choices\":[{\"message\":{\"content\":\"La leche está a $100.\"}}]}";
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(respuestaJson);

    String resultado = servicioAsistente.consultar(
      "leche",
      null,
      null,
      null,
      Collections.emptyList()
    );

    assertThat(resultado, equalTo("La leche está a $100."));
    verify(servicioGeminiMock, times(1)).llamar(anyString(), contains("Leche"));
  }

  @Test
  public void consultarConCoordenadas_usaFallbackSiNoHayReportesCercanos() throws Exception {
    // Comercio a 100km de distancia
    Reporte reporte = reporteValido("Leche", 100.0, "Almacen Lejano", "Calle Lejana 1", 0.0, 0.0);
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Arrays.asList(reporte));
    String respuestaJson = "{\"choices\":[{\"message\":{\"content\":\"Encontré leche lejos.\"}}]}";
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(respuestaJson);

    String resultado = servicioAsistente.consultar(
      "leche",
      -34.6037,
      -58.3816,
      null,
      Collections.emptyList()
    );

    assertThat(resultado, equalTo("Encontré leche lejos."));

    verify(servicioGeminiMock, times(1)).llamar(anyString(), contains("Almacen Lejano"));
  }

  @Test
  public void consultarConReportesSinComercioLosIgnora() throws Exception {
    Reporte reporteInvalido = new Reporte();
    reporteInvalido.setComercio(null);
    reporteInvalido.setProducto(new Producto());
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Arrays.asList(reporteInvalido));
    String respuestaJson = "{\"choices\":[{\"message\":{\"content\":\"Sin datos.\"}}]}";
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(respuestaJson);

    String resultado = servicioAsistente.consultar(
      "leche",
      null,
      null,
      null,
      Collections.emptyList()
    );

    assertThat(resultado, equalTo("Sin datos."));
    verify(servicioGeminiMock, times(1))
      .llamar(anyString(), contains("No hay reportes disponibles"));
  }

  @Test
  public void consultarSinUbicacionNoInformaDistanciaEnElContexto() throws Exception {
    Reporte reporte = reporteValido("Leche", 100.0, "Almacen Don Jose", "Calle 1", -34.6, -58.4);
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Arrays.asList(reporte));
    when(servicioGeminiMock.llamar(anyString(), anyString()))
      .thenReturn("{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}");

    servicioAsistente.consultar("leche", null, null, null, Collections.emptyList());

    verify(servicioGeminiMock).llamar(anyString(), argThat(contexto -> !contexto.contains(" km")));
  }

  @Test
  public void consultarConUbicacionInformaLaDistanciaEnElContexto() throws Exception {
    Reporte reporte = reporteValido("Leche", 100.0, "Almacen Don Jose", "Calle 1", -34.6, -58.4);
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Arrays.asList(reporte));
    when(servicioGeminiMock.llamar(anyString(), anyString()))
      .thenReturn("{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}");

    servicioAsistente.consultar("leche", -34.61, -58.41, null, Collections.emptyList());

    verify(servicioGeminiMock).llamar(anyString(), contains(" km"));
  }

  @Test
  public void consultarCuandoGeminiLanzaExcepcionPropagaError() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    when(servicioGeminiMock.llamar(anyString(), anyString()))
      .thenThrow(new Exception("Error de red"));

    org.junit.jupiter.api.Assertions.assertThrows(
      Exception.class,
      () -> servicioAsistente.consultar("leche", null, null, null, Collections.emptyList())
    );
  }

  @Test
  public void filtrarPorKeywordDeberiaEnviarSoloReportesDelProductoPreguntado() throws Exception {
    Reporte reporteLeche = reporteValido("Leche", 100.0, "Almacen", "Av. Test 1", null, null);
    Reporte reporteArroz = reporteValido("Arroz", 80.0, "Kiosco", "Av. Test 2", null, null);
    when(repositorioReporteMock.listarRecientes(7))
      .thenReturn(Arrays.asList(reporteLeche, reporteArroz));
    String respuestaJson = "{\"choices\":[{\"message\":{\"content\":\"La leche está a $100.\"}}]}";
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(respuestaJson);

    servicioAsistente.consultar("leche", null, null, null, Collections.emptyList());

    verify(servicioGeminiMock, times(1)).llamar(anyString(), contains("Leche"));
    verify(servicioGeminiMock, times(1)).llamar(anyString(), argThat(s -> !s.contains("Arroz")));
  }

  @Test
  public void filtrarPorKeywordSinMatchDeberiaEnviarTodosLosReportes() throws Exception {
    Reporte reporteLeche = reporteValido("Leche", 100.0, "Almacen", "Av. Test 1", null, null);
    Reporte reporteArroz = reporteValido("Arroz", 80.0, "Kiosco", "Av. Test 2", null, null);
    when(repositorioReporteMock.listarRecientes(7))
      .thenReturn(Arrays.asList(reporteLeche, reporteArroz));
    String respuestaJson =
      "{\"choices\":[{\"message\":{\"content\":\"Tenemos varios productos.\"}}]}";
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(respuestaJson);

    servicioAsistente.consultar("qué hay disponible", null, null, null, Collections.emptyList());

    verify(servicioGeminiMock, times(1)).llamar(anyString(), contains("Leche"));
    verify(servicioGeminiMock, times(1)).llamar(anyString(), contains("Arroz"));
  }

  @Test
  public void consultarIncluyeLasPromocionesVigentesEnElContexto() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    Comercio comercio = new Comercio();
    comercio.setNombre("Almacen Don Jose");
    Promocion porProducto = new Promocion();
    porProducto.setTitulo("Oferta leche");
    porProducto.setProductoNombre("Leche entera 1l");
    porProducto.setPrecioPromocional(140.0);
    porProducto.setComercio(comercio);
    Promocion porMetodo = new Promocion();
    porMetodo.setTitulo("Martes de débito");
    porMetodo.setTipo(TipoPromocion.METODO_PAGO);
    porMetodo.setMetodoPago(MetodoPago.DEBITO);
    porMetodo.setPorcentajeDescuento(20);
    porMetodo.setComercio(comercio);
    when(repositorioPromocionMock.listarActivas())
      .thenReturn(Arrays.asList(porProducto, porMetodo));
    when(servicioGeminiMock.llamar(anyString(), anyString()))
      .thenReturn("{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}");

    servicioAsistente.consultar("¿hay promos?", null, null, null, Collections.emptyList());

    verify(servicioGeminiMock)
      .llamar(
        anyString(),
        argThat(prompt ->
          prompt.contains("Promociones vigentes") &&
          prompt.contains("Leche entera 1l") &&
          prompt.contains("20% de descuento pagando con Tarjeta de débito")
        )
      );
  }

  @Test
  public void consultarSinPromocionesNoAgregaLaSeccionDePromociones() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    when(repositorioPromocionMock.listarActivas()).thenReturn(Collections.emptyList());
    when(servicioGeminiMock.llamar(anyString(), anyString()))
      .thenReturn("{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}");

    servicioAsistente.consultar("leche", null, null, null, Collections.emptyList());

    verify(servicioGeminiMock)
      .llamar(anyString(), argThat(prompt -> !prompt.contains("Promociones vigentes")));
  }

  @Test
  public void elAsistenteAyudaTambienConElUsoDeLaApp() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    when(servicioGeminiMock.llamar(anyString(), anyString()))
      .thenReturn("{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}");

    servicioAsistente.consultar(
      "¿cómo reporto un precio?",
      null,
      null,
      null,
      Collections.emptyList()
    );

    verify(servicioGeminiMock)
      .llamar(argThat(sistema -> sistema.contains("Reportar un precio")), anyString());
  }

  @Test
  public void consultarIncluyeLasListasDelVecinoYDondeComprarMasBarato() throws Exception {
    Reporte caro = reporteValido("Leche", 120.0, "Almacen Caro", "Calle 1", null, null);
    Reporte barato = reporteValido("Leche", 100.0, "Kiosco Barato", "Calle 2", null, null);
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Arrays.asList(caro, barato));
    Lista lista = new Lista("Semanal", 5L);
    lista.getItems().add(new ItemLista(lista, caro, 2));
    when(repositorioListaMock.buscarPorUsuarioId(5L)).thenReturn(Arrays.asList(lista));
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("¿cuánto ahorro?", null, null, 5L, Collections.emptyList());

    verify(servicioGeminiMock)
      .llamar(
        anyString(),
        argThat(prompt ->
          prompt.contains("Listas de compras del vecino") &&
          prompt.contains("\"Semanal\" (1 productos, total $240.00)") &&
          prompt.contains("más barato: $100.00 en Kiosco Barato (ahorrás $40.00)") &&
          prompt.contains(
            "Ahorro posible comprando cada producto en el comercio más barato: $40.00"
          )
        )
      );
  }

  @Test
  public void consultarSinUsuarioNoIncluyeListas() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("leche", null, null, null, Collections.emptyList());

    verify(servicioGeminiMock).llamar(anyString(), argThat(p -> !p.contains("listas de compras")));
    verify(repositorioListaMock, never()).buscarPorUsuarioId(any());
  }

  @Test
  public void consultarConUsuarioSinListasLoAvisa() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    when(repositorioListaMock.buscarPorUsuarioId(5L)).thenReturn(Collections.emptyList());
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("mi lista", null, null, 5L, Collections.emptyList());

    verify(servicioGeminiMock).llamar(anyString(), contains("todavía no armó listas de compras"));
  }

  @Test
  public void consultarMuestraPrimeroElPrecioMasBajo() throws Exception {
    Reporte caro = reporteValido("Leche", 150.0, "Almacen Caro", "Calle 1", null, null);
    Reporte barato = reporteValido("Leche", 100.0, "Kiosco Barato", "Calle 2", null, null);
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Arrays.asList(caro, barato));
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("leche", null, null, null, Collections.emptyList());

    ArgumentCaptor<String> prompt = ArgumentCaptor.forClass(String.class);
    verify(servicioGeminiMock).llamar(anyString(), prompt.capture());
    assertThat(
      prompt.getValue().indexOf("$100.00"),
      lessThan(prompt.getValue().indexOf("$150.00"))
    );
  }

  @Test
  public void consultarEntiendeLaPreguntaAunqueVengaEnPluralYConOtrasPalabras() throws Exception {
    Reporte leche = reporteValido("Leche", 100.0, "Almacen", "Calle 1", null, null);
    Reporte arroz = reporteValido("Arroz", 80.0, "Kiosco", "Calle 2", null, null);
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Arrays.asList(leche, arroz));
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar(
      "¿Dónde están más baratas las leches?",
      null,
      null,
      null,
      Collections.emptyList()
    );

    verify(servicioGeminiMock)
      .llamar(anyString(), argThat(p -> p.contains("Leche") && !p.contains("Arroz")));
  }

  @Test
  public void consultarSinMatchEnvíaElMasBaratoDeCadaProducto() throws Exception {
    Reporte lecheCara = reporteValido("Leche", 150.0, "Almacen", "Calle 1", null, null);
    Reporte lecheBarata = reporteValido("Leche", 100.0, "Kiosco", "Calle 2", null, null);
    Reporte arroz = reporteValido("Arroz", 80.0, "Kiosco", "Calle 2", null, null);
    when(repositorioReporteMock.listarRecientes(7))
      .thenReturn(Arrays.asList(lecheCara, lecheBarata, arroz));
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar(
      "tengo 10000 pesos para el mes",
      null,
      null,
      null,
      Collections.emptyList()
    );

    verify(servicioGeminiMock)
      .llamar(
        anyString(),
        argThat(p ->
          p.contains("Precio más barato reportado de cada producto") &&
          p.contains("$100.00") &&
          p.contains("$80.00") &&
          !p.contains("$150.00")
        )
      );
  }

  @Test
  public void consultarIncluyeElCatalogoYElUltimoPrecioDeProductosSinReportesRecientes()
    throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    Producto aceite = new Producto();
    aceite.setId(9L);
    aceite.setNombre("Aceite de girasol");
    aceite.setMarca("Cocinero");
    aceite.setUnidad("900ml");
    aceite.setCategoria("Almacén");
    when(repositorioProductoMock.buscarPorNombre("aceite")).thenReturn(Arrays.asList(aceite));
    Reporte viejo = reporteValido(
      "Aceite de girasol",
      1500.0,
      "Almacen Viejo",
      "Calle 3",
      null,
      null
    );
    viejo.setFechaDeReporte(LocalDateTime.now().minusDays(30));
    when(repositorioReporteMock.buscarPorProducto(9L)).thenReturn(Arrays.asList(viejo));
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("aceite", null, null, null, Collections.emptyList());

    verify(servicioGeminiMock)
      .llamar(
        anyString(),
        argThat(p ->
          p.contains("Productos del catálogo que coinciden") &&
          p.contains("Aceite de girasol Cocinero 900ml (categoría: Almacén)") &&
          p.contains("$1500.00 en Almacen Viejo (hace 30 días) (dato viejo)")
        )
      );
  }

  @Test
  public void consultarAvisaSiUnProductoDelCatalogoNuncaFueReportado() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    Producto yerba = new Producto();
    yerba.setId(4L);
    yerba.setNombre("Yerba mate");
    when(repositorioProductoMock.buscarPorNombre("yerba")).thenReturn(Arrays.asList(yerba));
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("yerba", null, null, null, Collections.emptyList());

    verify(servicioGeminiMock).llamar(anyString(), contains("todavía nadie reportó su precio"));
  }

  @Test
  public void consultarMarcaLosPreciosDudosos() throws Exception {
    Reporte dudoso = reporteValido("Leche", 10.0, "Almacen", "Calle 1", null, null);
    dudoso.setPuntuacion(Reporte.PUNTUACION_DUDOSO + 1);
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Arrays.asList(dudoso));
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("leche", null, null, null, Collections.emptyList());

    verify(servicioGeminiMock).llamar(anyString(), contains("lo marcaron como dudoso"));
  }

  @Test
  public void consultarConUbicacionIncluyeLosComerciosMasCercanos() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    Comercio cerca = new Comercio();
    cerca.setNombre("Verduleria Cercana");
    cerca.setTipo("Verdulería");
    cerca.setDireccion("Calle 5");
    cerca.setLatitud(-34.601);
    cerca.setLongitud(-58.382);
    Comercio lejos = new Comercio();
    lejos.setNombre("Almacen Lejano");
    lejos.setDireccion("Calle 9");
    lejos.setLatitud(-34.9);
    lejos.setLongitud(-58.9);
    when(repositorioComercioMock.listarTodos()).thenReturn(Arrays.asList(lejos, cerca));
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("comercios", -34.6037, -58.3816, null, Collections.emptyList());

    ArgumentCaptor<String> prompt = ArgumentCaptor.forClass(String.class);
    verify(servicioGeminiMock).llamar(anyString(), prompt.capture());
    assertThat(prompt.getValue(), containsString("Comercios más cercanos al vecino"));
    assertThat(
      prompt.getValue().indexOf("Verduleria Cercana"),
      lessThan(prompt.getValue().indexOf("Almacen Lejano"))
    );
  }

  @Test
  public void elPromptDelSistemaExplicaComoUsarLaApp() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("¿cómo armo una lista?", null, null, null, Collections.emptyList());

    verify(servicioGeminiMock)
      .llamar(
        argThat(sistema -> sistema.contains("Mis listas") && sistema.contains("foto del ticket")),
        anyString()
      );
  }

  @Test
  public void consultarAvisaDeLasPromocionesQueTodaviaNoEmpezaron() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    Comercio comercio = new Comercio();
    comercio.setNombre("Almacen Don Jose");
    Promocion futura = new Promocion();
    futura.setTitulo("Semana del café");
    futura.setProductoNombre("Café molido 500g");
    futura.setPrecioPromocional(3000.0);
    futura.setFechaInicio(LocalDateTime.of(2030, 3, 15, 8, 0));
    futura.setComercio(comercio);
    when(repositorioPromocionMock.listarProximas()).thenReturn(Arrays.asList(futura));
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("¿hay promos?", null, null, null, Collections.emptyList());

    verify(servicioGeminiMock)
      .llamar(
        anyString(),
        argThat(prompt ->
          prompt.contains("todavía NO empezaron") &&
          prompt.contains("Café molido 500g") &&
          prompt.contains("desde el 15/03")
        )
      );
  }

  @Test
  public void consultarSinPromocionesProximasNoAgregaEsaSeccion() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("leche", null, null, null, Collections.emptyList());

    verify(servicioGeminiMock)
      .llamar(anyString(), argThat(prompt -> !prompt.contains("todavía NO empezaron")));
  }

  @Test
  public void alPreguntarPorUnComercioAbiertoIncluyeSusHorariosAunSinUbicacion() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    Comercio abierto = new Comercio();
    abierto.setId(1L);
    abierto.setNombre("Almacen Siempre Abierto");
    abierto.setDireccion("Calle 1");
    Comercio sinHorario = new Comercio();
    sinHorario.setId(2L);
    sinHorario.setNombre("Kiosco Misterio");
    sinHorario.setDireccion("Calle 2");
    when(repositorioComercioMock.listarTodos()).thenReturn(Arrays.asList(sinHorario, abierto));
    when(repositorioHorarioMock.buscarPorComercioId(1L)).thenReturn(todosLosDiasAbierto());
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar(
      "¿cuál está abierto ahora?",
      null,
      null,
      null,
      Collections.emptyList()
    );

    ArgumentCaptor<String> prompt = ArgumentCaptor.forClass(String.class);
    verify(servicioGeminiMock).llamar(anyString(), prompt.capture());
    assertThat(prompt.getValue(), containsString("Comercios y su horario de atención"));
    assertThat(
      prompt.getValue(),
      containsString("Almacen Siempre Abierto (comercio), Calle 1, abierto ahora")
    );
    assertThat(
      prompt.getValue(),
      containsString("Kiosco Misterio (comercio), Calle 2, sin horario cargado")
    );
  }

  @Test
  public void sinUbicacionNiPreguntaDeHorarioNoListaComercios() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("leche", null, null, null, Collections.emptyList());

    verify(repositorioComercioMock, never()).listarTodos();
  }

  @Test
  public void losComerciosCercanosTambienMuestranSiEstanAbiertos() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    Comercio cerca = new Comercio();
    cerca.setId(3L);
    cerca.setNombre("Verduleria Cercana");
    cerca.setDireccion("Calle 5");
    cerca.setLatitud(-34.601);
    cerca.setLongitud(-58.382);
    when(repositorioComercioMock.listarTodos()).thenReturn(Arrays.asList(cerca));
    when(repositorioHorarioMock.buscarPorComercioId(3L)).thenReturn(todosLosDiasAbierto());
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("comercios", -34.6037, -58.3816, null, Collections.emptyList());

    verify(servicioGeminiMock).llamar(anyString(), contains("abierto ahora, cierra a las 23:59"));
  }

  @Test
  public void laListaMuestraSiHayUnaPromocionMasBarataQueElPrecioAnotado() throws Exception {
    Reporte caro = reporteValido("Leche", 120.0, "Almacen Caro", "Calle 1", null, null);
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Arrays.asList(caro));
    Lista lista = new Lista("Semanal", 5L);
    lista.getItems().add(new ItemLista(lista, caro, 1));
    when(repositorioListaMock.buscarPorUsuarioId(5L)).thenReturn(Arrays.asList(lista));
    Comercio comercioPromo = new Comercio();
    comercioPromo.setNombre("Almacen Don Jose");
    Promocion promo = new Promocion();
    promo.setTitulo("Oferta leche");
    promo.setProductoNombre("Leche entera 1l");
    promo.setPrecioPromocional(90.0);
    promo.setComercio(comercioPromo);
    when(repositorioPromocionMock.listarActivas()).thenReturn(Arrays.asList(promo));
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("¿cuánto ahorro?", null, null, 5L, Collections.emptyList());

    verify(servicioGeminiMock)
      .llamar(anyString(), contains("→ en promoción: $90.00 en Almacen Don Jose"));
  }

  @Test
  public void laListaNoMarcaPromocionesQueNoSonMasBaratas() throws Exception {
    Reporte barato = reporteValido("Leche", 80.0, "Kiosco Barato", "Calle 2", null, null);
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Arrays.asList(barato));
    Lista lista = new Lista("Semanal", 5L);
    lista.getItems().add(new ItemLista(lista, barato, 1));
    when(repositorioListaMock.buscarPorUsuarioId(5L)).thenReturn(Arrays.asList(lista));
    Promocion promo = new Promocion();
    promo.setTitulo("Oferta leche");
    promo.setProductoNombre("Leche entera 1l");
    promo.setPrecioPromocional(90.0);
    when(repositorioPromocionMock.listarActivas()).thenReturn(Arrays.asList(promo));
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(RESPUESTA_OK);

    servicioAsistente.consultar("¿cuánto ahorro?", null, null, 5L, Collections.emptyList());

    verify(servicioGeminiMock)
      .llamar(anyString(), argThat(prompt -> !prompt.contains("en promoción")));
  }

  // --- helpers ---
  private List<HorarioComercio> todosLosDiasAbierto() {
    List<HorarioComercio> horarios = new ArrayList<>();
    for (int dia = 1; dia <= 7; dia++) {
      HorarioComercio horario = new HorarioComercio();
      horario.setDiaSemana(dia);
      horario.setAbierto(true);
      horario.setHoraApertura(LocalTime.MIN);
      horario.setHoraCierre(LocalTime.of(23, 59, 59));
      horarios.add(horario);
    }
    return horarios;
  }

  private Reporte reporteValido(
    String nombreProducto,
    Double precio,
    String nombreComercio,
    String direccion,
    Double lat,
    Double lon
  ) {
    Producto producto = new Producto();
    producto.setNombre(nombreProducto);

    Comercio comercio = new Comercio();
    comercio.setNombre(nombreComercio);
    comercio.setDireccion(direccion);
    comercio.setLatitud(lat);
    comercio.setLongitud(lon);

    Reporte reporte = new Reporte();
    reporte.setPrecio(precio);
    reporte.setProducto(producto);
    reporte.setComercio(comercio);
    return reporte;
  }
}
