package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class LocalidadesTest {

  @Test
  public void unaLocalidadDeLaListaEsValida() {
    assertThat(Localidades.esValida("La Matanza"), equalTo(true));
    assertThat(Localidades.esValida("Buenos Aires"), equalTo(true));
  }

  @Test
  public void laLocalidadConEspaciosEnLosBordesEsValida() {
    assertThat(Localidades.esValida("  Pilar "), equalTo(true));
  }

  @Test
  public void unaLocalidadInventadaNoEsValida() {
    assertThat(Localidades.esValida("Narnia"), equalTo(false));
  }

  @Test
  public void laLocalidadVaciaONulaNoEsValida() {
    assertThat(Localidades.esValida(""), equalTo(false));
    assertThat(Localidades.esValida("   "), equalTo(false));
    assertThat(Localidades.esValida(null), equalTo(false));
  }

  @Test
  public void laComparacionDistingueMayusculas() {
    assertThat(Localidades.esValida("pilar"), equalTo(false));
  }
}
