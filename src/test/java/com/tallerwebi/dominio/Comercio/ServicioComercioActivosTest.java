package com.tallerwebi.dominio.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;

public class ServicioComercioActivosTest {

  @Test
  public void listarTodosNoDeberiaIncluirLosComerciosDesactivados() {
    RepositorioComercio repositorio = mock(RepositorioComercio.class);
    Comercio activo = unComercio(true);
    Comercio sinDato = unComercio(null);
    Comercio desactivado = unComercio(false);
    when(repositorio.listarTodos()).thenReturn(List.of(activo, desactivado, sinDato));
    ServicioComercio servicio = new ServicioComercioImpl(
      repositorio,
      mock(RepositorioHorario.class),
      mock(ServicioGeolocalizacion.class)
    );

    // Un comercio viejo sin el dato "activo" se considera activo
    assertThat(servicio.listarTodos(), contains(activo, sinDato));
  }

  private Comercio unComercio(Boolean activo) {
    Comercio comercio = new Comercio();
    comercio.setActivo(activo);
    return comercio;
  }
}
