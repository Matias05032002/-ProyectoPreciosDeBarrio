package com.tallerwebi.presentacion.Reporte;

import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.excepcion.ReporteExistente;
import com.tallerwebi.presentacion.ControladorReporte;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class ControladorReporteTest {

    private ServicioReporte servicioReporteMock;
    private ControladorReporte controladorReporte;
    private ServicioComercio servicioComercioMock;
    private ServicioProducto servicioProductoMock;
    private ServicioLogin servicioLoginMock;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;


    @BeforeEach
    public void init(){
        this.servicioReporteMock = mock(ServicioReporte.class);
        this.controladorReporte = new ControladorReporte(servicioReporteMock, servicioProductoMock, servicioComercioMock, servicioLoginMock);
        this.servicioProductoMock = mock(ServicioProducto.class);
        this.servicioComercioMock = mock(ServicioComercio.class);
        this.servicioLoginMock = mock(ServicioLogin.class);
        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getAttribute("EMAIL")).thenReturn("test@test.com");
    }

    @Test
    public void guardarNuevoReporte() throws ReporteExistente {
        Reporte reporte = new Reporte();
        when(this.servicioReporteMock.guardarReporte(reporte)).thenReturn(reporte);

        ModelAndView modelAndView = this.controladorReporte.guardarReporte(reporte, requestMock);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/reporte"));
        verify(this.servicioReporteMock, times(1)).guardarReporte(reporte);
    }

    @Test
    public void listarTodosLosReportes(){
        Reporte reporte = new Reporte();
        List<Reporte> listaDeReportes = Arrays.asList(reporte);
        when(sessionMock.getAttribute("ROL")).thenReturn("USER");
        when(this.servicioReporteMock.listarTodos()).thenReturn(listaDeReportes);

        ModelAndView modelAndView = this.controladorReporte.listarTodos(requestMock);

        assertThat(modelAndView.getModel().get("reportes"), is(listaDeReportes));
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("lista-reportes"));
    }
@Test
    public void buscarReportePorId(){
        Reporte reporte = new Reporte();
        when(this.servicioReporteMock.buscarReporte(1L)).thenReturn(reporte);

        ModelAndView modelAndView = this.controladorReporte.buscarReporte(1L);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("reportes"));
        assertThat(modelAndView.getModel().get("reportes"), is(reporte));
        verify(this.servicioReporteMock, times(1)).buscarReporte(1L);

}
    @Test
    public void buscarReportePorNombre(){
        Reporte reporte = new Reporte();
        List<Reporte> listaDeReportesPorNombre = Arrays.asList(reporte);
        when(sessionMock.getAttribute("ROL")).thenReturn("USER");
        when(this.servicioReporteMock.buscarPorNombre("Leche")).thenReturn(listaDeReportesPorNombre);

        ModelAndView modelAndView = this.controladorReporte.buscarPorNombre("Leche", requestMock);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("reportes"));
        assertThat(modelAndView.getModel().get("reportes"), is(listaDeReportesPorNombre));
        verify(this.servicioReporteMock, times(1)).buscarPorNombre("Leche");
    }
    @Test
    public void marcarDudosoUnReporte(){
        Reporte reporte = new Reporte();
        when(this.servicioReporteMock.marcarDudoso(1L)).thenReturn(reporte);

        ModelAndView modelAndView = this.controladorReporte.marcarDudoso(1L);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/reporte"));
        verify(this.servicioReporteMock, times(1)).marcarDudoso(1L);

    }

}
