package com.tallerwebi.dominio.Imagen;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

public class TipoDeImagenTest {

  @Test
  public void reconoceUnJpeg() {
    byte[] jpeg = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0 };

    assertThat(TipoDeImagen.detectar(jpeg), equalTo("image/jpeg"));
  }

  @Test
  public void reconoceUnPng() {
    byte[] png = { (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0 };

    assertThat(TipoDeImagen.detectar(png), equalTo("image/png"));
  }

  @Test
  public void reconoceUnWebp() {
    byte[] webp = { 'R', 'I', 'F', 'F', 1, 2, 3, 4, 'W', 'E', 'B', 'P', 0 };

    assertThat(TipoDeImagen.detectar(webp), equalTo("image/webp"));
  }

  @Test
  public void unRiffQueNoEsWebpNoSeAcepta() {
    byte[] wav = { 'R', 'I', 'F', 'F', 1, 2, 3, 4, 'W', 'A', 'V', 'E', 0 };

    assertThat(TipoDeImagen.detectar(wav), nullValue());
  }

  @Test
  public void unGifNoSeAcepta() {
    byte[] gif = { 'G', 'I', 'F', '8', '9', 'a', 0, 0, 0, 0, 0, 0 };

    assertThat(TipoDeImagen.detectar(gif), nullValue());
  }

  @Test
  public void unArchivoMuyCortoOVacioNoSeAcepta() {
    assertThat(TipoDeImagen.detectar(new byte[] { (byte) 0xFF, (byte) 0xD8 }), nullValue());
    assertThat(TipoDeImagen.detectar(new byte[0]), nullValue());
    assertThat(TipoDeImagen.detectar(null), nullValue());
  }
}
