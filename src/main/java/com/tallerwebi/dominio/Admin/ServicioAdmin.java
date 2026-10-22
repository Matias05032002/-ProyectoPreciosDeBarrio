package com.tallerwebi.dominio.Admin;

import java.util.List;

public interface ServicioAdmin {
  Long totalUsuarios();
  Long totalReportes();
  List<Object[]> productosMasReportados();
  List<Object[]> comerciosMasReportados();
  List<Object[]> reportesPorMes();
  List<Object[]> productosMasDudosos();
  List<Object[]> usuariosMasActivos();
  List<Object[]> distribucionPorRangoEtario();
}
