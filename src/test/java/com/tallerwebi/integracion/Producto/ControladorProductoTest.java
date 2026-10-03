package com.tallerwebi.integracion.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import jakarta.transaction.Transactional;
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
public class ControladorProductoTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @BeforeEach
  public void init() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  public void verificarQueGetProductoDevuelveDosCientosYlaVista() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/producto")).andExpect(status().isOk()).andReturn();

    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("producto/lista-productos"));
  }

  @Test
  public void verificarQueBuscarUnProductoDevuelveLaVistaProductos() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/producto/buscar?nombre=Leche"))
        .andExpect(status().isOk())
        .andReturn();
    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("producto/lista-productos"));
  }

  @Test
  public void verificarQueGetProductoDevuelveElModeloConProductos() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/producto")).andExpect(status().isOk()).andReturn();
    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getModel().containsKey("productos"), is(true));
  }

  @Autowired
  private org.hibernate.SessionFactory sessionFactory;

  @Test
  @Transactional
  public void cuandoSeBuscaUnProductoMuestraLaAntiguedadCorrecta() throws Exception {
    org.hibernate.Session session = sessionFactory.getCurrentSession();

    Producto producto = new Producto();
    producto.setNombre("Leche");
    producto.setMarca("La Serenisima");
    producto.setUnidad("1000ml");
    producto.setCategoria("Lacteos");
    session.save(producto);

    Comercio comercio = new Comercio();
    comercio.setNombre("Almacen Central");
    comercio.setDireccion("Av. Siempreviva 123");
    comercio.setLocalidad("La Matanza");
    session.save(comercio);

    Reporte reporte = new Reporte();
    reporte.setProducto(producto);
    reporte.setComercio(comercio);
    reporte.setPrecio(150.0);
    reporte.setFechaDeReporte(java.time.LocalDateTime.now().minusDays(3));
    session.save(reporte);

    MvcResult result =
      this.mockMvc.perform(get("/producto/buscar?nombre=Leche"))
        .andExpect(status().isOk())
        .andReturn();

    String html = result.getResponse().getContentAsString();
    assertThat(html.contains("Fresco"), is(true));
  }
}
