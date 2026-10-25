package com.tallerwebi.presentacion.Lista;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Lista.Lista;
import com.tallerwebi.dominio.Lista.ServicioLista;
import com.tallerwebi.presentacion.ControladorLista;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorListaTest {

  private ControladorLista controladorLista;
  private ServicioLista servicioListaMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioListaMock = mock(ServicioLista.class);
    sessionMock = mock(HttpSession.class);
    controladorLista = new ControladorLista(servicioListaMock);
  }

  // --- misListas ---

  @Test
  public void misListasSinSesionDeberiaRedirigirAlLogin() {
    when(sessionMock.getAttribute("ID")).thenReturn(null);

    ModelAndView mav = controladorLista.misListas(sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioListaMock);
  }

  @Test
  public void misListasConSesionDeberiaMostrarLasListasDelUsuario() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    Lista lista1 = new Lista("Lista compras", 1L);
    List<Lista> listas = Arrays.asList(lista1);
    when(servicioListaMock.obtenerListasDeUsuario(1L)).thenReturn(listas);

    ModelAndView mav = controladorLista.misListas(sessionMock);

    assertThat(mav.getViewName(), equalTo("lista/mis-listas"));
    @SuppressWarnings("unchecked")
    List<Lista> resultado = (List<Lista>) mav.getModel().get("listas");
    assertThat(resultado.size(), equalTo(1));
    verify(servicioListaMock, times(1)).obtenerListasDeUsuario(1L);
  }

  @Test
  public void misListasConSesionSinListasDeberiaRetornarListaVacia() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioListaMock.obtenerListasDeUsuario(1L)).thenReturn(Collections.emptyList());

    ModelAndView mav = controladorLista.misListas(sessionMock);

    assertThat(mav.getViewName(), equalTo("lista/mis-listas"));
    @SuppressWarnings("unchecked")
    List<Lista> resultado = (List<Lista>) mav.getModel().get("listas");
    assertThat(resultado.size(), equalTo(0));
  }

  // --- crearLista ---

  @Test
  public void crearListaSinSesionDeberiaRedirigirAlLogin() {
    when(sessionMock.getAttribute("ID")).thenReturn(null);

    ModelAndView mav = controladorLista.crearLista("Mi lista", sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioListaMock);
  }

  @Test
  public void crearListaConSesionDeberiaCrearYRedirigirAlDetalle() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    Lista listaCreada = new Lista("Mi lista del super", 1L);
    listaCreada.setId(7L);
    when(servicioListaMock.crearLista("Mi lista del super", 1L)).thenReturn(listaCreada);

    ModelAndView mav = controladorLista.crearLista("Mi lista del super", sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-listas/7"));
    verify(servicioListaMock, times(1)).crearLista("Mi lista del super", 1L);
  }

  // --- detalleLista ---

  @Test
  public void detalleListaSinSesionDeberiaRedirigirAlLogin() {
    when(sessionMock.getAttribute("ID")).thenReturn(null);

    ModelAndView mav = controladorLista.detalleLista(1L, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioListaMock);
  }

  @Test
  public void detalleListaConListaInexistenteDeberiaRedirigirAMisListas() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioListaMock.obtenerListaPorId(99L)).thenReturn(null);

    ModelAndView mav = controladorLista.detalleLista(99L, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-listas"));
  }

  @Test
  public void detalleListaDeOtroUsuarioDeberiaRedirigirAMisListas() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    Lista listaDeOtro = new Lista("Lista ajena", 2L);
    listaDeOtro.setId(5L);
    when(servicioListaMock.obtenerListaPorId(5L)).thenReturn(listaDeOtro);

    ModelAndView mav = controladorLista.detalleLista(5L, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-listas"));
  }

  @Test
  public void detalleListaPropiaMostrarElDetalle() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    Lista lista = new Lista("Mi lista", 1L);
    lista.setId(3L);
    when(servicioListaMock.obtenerListaPorId(3L)).thenReturn(lista);

    ModelAndView mav = controladorLista.detalleLista(3L, sessionMock);

    assertThat(mav.getViewName(), equalTo("lista/detalle-lista"));
    assertThat(mav.getModel().get("lista"), equalTo(lista));
  }

  // --- agregarProducto ---

  @Test
  public void agregarProductoSinSesionDeberiaRedirigirAlLogin() {
    when(sessionMock.getAttribute("ID")).thenReturn(null);

    ModelAndView mav = controladorLista.agregarProducto(1L, 5L, 2, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioListaMock);
  }

  @Test
  public void agregarProductoConSesionDeberiaAgregarYRedirigirAlDetalle() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);

    ModelAndView mav = controladorLista.agregarProducto(3L, 5L, 2, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-listas/3"));
    verify(servicioListaMock, times(1)).agregarProductoALista(3L, 5L, 2);
  }

  // --- eliminarItem ---

  @Test
  public void eliminarItemSinSesionDeberiaRedirigirAlLogin() {
    when(sessionMock.getAttribute("ID")).thenReturn(null);

    ModelAndView mav = controladorLista.eliminarItem(1L, 3L, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioListaMock);
  }

  @Test
  public void eliminarItemConSesionDeberiaEliminarYRedirigirAlDetalle() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);

    ModelAndView mav = controladorLista.eliminarItem(10L, 3L, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-listas/3"));
    verify(servicioListaMock, times(1)).eliminarItemDeLista(10L);
  }

  // --- eliminarLista ---

  @Test
  public void eliminarListaSinSesionDeberiaRedirigirAlLogin() {
    when(sessionMock.getAttribute("ID")).thenReturn(null);

    ModelAndView mav = controladorLista.eliminarLista(1L, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioListaMock);
  }

  @Test
  public void eliminarListaInexistenteDeberiaRedirigirAMisListas() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioListaMock.obtenerListaPorId(99L)).thenReturn(null);

    ModelAndView mav = controladorLista.eliminarLista(99L, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-listas"));
    verify(servicioListaMock, never()).eliminarLista(anyLong());
  }

  @Test
  public void eliminarListaDeOtroUsuarioDeberiaRedirigirAMisListas() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    Lista listaDeOtro = new Lista("Lista ajena", 2L);
    listaDeOtro.setId(5L);
    when(servicioListaMock.obtenerListaPorId(5L)).thenReturn(listaDeOtro);

    ModelAndView mav = controladorLista.eliminarLista(5L, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-listas"));
    verify(servicioListaMock, never()).eliminarLista(anyLong());
  }

  @Test
  public void eliminarListaPropiaSinProblemasDeberiaEliminarYRedirigir() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    Lista lista = new Lista("Mi lista", 1L);
    lista.setId(3L);
    when(servicioListaMock.obtenerListaPorId(3L)).thenReturn(lista);

    ModelAndView mav = controladorLista.eliminarLista(3L, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-listas"));
    verify(servicioListaMock, times(1)).eliminarLista(3L);
  }

  // --- editarItem ---

  @Test
  public void editarItemSinSesionDeberiaRedirigirAlLogin() {
    when(sessionMock.getAttribute("ID")).thenReturn(null);

    ModelAndView mav = controladorLista.editarItem(1L, 3L, 5, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioListaMock);
  }

  @Test
  public void editarItemConSesionDeberiaActualizarYRedirigirAlDetalle() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);

    ModelAndView mav = controladorLista.editarItem(10L, 3L, 5, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-listas/3"));
    verify(servicioListaMock, times(1)).editarCantidadItem(10L, 5);
  }
}
