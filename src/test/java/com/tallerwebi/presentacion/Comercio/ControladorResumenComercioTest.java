package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.ResumenComercio;
import com.tallerwebi.dominio.Comercio.ServicioResumenComercio;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class ControladorResumenComercioTest {

  private ServicioResumenComercio servicioMock;
  private ControladorResumenComercio controlador;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioMock = mock(ServicioResumenComercio.class);
    controlador = new ControladorResumenComercio(servicioMock);
    sessionMock = mock(HttpSession.class);
  }

  @Test
  public void resumenParaUnVecinoDevuelve403SinConsultarAlServicio() {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(sessionMock.getAttribute("ID")).thenReturn(1L);

    ResponseEntity<ResumenComercio> respuesta = controlador.resumen(sessionMock);

    assertThat(respuesta.getStatusCode().value(), equalTo(403));
    verifyNoInteractions(servicioMock);
  }

  @Test
  public void resumenSinSesionDevuelve403() {
    ResponseEntity<ResumenComercio> respuesta = controlador.resumen(sessionMock);

    assertThat(respuesta.getStatusCode().value(), equalTo(403));
  }

  @Test
  public void resumenDeUnComercioSinDatosDevuelve404() {
    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");
    when(sessionMock.getAttribute("ID")).thenReturn(3L);
    when(servicioMock.obtenerResumen(3L)).thenReturn(null);

    assertThat(controlador.resumen(sessionMock).getStatusCode().value(), equalTo(404));
  }

  @Test
  public void resumenDelComercioUsaElUsuarioDeLaSesion() {
    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");
    when(sessionMock.getAttribute("ID")).thenReturn(3L);
    ResumenComercio resumen = new ResumenComercio(
      "Almacen",
      true,
      false,
      4,
      2,
      1,
      80,
      Collections.emptyList(),
      Collections.emptyList()
    );
    when(servicioMock.obtenerResumen(3L)).thenReturn(resumen);

    ResponseEntity<ResumenComercio> respuesta = controlador.resumen(sessionMock);

    assertThat(respuesta.getStatusCode().value(), equalTo(200));
    assertThat(respuesta.getBody(), sameInstance(resumen));
  }
}
