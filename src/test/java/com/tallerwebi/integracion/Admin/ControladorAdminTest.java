package com.tallerwebi.integracion.Admin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
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
public class ControladorAdminTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @BeforeEach
  public void init() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  public void sinSesionDeberiaRedirigirAlHome() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/admin/estadisticas"))
        .andExpect(status().is3xxRedirection())
        .andReturn();

    ModelAndView mav = result.getModelAndView();
    assert mav != null;
    assertThat(Objects.requireNonNull(mav.getViewName()), equalTo("redirect:/home"));
  }

  @Test
  public void conRolUserDeberiaRedirigirAlHome() throws Exception {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("ROL", "USER");

    MvcResult result =
      this.mockMvc.perform(get("/admin/estadisticas").session(session))
        .andExpect(status().is3xxRedirection())
        .andReturn();

    ModelAndView mav = result.getModelAndView();
    assert mav != null;
    assertThat(Objects.requireNonNull(mav.getViewName()), equalTo("redirect:/home"));
  }

  @Test
  public void conRolAdminDeberiaMostrarEstadisticas() throws Exception {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("ROL", "ADMIN");

    MvcResult result =
      this.mockMvc.perform(get("/admin/estadisticas").session(session))
        .andExpect(status().isOk())
        .andReturn();

    ModelAndView mav = result.getModelAndView();
    assert mav != null;
    assertThat(Objects.requireNonNull(mav.getViewName()), equalTo("admin/estadisticas"));
  }
}
