package com.tallerwebi.dominio.Paginador;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPaginadorTest {

  private ServicioPaginadorImpl servicioPaginador;

  @BeforeEach
  public void init() {
    this.servicioPaginador = new ServicioPaginadorImpl();
  }

  // --- paginar ---

  @Test
  public void paginarPrimeraPaginaDevuelveLosNPrimeroElementos() {
    List<String> lista = Arrays.asList("a", "b", "c", "d", "e");
    List<String> resultado = servicioPaginador.paginar(lista, 0, 3);
    assertThat(resultado, contains("a", "b", "c"));
  }

  @Test
  public void paginarSegundaPaginaDevuelveLosElementosSiguientes() {
    List<String> lista = Arrays.asList("a", "b", "c", "d", "e");
    List<String> resultado = servicioPaginador.paginar(lista, 1, 3);
    assertThat(resultado, contains("d", "e"));
  }

  @Test
  public void paginarConPaginaFueraDeRangoDevuelveListaVacia() {
    List<String> lista = Arrays.asList("a", "b", "c");
    List<String> resultado = servicioPaginador.paginar(lista, 5, 3);
    assertThat(resultado, is(empty()));
  }

  @Test
  public void paginarConListaVaciaDevuelveListaVacia() {
    List<String> resultado = servicioPaginador.paginar(Collections.emptyList(), 0, 3);
    assertThat(resultado, is(empty()));
  }

  @Test
  public void paginarConTamanoExactoDevuelvePaginaCompleta() {
    List<String> lista = Arrays.asList("a", "b", "c");
    List<String> resultado = servicioPaginador.paginar(lista, 0, 3);
    assertThat(resultado, hasSize(3));
  }

  // --- calcularTotalPaginas ---

  @Test
  public void calcularTotalPaginasConTotalExactoDevuelveNumeroCorrecto() {
    int resultado = servicioPaginador.calcularTotalPaginas(9, 3);
    assertThat(resultado, is(3));
  }

  @Test
  public void calcularTotalPaginasConResidoDevuelveRedondeadoArriba() {
    int resultado = servicioPaginador.calcularTotalPaginas(10, 3);
    assertThat(resultado, is(4));
  }

  @Test
  public void calcularTotalPaginasConCeroElementosDevuelveCero() {
    int resultado = servicioPaginador.calcularTotalPaginas(0, 3);
    assertThat(resultado, is(0));
  }

  @Test
  public void calcularTotalPaginasConTamanoMenorAUnoDevuelveCero() {
    int resultado = servicioPaginador.calcularTotalPaginas(10, 0);
    assertThat(resultado, is(0));
  }

  @Test
  public void calcularTotalPaginasConUnSoloElementoDevuelveUnaPagina() {
    int resultado = servicioPaginador.calcularTotalPaginas(1, 9);
    assertThat(resultado, is(1));
  }
}
