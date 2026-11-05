package com.tallerwebi.dominio.Asistente;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PalabrasClaveTest {

  @Test
  public void extraerIgnoraPalabrasVaciasNumerosYTildes() {
    List<String> palabras = PalabrasClave.extraer(
      "¿Dónde está más barata la leche? Tengo 10000 pesos"
    );

    assertThat(palabras, contains("leche"));
  }

  @Test
  public void extraerPasaElPluralAlSingular() {
    assertThat(PalabrasClave.extraer("aceites y fideos"), contains("aceite", "fideo"));
    assertThat(PalabrasClave.extraer("los panes"), contains("pan"));
  }

  @Test
  public void extraerNoRepiteNiPasaElMaximoDePalabras() {
    List<String> palabras = PalabrasClave.extraer(
      "leche leche arroz fideo aceite yerba azucar sal"
    );

    assertThat(palabras, hasSize(5));
    assertThat(palabras.stream().distinct().count(), equalTo(5L));
  }

  @Test
  public void extraerConTextoNuloDevuelveListaVacia() {
    assertThat(PalabrasClave.extraer(null), empty());
  }

  @Test
  public void coincideBuscaPorInicioDePalabraSinTildesNiMayusculas() {
    List<String> leche = Arrays.asList("leche");

    assertThat(PalabrasClave.coincide("LECHE entera La Serenísima", leche), is(true));
    assertThat(PalabrasClave.coincide("Lechuga criolla", leche), is(false));
    assertThat(PalabrasClave.coincide("Café molido", Arrays.asList("cafe")), is(true));
  }

  @Test
  public void coincideNoEncuentraUnaPalabraEnElMedioDeOtra() {
    assertThat(PalabrasClave.coincide("Empanadas de carne", Arrays.asList("pan")), is(false));
  }

  @Test
  public void coincideSinPalabrasNuncaCoincide() {
    assertThat(PalabrasClave.coincide("Leche", Collections.emptyList()), is(false));
  }
}
