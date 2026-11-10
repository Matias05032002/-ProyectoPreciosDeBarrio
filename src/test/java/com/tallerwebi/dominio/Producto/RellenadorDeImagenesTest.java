package com.tallerwebi.dominio.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RellenadorDeImagenesTest {

  private ServicioProducto servicioProductoMock;
  private RellenadorDeImagenes rellenador;

  @BeforeEach
  public void init() {
    servicioProductoMock = mock(ServicioProducto.class);
    rellenador = new RellenadorDeImagenes(servicioProductoMock);
  }

  @Test
  public void rellenarCompletaLaImagenDeCadaProductoSinFoto() {
    when(servicioProductoMock.listarIdsDeProductosSinImagen())
      .thenReturn(Arrays.asList(1L, 2L, 3L));

    int completados = rellenador.rellenar();

    assertThat(completados, equalTo(3));
    verify(servicioProductoMock).completarImagenDelProducto(1L);
    verify(servicioProductoMock).completarImagenDelProducto(2L);
    verify(servicioProductoMock).completarImagenDelProducto(3L);
  }

  @Test
  public void rellenarSigueConLosDemasSiUnProductoFalla() {
    when(servicioProductoMock.listarIdsDeProductosSinImagen()).thenReturn(Arrays.asList(1L, 2L));
    doThrow(new IllegalStateException("sin red"))
      .when(servicioProductoMock)
      .completarImagenDelProducto(1L);

    int completados = rellenador.rellenar();

    assertThat(completados, equalTo(1));
    verify(servicioProductoMock).completarImagenDelProducto(2L);
  }

  @Test
  public void rellenarSinProductosSinFotoNoHaceNada() {
    when(servicioProductoMock.listarIdsDeProductosSinImagen()).thenReturn(Collections.emptyList());

    assertThat(rellenador.rellenar(), equalTo(0));
    verify(servicioProductoMock, never()).completarImagenDelProducto(anyLong());
  }

  @Test
  public void siFallaLaConsultaInicialNoPropagaLaExcepcion() {
    when(servicioProductoMock.listarIdsDeProductosSinImagen())
      .thenThrow(new IllegalStateException("bd"));

    assertThat(rellenador.rellenar(), equalTo(0));
  }
}
