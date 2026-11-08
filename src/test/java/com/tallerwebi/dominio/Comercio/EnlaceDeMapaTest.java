package com.tallerwebi.dominio.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

public class EnlaceDeMapaTest {

  private Comercio comercio(String direccion, String localidad, Double latitud, Double longitud) {
    Comercio comercio = new Comercio();
    comercio.setDireccion(direccion);
    comercio.setLocalidad(localidad);
    comercio.setLatitud(latitud);
    comercio.setLongitud(longitud);
    return comercio;
  }

  @Test
  public void conCoordenadasDeberiaArmarLaRutaHastaEseLugar() {
    Comercio comercio = comercio("Calle 456", "BA", -34.6, -58.4);

    assertThat(
      EnlaceDeMapa.para(comercio),
      equalTo("https://www.google.com/maps/dir/?api=1&destination=-34.6,-58.4")
    );
  }

  @Test
  public void sinCoordenadasDeberiaBuscarPorDireccionYLocalidad() {
    Comercio comercio = comercio("Av. Rivadavia 1234", "La Matanza", null, null);

    assertThat(
      EnlaceDeMapa.para(comercio),
      equalTo(
        "https://www.google.com/maps/search/?api=1&query=Av.%20Rivadavia%201234%2C%20La%20Matanza"
      )
    );
  }

  @Test
  public void conSoloUnaCoordenadaDeberiaBuscarPorDireccion() {
    Comercio comercio = comercio("Calle 456", "BA", -34.6, null);

    assertThat(
      EnlaceDeMapa.para(comercio),
      equalTo("https://www.google.com/maps/search/?api=1&query=Calle%20456%2C%20BA")
    );
  }

  @Test
  public void sinLocalidadDeberiaBuscarSoloPorDireccion() {
    Comercio comercio = comercio("Calle 456", null, null, null);

    assertThat(
      EnlaceDeMapa.para(comercio),
      equalTo("https://www.google.com/maps/search/?api=1&query=Calle%20456")
    );
  }

  @Test
  public void deberiaEscaparLosCaracteresEspecialesDeLaDireccion() {
    Comercio comercio = comercio("Pasaje Ñandú & Cía #5", "Ramos Mejía", null, null);

    assertThat(
      EnlaceDeMapa.para(comercio),
      equalTo(
        "https://www.google.com/maps/search/?api=1&query=Pasaje%20%C3%91and%C3%BA%20%26%20C%C3%ADa%20%235%2C%20Ramos%20Mej%C3%ADa"
      )
    );
  }

  @Test
  public void sinCoordenadasNiDireccionNoDeberiaHaberEnlace() {
    assertThat(EnlaceDeMapa.para(comercio(" ", "BA", null, null)), nullValue());
    assertThat(EnlaceDeMapa.para(comercio(null, null, null, null)), nullValue());
    assertThat(EnlaceDeMapa.para(null), nullValue());
  }
}
