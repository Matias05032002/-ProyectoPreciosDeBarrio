package com.tallerwebi.dominio.Consulta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
}
