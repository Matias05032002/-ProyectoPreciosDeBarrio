package com.tallerwebi.dominio.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioImagenEnCadenaTest {

  private ServicioImagenProducto primera;
  private ServicioImagenProducto segunda;
  private ServicioImagenProducto tercera;
  private ServicioImagenProducto cadena;

  @BeforeEach
  public void init() {
    primera = mock(ServicioImagenProducto.class);
    segunda = mock(ServicioImagenProducto.class);
    tercera = mock(ServicioImagenProducto.class);
    cadena = new ServicioImagenEnCadena(List.of(primera, segunda, tercera));
  }

  @Test
  public void devuelveLaFotoDeLaPrimeraFuenteQueLaTiene() {
    when(primera.buscarImagen("Leche", "Manfrey", "1l")).thenReturn("https://a/1.jpg");

    assertThat(cadena.buscarImagen("Leche", "Manfrey", "1l"), is("https://a/1.jpg"));
    verify(segunda, never()).buscarImagen("Leche", "Manfrey", "1l");
    verify(tercera, never()).buscarImagen("Leche", "Manfrey", "1l");
  }

  @Test
  public void siLaPrimeraNoLaTienePruebaConLaSiguiente() {
    when(segunda.buscarImagen("Leche", "Manfrey", "1l")).thenReturn("https://b/2.jpg");

    assertThat(cadena.buscarImagen("Leche", "Manfrey", "1l"), is("https://b/2.jpg"));
    verify(tercera, never()).buscarImagen("Leche", "Manfrey", "1l");
  }

  @Test
  public void llegaHastaLaUltimaFuente() {
    when(tercera.buscarImagen("Fideos", "Cuquets", "500g")).thenReturn("https://c/3.jpg");

    assertThat(cadena.buscarImagen("Fideos", "Cuquets", "500g"), is("https://c/3.jpg"));
  }

  @Test
  public void siNingunaFuenteLaTieneDevuelveNull() {
    assertThat(cadena.buscarImagen("Fideos", "Cuquets", "500g"), is(nullValue()));
  }
}
