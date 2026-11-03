package com.tallerwebi.dominio.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

public class ComercioTest {

  @Test
  public void unComercioNuevoNoTieneFoto() {
    assertThat(new Comercio().tieneFoto(), is(false));
  }

  @Test
  public void unComercioConFotoVaciaNoTieneFoto() {
    Comercio comercio = new Comercio();
    comercio.setFoto(null);

    assertThat(comercio.tieneFoto(), is(false));
  }

  @Test
  public void unComercioConBytesDeFotoTieneFoto() {
    Comercio comercio = new Comercio();
    comercio.setFoto(new byte[] { 1, 2, 3 });

    assertThat(comercio.tieneFoto(), is(true));
  }
}
