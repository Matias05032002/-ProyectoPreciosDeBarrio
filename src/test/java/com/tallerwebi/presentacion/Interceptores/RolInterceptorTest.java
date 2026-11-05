package com.tallerwebi.presentacion.Interceptores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RolInterceptorTest {

  private RolInterceptor interceptor;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  void preparar() {
    interceptor = new RolInterceptor();
    request = new MockHttpServletRequest();
    request.setContextPath("/spring");
    response = new MockHttpServletResponse();
  }

  private boolean entrar(String ruta, String rol) throws Exception {
    preparar();
    request.setRequestURI("/spring" + ruta);
    if (rol != null) {
      request.getSession().setAttribute("ROL", rol);
    }
    return interceptor.preHandle(request, response, new Object());
  }

  @Test
  void elAdminPuedeEntrarALasRutasDeAdmin() throws Exception {
    assertTrue(entrar("/admin/estadisticas", "ADMIN"));
  }

  @Test
  void unVecinoNoPuedeEntrarALasRutasDeAdmin() throws Exception {
    assertFalse(entrar("/admin/consultas", "USER"));
    assertEquals("/spring/home", response.getRedirectedUrl());
  }

  @Test
  void unComercioNoPuedeAprobarVerificaciones() throws Exception {
    assertFalse(entrar("/admin/verificacion/3/aprobar", "COMERCIO"));
  }

  @Test
  void sinRolNoPuedeEntrarALasRutasDeAdmin() throws Exception {
    assertFalse(entrar("/admin/estadisticas", null));
  }

  @Test
  void elComercioPuedeEntrarASusRutas() throws Exception {
    assertTrue(entrar("/promociones/mis-promociones", "COMERCIO"));
    assertTrue(entrar("/comercio/dashboard", "COMERCIO"));
    assertTrue(entrar("/comercio/perfil/horarios", "COMERCIO"));
    assertTrue(entrar("/inicio-comercio/resumen", "COMERCIO"));
  }

  @Test
  void unVecinoNoPuedeCrearNiDesactivarPromociones() throws Exception {
    assertFalse(entrar("/promociones/crear/producto", "USER"));
    assertFalse(entrar("/promociones/7/desactivar", "USER"));
  }

  @Test
  void unVecinoNoEntraAlPerfilDeComercio() throws Exception {
    assertFalse(entrar("/comercio/perfil", "USER"));
    assertFalse(entrar("/comercio/perfil/editar", "USER"));
  }

  @Test
  void lasRutasJsonDelComercioResponden403() throws Exception {
    assertFalse(entrar("/inicio-comercio/resumen", "USER"));
    assertEquals(403, response.getStatus());
  }

  @Test
  void lasRutasPublicasParaVecinosQuedanLibres() throws Exception {
    assertTrue(entrar("/comercio", "USER"));
    assertTrue(entrar("/comercio/5", "USER"));
    assertTrue(entrar("/comercio/mapa", "USER"));
    assertTrue(entrar("/promociones", "USER"));
    assertTrue(entrar("/mis-listas", "USER"));
    assertTrue(entrar("/asistente/consultar", "USER"));
  }

  @Test
  void noSePuedeEsquivarLaReglaConBarrasNiPuntosNiParametrosDeRuta() throws Exception {
    assertFalse(entrar("/admin//estadisticas", "USER"));
    assertFalse(entrar("/comercio/../admin/estadisticas", "USER"));
    assertFalse(entrar("/admin/estadisticas;jsessionid=abc", "USER"));
  }
}
