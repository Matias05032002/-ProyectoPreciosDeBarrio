package com.tallerwebi.dominio.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioComercioTest {

  private ServicioComercio servicioComercio;
  private RepositorioComercio repositorioComercioMock;

  @BeforeEach
  public void init() {
    this.repositorioComercioMock = mock(RepositorioComercio.class);
    this.servicioComercio = new ServicioComercioImpl(this.repositorioComercioMock);
  }

  @Test
  public void guardarUnNuevoComercio() {
    Comercio comercio = new Comercio();
    comercio.setNombre("Almacen de Matias");
    when(this.repositorioComercioMock.guardarComercio(comercio)).thenReturn(comercio);

    Comercio resultado = this.servicioComercio.guardarComercio(comercio);

    assertThat(resultado, equalTo(comercio));
    verify(this.repositorioComercioMock, times(1)).guardarComercio(comercio);
  }

  @Test
  public void listarTodosLosComercios() {
    List<Comercio> comercios = Arrays.asList(new Comercio(), new Comercio());
    when(this.repositorioComercioMock.listarTodos()).thenReturn(comercios);

    List<Comercio> resultadosLista = this.servicioComercio.listarTodos();

    assertThat(resultadosLista, equalTo(comercios));
    verify(this.repositorioComercioMock, times(1)).listarTodos();
  }

  @Test
  public void buscarComercioPorId() {
    Comercio comercio = new Comercio();
    comercio.setNombre("Almacen de Matias Llanos");
    when(this.repositorioComercioMock.buscarComercio(1L)).thenReturn(comercio);

    Comercio resultadoComercio = this.servicioComercio.buscarComercio(1L);

    assertThat(resultadoComercio, equalTo(comercio));
    verify(this.repositorioComercioMock, times(1)).buscarComercio(1L);
  }

  @Test
  public void buscarComerciosPorNombre() {
    Comercio comercio = new Comercio();
    List<Comercio> listaDeComercios = Arrays.asList(comercio);
    when(this.repositorioComercioMock.buscarComercioPorNombre("Almacen Mati"))
      .thenReturn(listaDeComercios);

    List<Comercio> comercioBuscado = this.servicioComercio.buscarComercioPorNombre("Almacen Mati");

    assertThat(comercioBuscado, equalTo(listaDeComercios));
    verify(this.repositorioComercioMock, times(1)).buscarComercioPorNombre("Almacen Mati");
  }

  @Test
  public void buscarComercioPorNombreSiNoExisteRetornaNull() {
    when(this.repositorioComercioMock.buscarComercioPorNombre("ComercioInexistente"))
      .thenReturn(Collections.emptyList());

    List<Comercio> comercioBuscado =
      this.servicioComercio.buscarComercioPorNombre("ComercioInexistente");

    assertThat(comercioBuscado, equalTo(Collections.emptyList()));
    verify(this.repositorioComercioMock, times(1)).buscarComercioPorNombre("ComercioInexistente");
  }
}
