package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ReporteExistente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServicioReporteTest {
    private ServicioReporte servicioReporte;
    private ServicioComercio servicioComercioMock;
    private ServicioProducto servicioProductoMock;
    private RepositorioReporte repositorioReporteMock;


    @BeforeEach
    public void init(){
        this.repositorioReporteMock = mock(RepositorioReporte.class);
        this.servicioComercioMock = mock(ServicioComercio.class);
        this.servicioProductoMock = mock(ServicioProducto.class);
        this.servicioReporte = new ServicioReporteImpl(this.servicioComercioMock, this.servicioProductoMock, this.repositorioReporteMock);
    }

    @Test
    public void guardarReporteNuevo() throws ReporteExistente {
        Producto producto = new Producto();
        producto.setNombre("Leche");
        Comercio comercio = new Comercio();
        comercio.setNombre("Almacen de matias");
        Reporte reporte = new Reporte();
        reporte.setProducto(producto);
        reporte.setComercio(comercio);
        reporte.setPrecio(100.0);

        when(this.servicioComercioMock.buscarComercioPorNombre("Almacen de matias")).thenReturn(comercio);
        when(this.servicioProductoMock.buscarProductoPorNombreExacto("Leche")).thenReturn(producto);
        when(this.repositorioReporteMock.guardarReporte(reporte)).thenReturn(reporte);

        Reporte reporteResultado = this.servicioReporte.guardarReporte(reporte);


        assertThat(reporteResultado, equalTo(reporte));
        verify(this.repositorioReporteMock, times(1)).guardarReporte(reporte);
    }
    @Test
    public void buscarReportePorId(){
        Reporte reporte = new Reporte();
        when(this.repositorioReporteMock.buscarReporte(1L)).thenReturn(reporte);

        Reporte reporteBuscado = this.servicioReporte.buscarReporte(1L);

        assertThat(reporteBuscado, equalTo(reporte));
        verify(this.repositorioReporteMock, times(1)).buscarReporte(1L);

    }
    @Test
    public void buscarReportePorProducto(){
        Producto producto = new Producto();
        Reporte reporte = new Reporte();
        List<Reporte> listaDeReportes = Arrays.asList(reporte);
        when(this.repositorioReporteMock.buscarPorPorducto(producto.getId())).thenReturn(listaDeReportes);

        List<Reporte> listaDeReporteBuscado = this.servicioReporte.buscarPorPorducto(producto.getId());

        assertThat(listaDeReporteBuscado, equalTo(listaDeReportes));
        verify(this.repositorioReporteMock, times(1)).buscarPorPorducto(producto.getId());

    }
    @Test
    public void marcarUnReporteDudoso(){
        Reporte reporte = new Reporte();
        when(this.repositorioReporteMock.marcarDudoso(1L)).thenReturn(reporte);

        Reporte reporteMarcado = this.servicioReporte.marcarDudoso(1L);

        assertThat(reporteMarcado, equalTo(reporte));
        verify(this.repositorioReporteMock, times(1)).marcarDudoso(1L);

    }
    @Test
    public void listarTodosLosReportes(){
        Reporte reporte = new Reporte();
        List<Reporte> listaDeReportes = Arrays.asList(reporte);
        when(this.repositorioReporteMock.listarTodos()).thenReturn(listaDeReportes);

        List<Reporte> listaDeReportesBuscados = this.servicioReporte.listarTodos();

        assertThat(listaDeReportesBuscados, equalTo(listaDeReportes));
        verify(this.repositorioReporteMock, times(1)).listarTodos();

    }
    @Test
    public void buscarReporteDuplicado(){
        Reporte reporte = new Reporte();
        reporte.setFechaDeReporte(LocalDateTime.now());
        when(this.repositorioReporteMock.buscarReporteDuplicado(1L,1L,1L,reporte.getFechaDeReporte().toLocalDate())).thenReturn(reporte);

        Reporte reporteDuplicado = this.servicioReporte.buscarReporteDuplicado(1L,1L,1L,reporte.getFechaDeReporte().toLocalDate());

        assertThat(reporteDuplicado, equalTo(reporte));
        verify(this.repositorioReporteMock, times(1)).buscarReporteDuplicado(1L,1L,1L,reporte.getFechaDeReporte().toLocalDate());

    }
    @Test
    public void guardarReporteDuplicadoDeberiaLanzarExcepcion() throws ReporteExistente {
        Producto producto = new Producto();
        producto.setNombre("Leche");
        producto.setId(1L);
        Comercio comercio = new Comercio();
        comercio.setNombre("Almacen");
        comercio.setId(1L);
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Reporte reporte = new Reporte();
        reporte.setProducto(producto);
        reporte.setComercio(comercio);
        reporte.setUsuario(usuario);

        when(this.servicioProductoMock.buscarProductoPorNombreExacto("Leche")).thenReturn(producto);
        when(this.servicioComercioMock.buscarComercioPorNombre("Almacen")).thenReturn(comercio);
        when(this.repositorioReporteMock.buscarReporteDuplicado(eq(1L), eq(1L), eq(1L), any(LocalDate.class)))
                .thenReturn(reporte);

        assertThrows(ReporteExistente.class, () -> this.servicioReporte.guardarReporte(reporte));
        verify(this.repositorioReporteMock, times(0)).guardarReporte(reporte);
    }
    @Test
    public void buscarReportesPorNombre() {
        Reporte reporte = new Reporte();
        List<Reporte> reportes = Arrays.asList(reporte);
        when(this.repositorioReporteMock.buscarPorNombre("leche")).thenReturn(reportes);

        List<Reporte> resultado = this.servicioReporte.buscarPorNombre("leche");

        assertThat(resultado, equalTo(reportes));
        verify(this.repositorioReporteMock, times(1)).buscarPorNombre("leche");
    }
}
