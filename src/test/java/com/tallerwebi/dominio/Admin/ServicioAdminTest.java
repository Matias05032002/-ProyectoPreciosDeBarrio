package com.tallerwebi.dominio.Admin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import com.tallerwebi.dominio.RepositorioUsuario;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioAdminTest {

  private ServicioAdmin servicioAdmin;
  private RepositorioReporte repositorioReporteMock;
  private RepositorioUsuario repositorioUsuarioMock;

  @BeforeEach
  public void init() {
    repositorioReporteMock = mock(RepositorioReporte.class);
    repositorioUsuarioMock = mock(RepositorioUsuario.class);
    servicioAdmin = new ServicioAdminImpl(repositorioReporteMock, repositorioUsuarioMock);
  }

  @Test
  public void totalUsuariosDevuelveElConteo() {
    when(repositorioUsuarioMock.contarTodos()).thenReturn(42L);
    assertThat(servicioAdmin.totalUsuarios(), equalTo(42L));
  }

  @Test
  public void totalReportesDevuelveElConteo() {
    when(repositorioReporteMock.contarTodos()).thenReturn(100L);
    assertThat(servicioAdmin.totalReportes(), equalTo(100L));
  }

  @Test
  public void productosMasReportadosDevuelveLista() {
    List<Object[]> datos = new ArrayList<>();
    datos.add(new Object[] { "Leche", 5L });
    datos.add(new Object[] { "Pan", 3L });
    when(repositorioReporteMock.productosMasReportados(10)).thenReturn(datos);
    assertThat(servicioAdmin.productosMasReportados(), hasSize(2));
  }

  @Test
  public void comerciosMasReportadosDevuelveLista() {
    List<Object[]> datos = new ArrayList<>();
    datos.add(new Object[] { "Almacen Don Juan", 8L });
    when(repositorioReporteMock.comerciosMasReportados(10)).thenReturn(datos);
    assertThat(servicioAdmin.comerciosMasReportados(), hasSize(1));
  }

  @Test
  public void reportesPorMesDevuelveLista() {
    List<Object[]> datos = new ArrayList<>();
    datos.add(new Object[] { 10, 2024, 15L });
    when(repositorioReporteMock.reportesPorMes()).thenReturn(datos);
    assertThat(servicioAdmin.reportesPorMes(), hasSize(1));
  }

  @Test
  public void productosMasDudososDevuelveLista() {
    List<Object[]> datos = new ArrayList<>();
    datos.add(new Object[] { "Aceite", 3 });
    when(repositorioReporteMock.productosMasDudosos(10)).thenReturn(datos);
    assertThat(servicioAdmin.productosMasDudosos(), hasSize(1));
  }

  @Test
  public void usuariosMasActivosDevuelveLista() {
    List<Object[]> datos = new ArrayList<>();
    datos.add(new Object[] { "Juan", "Perez", 10L });
    when(repositorioUsuarioMock.usuariosMasActivos(10)).thenReturn(datos);
    assertThat(servicioAdmin.usuariosMasActivos(), hasSize(1));
  }

  @Test
  public void distribucionPorRangoEtarioDevuelveLista() {
    List<Object[]> datos = new ArrayList<>();
    datos.add(new Object[] { "18-24", 5L });
    datos.add(new Object[] { "25-34", 12L });
    when(repositorioUsuarioMock.distribucionPorRangoEtario()).thenReturn(datos);
    assertThat(servicioAdmin.distribucionPorRangoEtario(), hasSize(2));
  }

  @Test
  public void productosMasReportadosDevuelveListaVaciaSiNoHayDatos() {
    when(repositorioReporteMock.productosMasReportados(10)).thenReturn(Collections.emptyList());
    assertThat(servicioAdmin.productosMasReportados(), empty());
  }
}
