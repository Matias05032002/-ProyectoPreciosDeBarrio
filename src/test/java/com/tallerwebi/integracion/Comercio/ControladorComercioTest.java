package com.tallerwebi.integracion.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SpringWebTestConfig.class, HibernateTestConfig.class })
public class ControladorComercioTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @BeforeEach
  public void init() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  public void verificarQueGetComercioDevuelveDosCientosYlaVista() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/comercio")).andExpect(status().isOk()).andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("comercio/lista-comercios"));
  }

  @Test
  public void verificarQueGetComercioDevuelveElModeloConComercios() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/comercio")).andExpect(status().isOk()).andReturn();
    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getModel().containsKey("comercios"), is(true));
  }

  @Test
  public void verificarQueBuscarUnComercioDevuelveLaVistaComercios() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/comercio/buscar?nombre=Almacen+de+Matias"))
        .andExpect(status().isOk())
        .andReturn();
    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("comercio/lista-comercios"));
  }

  @Test
  public void verificarQueGetIdDevuelveLavistaCorrecta() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/comercio/1")).andExpect(status().isOk()).andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("comercio/comercios"));
  }

  @Test
  public void verificarQueGetIdDevuelveElModeloConComercioYReportes() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/comercio/1")).andExpect(status().isOk()).andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getModel().containsKey("comercio"), is(true));
    assertThat(modelAndView.getModel().containsKey("reportes"), is(true));
  }

  @Test
  public void deberiaRedirigirALoginSiNoHaySesionEnGetPerfil() throws Exception {
    this.mockMvc.perform(get("/comercio/perfil")).andExpect(status().is3xxRedirection());
  }

  @Test
  public void deberiaMostrarPerfilCuandoHaySesion() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/comercio/perfil").sessionAttr("ID", 1L))
        .andExpect(status().isOk())
        .andReturn();
    ModelAndView mav = result.getModelAndView();
    assertThat(mav.getModel().containsKey("comercio"), is(true));
  }

  @Test
  public void deberiaRedirigirALoginSiNoHaySesionEnPostPerfil() throws Exception {
    this.mockMvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
          .post("/comercio/perfil")
          .param("nombre", "Test")
          .param("direccion", "Av Test 123")
          .param("localidad", "La Matanza")
      )
      .andExpect(status().is3xxRedirection());
  }
}
