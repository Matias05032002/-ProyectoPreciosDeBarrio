package com.tallerwebi.integracion.Lista;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import jakarta.transaction.Transactional;
import org.hibernate.SessionFactory;
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
public class ControladorListaTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @BeforeEach
  public void init() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  public void deberiaMostrarMisListasCon200() throws Exception {
    this.mockMvc.perform(get("/mis-listas").sessionAttr("ID", 1L)).andExpect(status().isOk());
  }

  @Test
  public void deberiaMostrarLaVistaMisListas() throws Exception {
    MvcResult result = this.mockMvc.perform(get("/mis-listas").sessionAttr("ID", 1L)).andReturn();
    ModelAndView mav = result.getModelAndView();
    assertThat(mav, is(notNullValue()));
    assertThat(mav.getViewName(), equalToIgnoringCase("lista/mis-listas"));
  }

  @Test
  public void deberiaTenerElAtributoListasEnElModelo() throws Exception {
    MvcResult result = this.mockMvc.perform(get("/mis-listas").sessionAttr("ID", 1L)).andReturn();
    ModelAndView mav = result.getModelAndView();
    assertThat(mav.getModel().containsKey("listas"), is(true));
  }

  @Test
  public void deberiaRedirigirSiNoHaySesionEnMisListas() throws Exception {
    this.mockMvc.perform(get("/mis-listas")).andExpect(status().is3xxRedirection());
  }

  @Test
  @Transactional
  public void deberiaCrearListaYRedirigirADetalle() throws Exception {
    this.mockMvc.perform(
        post("/mis-listas/crear").param("nombre", "Mi lista de prueba").sessionAttr("ID", 1L)
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrlPattern("/mis-listas/*"));
  }
}
