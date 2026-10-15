package com.tallerwebi.integracion.Reporte;

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
public class ControladorReporteTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @BeforeEach
  public void init() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  public void verificarQueGetReporteDevuelveDosCientosYlaVista() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/reporte").sessionAttr("ROL", "USER"))
        .andExpect(status().isOk())
        .andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("reporte/lista-reportes"));
  }

  @Test
  public void verificarQueGetReporteDevuelveElModeloConReportes() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/reporte").sessionAttr("ROL", "USER"))
        .andExpect(status().isOk())
        .andReturn();
    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getModel().containsKey("reportes"), is(true));
  }

  @Test
  public void verificarQueBuscarUnReporteDevuelveLaVistaReportes() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/reporte/buscar?nombre=Leche").sessionAttr("ROL", "USER"))
        .andExpect(status().isOk())
        .andReturn();
    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("reporte/lista-reportes"));
  }

  @Test
  public void cuandoSeGuardaUnReporteElModeloContieneGuardadoTrue() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/reporte").param("guardado", "true").sessionAttr("ROL", "USER"))
        .andExpect(status().isOk())
        .andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getModel().get("guardado"), is(true));
  }

  @Test
  public void verificarQueGetMisReportesDevuelveDosCientosYlaVista() throws Exception {
    MvcResult result =
      this.mockMvc.perform(
          get("/reporte/mis-reportes").sessionAttr("ROL", "USER").sessionAttr("ID", 1L)
        )
        .andExpect(status().isOk())
        .andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("reporte/mis-reportes-comercio"));
  }

  @Test
  public void verificarQueGetMisReportesDevuelveElModeloConReportes() throws Exception {
    MvcResult result =
      this.mockMvc.perform(
          get("/reporte/mis-reportes").sessionAttr("ROL", "USER").sessionAttr("ID", 1L)
        )
        .andExpect(status().isOk())
        .andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getModel().containsKey("reportes"), is(true));
  }

  @Test
  public void verificarQueGetMisProductosDevuelveDosCientosYlaVista() throws Exception {
    MvcResult result =
      this.mockMvc.perform(
          get("/reporte/mis-productos").sessionAttr("ROL", "COMERCIO").sessionAttr("ID", 1L)
        )
        .andExpect(status().isOk())
        .andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("reporte/mis-productos-comercio"));
  }

  @Test
  public void verificarQueGetMisProductosDevuelveElModeloConReportes() throws Exception {
    MvcResult result =
      this.mockMvc.perform(
          get("/reporte/mis-productos").sessionAttr("ROL", "COMERCIO").sessionAttr("ID", 1L)
        )
        .andExpect(status().isOk())
        .andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getModel().containsKey("reportes"), is(true));
  }

  @Test
  public void verificarQueGetTicketDevuelveDosCientosYlaVista() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/reporte/ticket").sessionAttr("ROL", "USER"))
        .andExpect(status().isOk())
        .andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("reporte/subir-ticket"));
  }

  @Test
  public void verificarQueGetTicketDevuelveElModeloConComercios() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/reporte/ticket").sessionAttr("ROL", "USER"))
        .andExpect(status().isOk())
        .andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getModel().containsKey("comercios"), is(true));
  }

  @Test
  public void verificarQueGetTicketSinSesionRedirige() throws Exception {
    MvcResult result = this.mockMvc.perform(get("/reporte/ticket")).andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void verificarQueGetEditarReporteSinSesionRedirige() throws Exception {
    MvcResult result = this.mockMvc.perform(get("/reporte/1/editar")).andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void verificarQueGetEditarReporteConSesionYReporteInexistenteRedirige() throws Exception {
    MvcResult result =
      this.mockMvc.perform(
          get("/reporte/9999/editar").sessionAttr("ROL", "USER").sessionAttr("ID", 1L)
        )
        .andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/reporte/mis-reportes"));
  }
}
