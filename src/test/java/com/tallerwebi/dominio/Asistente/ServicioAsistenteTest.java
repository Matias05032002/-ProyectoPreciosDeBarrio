package com.tallerwebi.dominio.Asistente;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Gemini.ServicioGemini;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioAsistenteTest {

  private ServicioAsistente servicioAsistente;
  private RepositorioReporte repositorioReporteMock;
  private ServicioGemini servicioGeminiMock;

  @BeforeEach
  public void init() {
    this.repositorioReporteMock = mock(RepositorioReporte.class);
    this.servicioGeminiMock = mock(ServicioGemini.class);
    this.servicioAsistente = new ServicioAsistenteImpl(repositorioReporteMock, servicioGeminiMock);
  }

  @Test
  public void consultarSinReportesDevuelveRespuestaDeGemini() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    String respuestaJson =
      "{\"choices\":[{\"message\":{\"content\":\"No hay productos disponibles.\"}}]}";
    when(servicioGeminiMock.llamar(anyString(), anyString())).thenReturn(respuestaJson);

    String resultado = servicioAsistente.consultar("leche", null, null, Collections.emptyList());

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

    String resultado = servicioAsistente.consultar("leche", null, null, Collections.emptyList());

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

    String resultado = servicioAsistente.consultar("leche", null, null, Collections.emptyList());

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

    servicioAsistente.consultar("leche", null, null, Collections.emptyList());

    verify(servicioGeminiMock).llamar(anyString(), argThat(contexto -> !contexto.contains(" km")));
  }

  @Test
  public void consultarConUbicacionInformaLaDistanciaEnElContexto() throws Exception {
    Reporte reporte = reporteValido("Leche", 100.0, "Almacen Don Jose", "Calle 1", -34.6, -58.4);
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Arrays.asList(reporte));
    when(servicioGeminiMock.llamar(anyString(), anyString()))
      .thenReturn("{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}");

    servicioAsistente.consultar("leche", -34.61, -58.41, Collections.emptyList());

    verify(servicioGeminiMock).llamar(anyString(), contains(" km"));
  }

  @Test
  public void consultarCuandoGeminiLanzaExcepcionPropagaError() throws Exception {
    when(repositorioReporteMock.listarRecientes(7)).thenReturn(Collections.emptyList());
    when(servicioGeminiMock.llamar(anyString(), anyString()))
      .thenThrow(new Exception("Error de red"));

    org.junit.jupiter.api.Assertions.assertThrows(
      Exception.class,
      () -> servicioAsistente.consultar("leche", null, null, Collections.emptyList())
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

    servicioAsistente.consultar("leche", null, null, Collections.emptyList());

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

    servicioAsistente.consultar("qué hay disponible", null, null, Collections.emptyList());

    verify(servicioGeminiMock, times(1)).llamar(anyString(), contains("Leche"));
    verify(servicioGeminiMock, times(1)).llamar(anyString(), contains("Arroz"));
  }

  // --- helpers ---

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
