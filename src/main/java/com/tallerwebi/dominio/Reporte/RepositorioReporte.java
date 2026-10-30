package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Usuario;
import java.time.LocalDate;
import java.util.List;

public interface RepositorioReporte {
  Reporte guardarReporte(Reporte reporte);
  Reporte buscarReporte(Long id);
  List<Reporte> buscarPorProducto(Long productoId);
  List<Reporte> buscarPorNombre(String nombreProducto);
  Reporte marcarDudoso(Long id, Usuario usuario);
  List<Reporte> listarTodos();
  Reporte buscarReporteDuplicado(Long usuarioId, Long productoId, Long comercioId, LocalDate fecha);
  List<Reporte> buscarPorComercio(Long comercioId);
  List<Reporte> buscarPorUsuarioId(Long usuarioId);
  Long contarTodos();
  List<Object[]> productosMasReportados(int limite);
  List<Object[]> comerciosMasReportados(int limite);
  List<Object[]> reportesPorMes();
  List<Object[]> productosMasDudosos(int limite);
  List<Object[]> contarUsuariosDistintosPorComercio(Long comercioId);
  Long contarReportesDudososPorComercio(Long comercioId);
  List<Object[]> rangoEtariosVisitantes(Long comercioId);
  List<Object[]> productosMasBuscadosPorLocalidad(String localidad, int limite);
}
