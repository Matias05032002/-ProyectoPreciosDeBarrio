package com.tallerwebi.dominio.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ComercioYaRegistrado;
import com.tallerwebi.dominio.excepcion.DatosDeComercioInvalidos;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioRegistroComercioTest {

  private ServicioLogin servicioLoginMock;
  private ServicioComercio servicioComercioMock;
  private ServicioRegistroComercio servicio;

  @BeforeEach
  public void init() {
    servicioLoginMock = mock(ServicioLogin.class);
    servicioComercioMock = mock(ServicioComercio.class);
    servicio = new ServicioRegistroComercioImpl(servicioLoginMock, servicioComercioMock);
  }

  private void registrarValido() throws Exception {
    servicio.registrar(
      "comercio@unlam.com",
      "Password1",
      "  Almacén Don José ",
      "Almacen",
      " Rivadavia 1234",
      "La Matanza"
    );
  }

  private void conUsuarioCreadoConId(Long id) {
    Usuario creado = mock(Usuario.class);
    when(creado.getId()).thenReturn(id);
    when(servicioLoginMock.buscarPorEmail("comercio@unlam.com")).thenReturn(creado);
  }

  @Test
  public void conDatosNuevosCreaElUsuarioComercianteYSuComercio() throws Exception {
    conUsuarioCreadoConId(7L);

    registrarValido();

    ArgumentCaptor<Usuario> usuario = ArgumentCaptor.forClass(Usuario.class);
    verify(servicioLoginMock).registrar(usuario.capture());
    assertThat(usuario.getValue().getRol(), equalTo("COMERCIO"));
    assertThat(usuario.getValue().getEmail(), equalTo("comercio@unlam.com"));
    assertThat(usuario.getValue().getNombre(), equalTo("Almacén Don José"));

    ArgumentCaptor<Comercio> comercio = ArgumentCaptor.forClass(Comercio.class);
    verify(servicioComercioMock).guardarComercio(comercio.capture());
    assertThat(comercio.getValue().getNombre(), equalTo("Almacén Don José"));
    assertThat(comercio.getValue().getDireccion(), equalTo("Rivadavia 1234"));
    assertThat(comercio.getValue().getLocalidad(), equalTo("La Matanza"));
    assertThat(comercio.getValue().getActivo(), equalTo(true));
    assertThat(comercio.getValue().getUsuarioId(), equalTo(7L));
    assertThat(comercio.getValue().getFechaRegistro(), notNullValue());
  }

  @Test
  public void siElComercioYaEstabaCargadoSinDuenoLaCuentaSeQuedaConEse() throws Exception {
    conUsuarioCreadoConId(7L);
    Comercio sinDueno = new Comercio();
    sinDueno.setUsuarioId(null);
    when(servicioComercioMock.buscarPorDireccionYLocalidad("Rivadavia 1234", "La Matanza"))
      .thenReturn(sinDueno);

    registrarValido();

    assertThat(sinDueno.getUsuarioId(), equalTo(7L));
    verify(servicioComercioMock).actualizarComercio(sinDueno);
    verify(servicioComercioMock, never()).guardarComercio(any());
  }

  @Test
  public void siElComercioYaTieneDuenoLanzaComercioYaRegistradoSinCrearNada() throws Exception {
    Comercio conDueno = new Comercio();
    conDueno.setUsuarioId(3L);
    when(servicioComercioMock.buscarPorDireccionYLocalidad("Rivadavia 1234", "La Matanza"))
      .thenReturn(conDueno);

    assertThrows(ComercioYaRegistrado.class, this::registrarValido);

    verify(servicioLoginMock, never()).registrar(any());
    verify(servicioComercioMock, never()).guardarComercio(any());
  }

  @Test
  public void siElEmailYaExisteNoCreaElComercio() throws Exception {
    doThrow(new UsuarioExistente()).when(servicioLoginMock).registrar(any());

    assertThrows(UsuarioExistente.class, this::registrarValido);

    verify(servicioComercioMock, never()).guardarComercio(any());
    verify(servicioComercioMock, never()).actualizarComercio(any());
  }

  @Test
  public void conLocalidadInventadaLanzaDatosInvalidosSinCrearLaCuenta() throws Exception {
    assertThrows(
      DatosDeComercioInvalidos.class,
      () ->
        servicio.registrar(
          "comercio@unlam.com",
          "Password1",
          "Almacén",
          "Almacen",
          "Rivadavia 1234",
          "Narnia"
        )
    );

    verify(servicioLoginMock, never()).registrar(any());
  }

  @Test
  public void conCamposVaciosLanzaDatosInvalidosSinCrearLaCuenta() throws Exception {
    assertThrows(
      DatosDeComercioInvalidos.class,
      () ->
        servicio.registrar("c@unlam.com", "Password1", "Almacén", "Almacen", "   ", "La Matanza")
    );
    assertThrows(
      DatosDeComercioInvalidos.class,
      () -> servicio.registrar("c@unlam.com", "Password1", null, "Almacen", "Calle 1", "La Matanza")
    );
    assertThrows(
      DatosDeComercioInvalidos.class,
      () -> servicio.registrar("c@unlam.com", "Password1", "Almacén", " ", "Calle 1", "La Matanza")
    );

    verify(servicioLoginMock, never()).registrar(any());
  }

  @Test
  public void conNombreDireccionOTipoDemasiadoLargosLanzaDatosInvalidos() throws Exception {
    assertThrows(
      DatosDeComercioInvalidos.class,
      () ->
        servicio.registrar(
          "c@unlam.com",
          "Password1",
          "a".repeat(151),
          "Almacen",
          "Calle 1",
          "La Matanza"
        )
    );
    assertThrows(
      DatosDeComercioInvalidos.class,
      () ->
        servicio.registrar(
          "c@unlam.com",
          "Password1",
          "Almacén",
          "Almacen",
          "a".repeat(201),
          "La Matanza"
        )
    );
    assertThrows(
      DatosDeComercioInvalidos.class,
      () ->
        servicio.registrar(
          "c@unlam.com",
          "Password1",
          "Almacén",
          "a".repeat(51),
          "Calle 1",
          "La Matanza"
        )
    );

    verify(servicioLoginMock, never()).registrar(any());
  }

  @Test
  public void conLosLargosExactamenteEnElMaximoSeAcepta() throws Exception {
    conUsuarioCreadoConId(7L);

    servicio.registrar(
      "comercio@unlam.com",
      "Password1",
      "a".repeat(150),
      "b".repeat(50),
      "c".repeat(200),
      "La Matanza"
    );

    verify(servicioComercioMock).guardarComercio(any());
  }
}
