package com.tallerwebi.presentacion.Producto;

import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.presentacion.ControladorProducto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ControladorProductoTest {

    private ControladorProducto controladorProducto;
    private ServicioProducto servicioProductoMock;


    @BeforeEach
    public void init(){
        this.servicioProductoMock = mock(ServicioProducto.class);
        this.controladorProducto = new ControladorProducto(servicioProductoMock);
    }

@Test
    public void listarProductosDeberiaRetornarVistaConListaDeProductos(){
    Producto productos = new Producto();
    List<Producto> listaDeProductos = Arrays.asList(productos);
    when(this.servicioProductoMock.listarTodos()).thenReturn(listaDeProductos);

    ModelAndView modelAndView = controladorProducto.listarProductos();

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("lista-productos"));
    assertThat(modelAndView.getModel().get("productos"), is(listaDeProductos));

}

@Test
public void guardarUnNuevoProductoDeberiaRedirigirAProductos(){
        Producto producto = new Producto();
        when(this.servicioProductoMock.guardarProducto(producto)).thenReturn(producto);

        ModelAndView modelAndView = controladorProducto.guardarProducto(producto);

        assertThat(modelAndView.getViewName(),equalToIgnoringCase("redirect:/productos") );
        verify(this.servicioProductoMock, times(1)).guardarProducto(producto);
}
@Test
    public void buscarUnProductoPorNombre(){
        Producto producto = new Producto();
        List<Producto>lista = Arrays.asList(producto);
        producto.setNombre("Leche");
        when(this.servicioProductoMock.buscarPorNombre(producto.getNombre())).thenReturn(lista);

        ModelAndView modelAndView = controladorProducto.buscarProducto(producto.getNombre());

        assertThat(modelAndView.getViewName(), equalTo("productos"));
        assertThat(modelAndView.getModel().get("productos"), is(lista));
}

@Test
    public void buscarUnProductoQueNoExisteDevuelveListaVacia(){
        when(this.servicioProductoMock.buscarPorNombre("Producto Inexistente")).thenReturn(Collections.emptyList());

        ModelAndView modelAndView = this.controladorProducto.buscarProducto("Producto Inexistente");

    assertThat((List<?>)modelAndView.getModel().get("productos"), is(empty()));

}

}
