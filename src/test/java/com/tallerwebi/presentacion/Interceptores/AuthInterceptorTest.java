package com.tallerwebi.presentacion.Interceptores;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

public class AuthInterceptorTest {

  private AuthInterceptor interceptor;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  public void init() {
    interceptor = new AuthInterceptor();
    request = new MockHttpServletRequest();
    request.setContextPath("/spring");
    response = new MockHttpServletResponse();
  }

  @Test
  public void sinSesionDeberiaRedirigirAlLoginYCortarElPedido() throws Exception {
    boolean sigue = interceptor.preHandle(request, response, new Object());

    assertThat(sigue, is(false));
    assertThat(response.getRedirectedUrl(), equalTo("/spring/login"));
  }

  @Test
  public void conSesionSinEmailDeberiaRedirigirAlLogin() throws Exception {
    request.setSession(new MockHttpSession());

    boolean sigue = interceptor.preHandle(request, response, new Object());

    assertThat(sigue, is(false));
    assertThat(response.getRedirectedUrl(), equalTo("/spring/login"));
  }

  @Test
  public void conSesionConEmailDeberiaDejarPasar() throws Exception {
    MockHttpSession sesion = new MockHttpSession();
    sesion.setAttribute("EMAIL", "vecino@unlam.edu.ar");
    request.setSession(sesion);

    boolean sigue = interceptor.preHandle(request, response, new Object());

    assertThat(sigue, is(true));
    assertThat(response.getRedirectedUrl(), equalTo(null));
  }
}
