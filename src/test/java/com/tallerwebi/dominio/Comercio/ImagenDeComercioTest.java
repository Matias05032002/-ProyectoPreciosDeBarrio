package com.tallerwebi.dominio.Comercio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class ImagenDeComercioTest {

  private static final String BASE = "/img/comercio/";

  @Test
  void cadaTipoDeComercioDeberiaTenerSuImagen() {
    assertEquals(BASE + "almacen.jpg", ImagenDeComercio.rutaPara("Almacen"));
    assertEquals(BASE + "verduleria.jpg", ImagenDeComercio.rutaPara("Verduleria"));
    assertEquals(BASE + "carniceria.jpg", ImagenDeComercio.rutaPara("Carniceria"));
    assertEquals(BASE + "kiosco.jpg", ImagenDeComercio.rutaPara("Kiosco"));
    assertEquals(BASE + "supermercado.jpg", ImagenDeComercio.rutaPara("Supermercado"));
    assertNull(ImagenDeComercio.rutaPara("Otro"));
  }

  @Test
  void elTipoDeberiaIgnorarTildesMayusculasYEspacios() {
    assertEquals(BASE + "verduleria.jpg", ImagenDeComercio.rutaPara("  VERDULERÍA "));
    assertEquals(BASE + "almacen.jpg", ImagenDeComercio.rutaPara("Almacén"));
    assertEquals(BASE + "carniceria.jpg", ImagenDeComercio.rutaPara("carnicería"));
  }

  @Test
  void unTipoDesconocidoVacioONuloNoDeberiaTenerImagen() {
    assertNull(ImagenDeComercio.rutaPara("Ferreteria"));
    assertNull(ImagenDeComercio.rutaPara(""));
    assertNull(ImagenDeComercio.rutaPara(null));
  }

  @Test
  void todasLasFotosDeberianExistirYSerJpegValidos() throws IOException {
    for (String tipo : ImagenDeComercio.TIPOS) {
      Path archivo = Paths.get("src/main/webapp/resources/core" + ImagenDeComercio.rutaPara(tipo));
      assertTrue(Files.exists(archivo), "Falta la imagen de " + tipo + ": " + archivo);
      byte[] bytes = Files.readAllBytes(archivo);
      boolean esJpeg =
        bytes.length > 1000 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8;
      assertTrue(esJpeg, "No es un JPEG valido: " + archivo);
    }
  }

  @Test
  void elComercioSinFotoDeberiaDarLaImagenSegunSuTipo() {
    Comercio comercio = new Comercio();
    comercio.setTipo("Kiosco");

    assertEquals(BASE + "kiosco.jpg", comercio.imagenPorDefecto());
  }

  @Test
  void elComercioDeTipoOtroNoDeberiaTenerImagenPorDefecto() {
    Comercio comercio = new Comercio();
    comercio.setTipo("Otro");

    assertNull(comercio.imagenPorDefecto());
  }
}
