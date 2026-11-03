package com.tallerwebi.dominio.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import com.tallerwebi.dominio.Comercio.UbicacionSugerida;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class ServicioGeolocalizacionImplTest {

  private static final String SIN_RESULTADOS_GEOREF = "{\"cantidad\":0,\"direcciones\":[]}";
  private static final String NOMINATIM_UN_LUGAR =
    "[{\"lat\":\"-34.6701\",\"lon\":\"-58.5627\",\"display_name\":\"Arieta 3000, San Justo, Partido de La Matanza, Buenos Aires, Argentina\"}]";

  private final List<String> urlsConsultadas = new ArrayList<>();

  private ServicioGeolocalizacionImpl servicioQueResponde(Function<String, String> respuestas) {
    return new ServicioGeolocalizacionImpl(
      url -> {
        urlsConsultadas.add(url);
        String respuesta = respuestas.apply(url);
        if (respuesta == null) throw new IOException("sin conexión");
        return respuesta;
      },
      0
    );
  }

  private static String respuestaRealDeGeoref() throws IOException {
    try (
      InputStream entrada =
        ServicioGeolocalizacionImplTest.class.getResourceAsStream(
            "/geolocalizacion/georef-arieta-3000.json"
          )
    ) {
      return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  @Test
  public void unaDireccionMalEscritaDevuelveLaCalleNormalizadaConCoordenadas() throws IOException {
    String georef = respuestaRealDeGeoref();
    ServicioGeolocalizacionImpl servicio = servicioQueResponde(url -> georef);

    List<UbicacionSugerida> sugerencias = servicio.buscar("arrieta 3000", "La Matanza");

    assertThat(sugerencias, hasSize(1));
    UbicacionSugerida primera = sugerencias.get(0);
    assertThat(
      primera.nomenclatura(),
      equalTo("DR IGNACIO ARIETA 3000, San Justo, La Matanza, Buenos Aires")
    );
    assertThat(primera.localidad(), equalTo("San Justo"));
    assertThat(primera.latitud(), equalTo(-34.68789160984074));
    assertThat(primera.longitud(), equalTo(-58.54824429666323));
    assertThat(primera.fuente(), equalTo("georef"));
  }

  @Test
  public void lasOpcionesSinCoordenadasSeDescartan() throws IOException {
    List<UbicacionSugerida> sugerencias = new ServicioGeolocalizacionImpl(url -> "", 0)
      .leerGeoref(respuestaRealDeGeoref());

    assertThat(sugerencias, hasSize(1));
  }

  @Test
  public void paraUnPartidoSeFiltraPorProvinciaDeBuenosAiresYPartido() {
    ServicioGeolocalizacionImpl servicio = new ServicioGeolocalizacionImpl(url -> "", 0);

    String url = servicio.urlGeoref("arieta 3000", "La Matanza", true);

    assertThat(url, startsWith("https://apis.datos.gob.ar/georef/api/v2.0/direcciones?"));
    assertThat(url, containsString("direccion=arieta%203000"));
    assertThat(url, containsString("provincia=06"));
    assertThat(url, containsString("departamento=La%20Matanza"));
  }

  @Test
  public void paraCabaSeFiltraSoloPorLaCiudadAutonoma() {
    ServicioGeolocalizacionImpl servicio = new ServicioGeolocalizacionImpl(url -> "", 0);

    String url = servicio.urlGeoref("Corrientes 1500", "Buenos Aires", true);

    assertThat(url, containsString("provincia=02"));
    assertThat(url, not(containsString("departamento=")));
  }

  @Test
  public void siNoHayResultadosPorPartidoSeReintentaPorLocalidad() {
    ServicioGeolocalizacionImpl servicio = servicioQueResponde(url ->
      url.contains("localidad=Mar%20del%20Plata")
        ? "{\"direcciones\":[{\"nomenclatura\":\"AV COLON 1500, Mar del Plata\",\"localidad\":{\"nombre\":\"Mar del Plata\"},\"ubicacion\":{\"lat\":-38.0,\"lon\":-57.55}}]}"
        : SIN_RESULTADOS_GEOREF
    );

    List<UbicacionSugerida> sugerencias = servicio.buscar("colon 1500", "Mar del Plata");

    assertThat(sugerencias, hasSize(1));
    assertThat(urlsConsultadas.get(0), containsString("departamento=Mar%20del%20Plata"));
    assertThat(urlsConsultadas.get(1), containsString("localidad=Mar%20del%20Plata"));
  }

  @Test
  public void siGeorefNoEncuentraNadaSeUsaNominatim() {
    ServicioGeolocalizacionImpl servicio = servicioQueResponde(url ->
      url.contains("nominatim") ? NOMINATIM_UN_LUGAR : SIN_RESULTADOS_GEOREF
    );

    List<UbicacionSugerida> sugerencias = servicio.buscar("arieta 3000", "La Matanza");

    assertThat(sugerencias, hasSize(1));
    assertThat(sugerencias.get(0).fuente(), equalTo("nominatim"));
    assertThat(sugerencias.get(0).latitud(), equalTo(-34.6701));
    String urlNominatim = urlsConsultadas.get(urlsConsultadas.size() - 1);
    assertThat(urlNominatim, containsString("countrycodes=ar"));
    assertThat(urlNominatim, containsString("La%20Matanza"));
  }

  @Test
  public void siLosServiciosFallanDevuelveListaVaciaSinLanzarExcepcion() {
    ServicioGeolocalizacionImpl servicio = servicioQueResponde(url -> null);

    assertThat(servicio.buscar("arieta 3000", "La Matanza"), empty());
  }

  @Test
  public void unaRespuestaInvalidaSeTomaComoFallaYNoRompe() {
    ServicioGeolocalizacionImpl servicio = servicioQueResponde(url -> "<html>error</html>");

    assertThat(servicio.buscar("arieta 3000", "La Matanza"), empty());
  }

  @Test
  public void laMismaDireccionNoSeConsultaDosVeces() throws IOException {
    String georef = respuestaRealDeGeoref();
    ServicioGeolocalizacionImpl servicio = servicioQueResponde(url -> georef);

    servicio.buscar("arieta 3000", "La Matanza");
    servicio.buscar("  ARIETA 3000 ", "La Matanza");

    assertThat(urlsConsultadas, hasSize(1));
  }

  @Test
  public void siHuboUnaFallaNoSeGuardaElResultadoVacioEnCache() {
    AtomicBoolean hayConexion = new AtomicBoolean(false);
    ServicioGeolocalizacionImpl servicio = servicioQueResponde(url ->
      hayConexion.get() ? SIN_RESULTADOS_GEOREF : null
    );

    servicio.buscar("arieta 3000", "La Matanza");
    int consultasSinConexion = urlsConsultadas.size();
    hayConexion.set(true);
    servicio.buscar("arieta 3000", "La Matanza");

    // Con conexión vuelve a consultar en lugar de responder la lista vacía de la falla
    assertThat(urlsConsultadas.size() > consultasSinConexion, equalTo(true));
  }

  @Test
  public void unaDireccionVaciaNoConsultaNada() {
    ServicioGeolocalizacionImpl servicio = servicioQueResponde(url -> SIN_RESULTADOS_GEOREF);

    assertThat(servicio.buscar("  ", "La Matanza"), empty());
    assertThat(servicio.buscar(null, "La Matanza"), empty());
    assertThat(urlsConsultadas, empty());
  }
}
