package com.tallerwebi.dominio.Consulta;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.DatosInvalidos;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ServicioConsultaTest {

  private RepositorioConsulta repositorioConsultaMock;
  private ServicioConsultaImpl servicioConsulta;

  @BeforeEach
  public void setUp() {
    repositorioConsultaMock = mock(RepositorioConsulta.class);
    servicioConsulta = new ServicioConsultaImpl(repositorioConsultaMock);
  }

  @Test
  public void guardarConsultaAsignaFechaEstadoYGuarda() {
    Consulta consulta = new Consulta();

    consulta.setNombre("Juan");
    consulta.setEmail("juan@email.com");
    consulta.setAsunto("Sugerencia");
    consulta.setMensaje("Me gustaría sugerir una mejora.");

    servicioConsulta.guardar(consulta);

    assertNotNull(consulta.getFecha());
    assertEquals("Pendiente", consulta.getEstado());

    verify(repositorioConsultaMock).guardar(consulta);
  }

  @Test
  public void listarTodasDevuelveLasConsultasDelRepositorio() {
    Consulta primera = new Consulta();
    primera.setNombre("Juan");

    Consulta segunda = new Consulta();
    segunda.setNombre("María");

    List<Consulta> consultasEsperadas = Arrays.asList(primera, segunda);

    when(repositorioConsultaMock.listarTodas()).thenReturn(consultasEsperadas);

    List<Consulta> resultado = servicioConsulta.listarTodas();

    assertSame(consultasEsperadas, resultado);
    assertEquals(2, resultado.size());
    verify(repositorioConsultaMock).listarTodas();
  }

  private Consulta consultaValida() {
    Consulta consulta = new Consulta();
    consulta.setNombre("Juan");
    consulta.setEmail("juan@email.com");
    consulta.setAsunto("Consulta");
    consulta.setMensaje("Hola, tengo una duda.");
    return consulta;
  }

  private void assertInvalida(Consulta consulta, String mensajeEsperado) {
    DatosInvalidos error = assertThrows(
      DatosInvalidos.class,
      () -> servicioConsulta.guardar(consulta)
    );
    assertEquals(mensajeEsperado, error.getMessage());
    verify(repositorioConsultaMock, never()).guardar(any(Consulta.class));
  }

  @Test
  public void guardarConsultaSinNombreLanzaDatosInvalidos() {
    Consulta consulta = consultaValida();
    consulta.setNombre("   ");

    assertInvalida(consulta, "El nombre es obligatorio");
  }

  @Test
  public void guardarConsultaConEmailInvalidoLanzaDatosInvalidos() {
    Consulta consulta = consultaValida();
    consulta.setEmail("esto-no-es-un-mail");

    assertInvalida(consulta, "El correo electrónico no es válido");
  }

  @Test
  public void guardarConsultaSinEmailLanzaDatosInvalidos() {
    Consulta consulta = consultaValida();
    consulta.setEmail(null);

    assertInvalida(consulta, "El correo electrónico es obligatorio");
  }

  @Test
  public void guardarConsultaConAsuntoInventadoLanzaDatosInvalidos() {
    Consulta consulta = consultaValida();
    consulta.setAsunto("Hackeo");

    assertInvalida(consulta, "Elegí un asunto de la lista");
  }

  @Test
  public void guardarConsultaSinAsuntoLanzaDatosInvalidos() {
    Consulta consulta = consultaValida();
    consulta.setAsunto("");

    assertInvalida(consulta, "Elegí un asunto de la lista");
  }

  @Test
  public void guardarConsultaSinMensajeLanzaDatosInvalidos() {
    Consulta consulta = consultaValida();
    consulta.setMensaje("");

    assertInvalida(consulta, "El mensaje es obligatorio");
  }

  @Test
  public void guardarConsultaConMensajeMuyLargoLanzaDatosInvalidos() {
    Consulta consulta = consultaValida();
    consulta.setMensaje("a".repeat(2001));

    assertInvalida(consulta, "El mensaje no puede superar los 2000 caracteres");
  }

  @Test
  public void guardarConsultaConMensajeDeLargoMaximoLoAcepta() {
    Consulta consulta = consultaValida();
    consulta.setMensaje("a".repeat(2000));

    servicioConsulta.guardar(consulta);

    verify(repositorioConsultaMock).guardar(consulta);
  }

  @Test
  public void guardarConsultaQuitaLosEspaciosDeLosBordes() {
    Consulta consulta = consultaValida();
    consulta.setNombre("  Juan ");
    consulta.setEmail(" juan@email.com ");
    consulta.setMensaje("  Hola  ");

    servicioConsulta.guardar(consulta);

    assertEquals("Juan", consulta.getNombre());
    assertEquals("juan@email.com", consulta.getEmail());
    assertEquals("Hola", consulta.getMensaje());
  }

  // Largos máximos de la consulta: nombre 100 y correo 150, coinciden con las columnas de la base

  @Test
  public void unNombreDeCienCaracteresSeAcepta() {
    Consulta consulta = consultaValida();
    consulta.setNombre("n".repeat(100));

    assertDoesNotThrow(() -> servicioConsulta.guardar(consulta));
  }

  @Test
  public void unNombreDeCientoUnCaracteresSeRechaza() {
    Consulta consulta = consultaValida();
    consulta.setNombre("n".repeat(101));

    assertThrows(DatosInvalidos.class, () -> servicioConsulta.guardar(consulta));
  }

  @Test
  public void unCorreoDeCientoCincuentaCaracteresSeAcepta() {
    Consulta consulta = consultaValida();
    consulta.setEmail("a".repeat(144) + "@x.com");

    assertDoesNotThrow(() -> servicioConsulta.guardar(consulta));
  }

  @Test
  public void unCorreoDeCientoCincuentaYUnCaracteresSeRechaza() {
    Consulta consulta = consultaValida();
    consulta.setEmail("a".repeat(145) + "@x.com");

    assertThrows(DatosInvalidos.class, () -> servicioConsulta.guardar(consulta));
  }

  @Nested
  class CasosDeBordeDeConsultas {

    private ServicioConsulta servicio;

    @BeforeEach
    public void init() {
      servicio = new ServicioConsultaImpl(mock(RepositorioConsulta.class));
    }

    @Test
    public void unNombreDeCienCaracteresSeAcepta() {
      Consulta consulta = consultaValida();
      consulta.setNombre("n".repeat(100));

      assertDoesNotThrow(() -> servicio.guardar(consulta));
    }

    @Test
    public void unNombreDeCientoUnCaracteresSeRechaza() {
      Consulta consulta = consultaValida();
      consulta.setNombre("n".repeat(101));

      assertThrows(DatosInvalidos.class, () -> servicio.guardar(consulta));
    }

    @Test
    public void unCorreoDeCientoCincuentaCaracteresSeAcepta() {
      Consulta consulta = consultaValida();
      consulta.setEmail("a".repeat(144) + "@x.com");

      assertDoesNotThrow(() -> servicio.guardar(consulta));
    }

    @Test
    public void unCorreoDeCientoCincuentaYUnCaracteresSeRechaza() {
      Consulta consulta = consultaValida();
      consulta.setEmail("a".repeat(145) + "@x.com");

      assertThrows(DatosInvalidos.class, () -> servicio.guardar(consulta));
    }

    private Consulta consultaValida() {
      Consulta consulta = new Consulta();
      consulta.setNombre("Matías");
      consulta.setEmail("matias@unlam.com");
      consulta.setAsunto("Sugerencia");
      consulta.setMensaje("Me gustaría ver más comercios del barrio.");
      return consulta;
    }
  }
}
