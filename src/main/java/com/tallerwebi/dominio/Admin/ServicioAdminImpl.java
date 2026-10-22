package com.tallerwebi.dominio.Admin;

import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import com.tallerwebi.dominio.RepositorioUsuario;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioAdminImpl implements ServicioAdmin {

  private final RepositorioReporte repositorioReporte;
  private final RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioAdminImpl(
    RepositorioReporte repositorioReporte,
    RepositorioUsuario repositorioUsuario
  ) {
    this.repositorioReporte = repositorioReporte;
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public Long totalUsuarios() {
    return repositorioUsuario.contarTodos();
  }

  @Override
  public Long totalReportes() {
    return repositorioReporte.contarTodos();
  }

  @Override
  public List<Object[]> productosMasReportados() {
    return repositorioReporte.productosMasReportados(10);
  }

  @Override
  public List<Object[]> comerciosMasReportados() {
    return repositorioReporte.comerciosMasReportados(10);
  }

  @Override
  public List<Object[]> reportesPorMes() {
    return repositorioReporte.reportesPorMes();
  }

  @Override
  public List<Object[]> productosMasDudosos() {
    return repositorioReporte.productosMasDudosos(10);
  }

  @Override
  public List<Object[]> usuariosMasActivos() {
    return repositorioUsuario.usuariosMasActivos(10);
  }

  @Override
  public List<Object[]> distribucionPorRangoEtario() {
    return repositorioUsuario.distribucionPorRangoEtario();
  }
}
