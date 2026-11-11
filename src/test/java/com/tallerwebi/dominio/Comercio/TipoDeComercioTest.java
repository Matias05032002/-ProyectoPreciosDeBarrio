package com.tallerwebi.dominio.Comercio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TipoDeComercioTest {

  @Test
  void normalizarDeberiaDevolverElTipoDeLaListaIgnorandoTildesMayusculasYEspacios() {
    assertEquals("Verduleria", TipoDeComercio.normalizar("  VERDULERÍA "));
    assertEquals("Almacen", TipoDeComercio.normalizar("almacén"));
    assertEquals("Otro", TipoDeComercio.normalizar("otro"));
  }

  @Test
  void normalizarUnTipoInventadoVacioONuloDeberiaDevolverNull() {
    assertNull(TipoDeComercio.normalizar("Ferreteria"));
    assertNull(TipoDeComercio.normalizar(" "));
    assertNull(TipoDeComercio.normalizar(null));
  }

  @Test
  void inferirDeberiaReconocerElTipoPorElNombre() {
    assertEquals("Verduleria", TipoDeComercio.inferirDe("Verdulería Don Pepe"));
    assertEquals("Verduleria", TipoDeComercio.inferirDe("Frutería Los Tilos"));
    assertEquals("Carniceria", TipoDeComercio.inferirDe("Carnicería El Gaucho"));
    assertEquals("Carniceria", TipoDeComercio.inferirDe("Pollería Doña Rosa"));
    assertEquals("Kiosco", TipoDeComercio.inferirDe("Kiosco 24 hs"));
    assertEquals("Kiosco", TipoDeComercio.inferirDe("Quiosco de la esquina"));
    assertEquals("Supermercado", TipoDeComercio.inferirDe("Supermercado Chino Lin"));
    assertEquals("Supermercado", TipoDeComercio.inferirDe("Super Día"));
    assertEquals("Supermercado", TipoDeComercio.inferirDe("Carrefour Express"));
    assertEquals("Almacen", TipoDeComercio.inferirDe("Almacén Don José"));
    assertEquals("Almacen", TipoDeComercio.inferirDe("Despensa La Esquina"));
  }

  @Test
  void inferirUnNombreSinPistasONuloDeberiaDarOtro() {
    assertEquals("Otro", TipoDeComercio.inferirDe("Don Pepe"));
    assertEquals("Otro", TipoDeComercio.inferirDe(""));
    assertEquals("Otro", TipoDeComercio.inferirDe(null));
  }

  @Test
  void inferirNoDeberiaConfundirPalabrasQueSoloContienenElFragmento() {
    assertEquals("Otro", TipoDeComercio.inferirDe("Superación SRL"));
    assertEquals("Otro", TipoDeComercio.inferirDe("Diagonal 74"));
  }

  @Test
  void paraGuardarDeberiaRespetarUnTipoValidoYDeducirElInvalido() {
    assertEquals("Kiosco", TipoDeComercio.paraGuardar("kiosco", "Verdulería X"));
    assertEquals("Verduleria", TipoDeComercio.paraGuardar(null, "Verdulería X"));
    assertEquals("Carniceria", TipoDeComercio.paraGuardar("inventado", "Carnicería Y"));
    assertEquals("Otro", TipoDeComercio.paraGuardar("", "Don Pepe"));
  }

  @Test
  void elegirDeberiaPreferirLaPistaDelNombreSobreLaSugerencia() {
    assertEquals("Verduleria", TipoDeComercio.elegir("Verdulería Don Pepe", "Kiosco"));
  }

  @Test
  void elegirSinPistaEnElNombreDeberiaUsarLaSugerenciaValida() {
    assertEquals("Carniceria", TipoDeComercio.elegir("Los Hermanos", "carnicería"));
  }

  @Test
  void elegirSinPistaNiSugerenciaValidaDeberiaDarOtro() {
    assertEquals("Otro", TipoDeComercio.elegir("Los Hermanos", "inventado"));
    assertEquals("Otro", TipoDeComercio.elegir("Los Hermanos", null));
  }
}
