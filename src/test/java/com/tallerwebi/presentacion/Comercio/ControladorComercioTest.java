package com.tallerwebi.presentacion.Comercio;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.presentacion.ControladorComercio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.mockito.Mockito.*;

public class ControladorComercioTest {

    private ControladorComercio controladorComercio;
    private ServicioComercio servicioComercioMock;

    @BeforeEach
    public void init(){
        this.servicioComercioMock = mock(ServicioComercio.class);
        this.controladorComercio = new ControladorComercio(servicioComercioMock);
    }

    @Test
    public void guardarUnNuevoComercio(){
        Comercio comercio = new Comercio();
        when(this.servicioComercioMock.guardarComercio(comercio)).thenReturn(comercio);

        ModelAndView modelAndView = this.controladorComercio.guardarComercio(comercio);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/comercios"));
        verify(this.servicioComercioMock, times(1)).guardarComercio(comercio);
    }

    @Test
    public void listarTodosLosComercios(){
        Comercio comercio = new Comercio();
        List<Comercio> listaDeComercios = Arrays.asList(comercio);
        when(this.servicioComercioMock.listarTodos()).thenReturn(listaDeComercios);

        ModelAndView modelAndView = this.controladorComercio.listarTodos();

        assertThat(modelAndView.getModel().get("comercios"), is(listaDeComercios));
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("lista-comercios"));
    }
@Test
    public void buscarComercioPorId(){
        Comercio comercio = new Comercio();
        when(this.servicioComercioMock.buscarComercio(1L)).thenReturn(comercio);

        ModelAndView modelAndView = this.controladorComercio.buscarPorId(1L);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("comercios"));
        assertThat(modelAndView.getModel().get("comercio"), is(comercio));
        verify(this.servicioComercioMock, times(1)).buscarComercio(1L);
}
@Test
public void buscarComercioPorNombre(){
    Comercio comercio = new Comercio();
    when(this.servicioComercioMock.buscarComercioPorNombre("Almacen de Matias")).thenReturn(comercio);

    ModelAndView modelAndView = this.controladorComercio.buscarComercioPorNombre("Almacen de Matias");

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("comercios"));
    assertThat(modelAndView.getModel().get("comercio"), is(comercio));
    verify(this.servicioComercioMock, times(1)).buscarComercioPorNombre("Almacen de Matias");
}

}
