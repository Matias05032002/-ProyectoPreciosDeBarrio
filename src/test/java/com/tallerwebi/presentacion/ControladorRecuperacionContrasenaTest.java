package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Recuperacion.ServicioRecuperacionContrasena;
import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.TokenDeRecuperacionInvalido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

public class ControladorRecuperacionContrasenaTest {

  private ServicioRecuperacionContrasena servicioMock;
  private ControladorRecuperacionContrasena controlador;
  private RedirectAttributesModelMap redirectAttributes;

  @BeforeEach
  public void init() {
    servicioMock = mock(ServicioRecuperacionContrasena.class);
    controlador = new ControladorRecuperacionContrasena(servicioMock);
    redirectAttributes = new RedirectAttributesModelMap();
  }

  @Test
  public void olvideContrasenaMuestraElFormulario() {
    assertThat(controlador.olvideContrasena().getViewName(), equalTo("olvide-contrasena"));
  }

  @Test
  public void pedirElLinkLlamaAlServicioYMuestraSiempreElMismoMensaje() {
    ModelAndView mav = controlador.solicitarRecuperacion("vecina@test.com", redirectAttributes);

    verify(servicioMock).solicitarRecuperacion("vecina@test.com");
    assertThat(mav.getViewName(), equalTo("redirect:/olvide-contrasena"));
    String mensaje = (String) redirectAttributes.getFlashAttributes().get("pedidoEnviado");
    assertThat(mensaje.startsWith("Si el email corresponde a una cuenta"), equalTo(true));
  }

  @Test
  public void elMensajeEsElMismoAunqueElEmailNoExista() {
    controlador.solicitarRecuperacion("vecina@test.com", redirectAttributes);
    Object mensajeExistente = redirectAttributes.getFlashAttributes().get("pedidoEnviado");
    RedirectAttributesModelMap otros = new RedirectAttributesModelMap();

    controlador.solicitarRecuperacion("nadie@test.com", otros);

    assertThat(otros.getFlashAttributes().get("pedidoEnviado"), equalTo(mensajeExistente));
  }

  @Test
  public void abrirElLinkConTokenValidoMuestraElFormularioConElToken() {
    when(servicioMock.esTokenValido("abc")).thenReturn(true);

    ModelAndView mav = controlador.formularioRestablecer("abc");

    assertThat(mav.getViewName(), equalTo("restablecer-contrasena"));
    assertThat(mav.getModel().get("token"), equalTo("abc"));
    assertThat(mav.getModel().get("tokenInvalido"), nullValue());
  }

  @Test
  public void abrirElLinkConTokenInvalidoAvisaQueNoSirve() {
    when(servicioMock.esTokenValido("viejo")).thenReturn(false);

    ModelAndView mav = controlador.formularioRestablecer("viejo");

    assertThat(mav.getModel().get("tokenInvalido"), equalTo(true));
    assertThat(mav.getModel().get("token"), nullValue());
  }

  @Test
  public void restablecerConExitoRedirigeAlLoginConAviso() throws ContrasenaInvalida {
    ModelAndView mav = controlador.restablecer(
      "abc",
      "NuevaClave1",
      "NuevaClave1",
      redirectAttributes
    );

    verify(servicioMock).restablecerContrasena("abc", "NuevaClave1", "NuevaClave1");
    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    assertThat(
      redirectAttributes.getFlashAttributes().get("contrasenaRestablecida"),
      equalTo("Tu contraseña se cambió. Ya podés ingresar con la nueva.")
    );
  }

  @Test
  public void restablecerConContrasenaInvalidaVuelveAlFormularioConElErrorYElToken()
    throws ContrasenaInvalida {
    doThrow(new ContrasenaInvalida("Las contraseñas no coinciden"))
      .when(servicioMock)
      .restablecerContrasena("abc", "NuevaClave1", "Otra1");

    ModelAndView mav = controlador.restablecer("abc", "NuevaClave1", "Otra1", redirectAttributes);

    assertThat(mav.getViewName(), equalTo("restablecer-contrasena"));
    assertThat(mav.getModel().get("error"), equalTo("Las contraseñas no coinciden"));
    assertThat(mav.getModel().get("token"), equalTo("abc"));
  }

  @Test
  public void restablecerConTokenVencidoMuestraQueElLinkNoSirve() throws ContrasenaInvalida {
    doThrow(new TokenDeRecuperacionInvalido())
      .when(servicioMock)
      .restablecerContrasena("viejo", "NuevaClave1", "NuevaClave1");

    ModelAndView mav = controlador.restablecer(
      "viejo",
      "NuevaClave1",
      "NuevaClave1",
      redirectAttributes
    );

    assertThat(mav.getViewName(), equalTo("restablecer-contrasena"));
    assertThat(mav.getModel().get("tokenInvalido"), equalTo(true));
  }
}
