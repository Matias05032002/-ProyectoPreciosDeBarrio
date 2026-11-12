package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.*;
import java.time.LocalDate;
import java.util.List;

public interface ServicioReporte {
  Reporte guardarReporte(Reporte reporte)
    throws ReporteExistente, CamposObligatoriosVacios, UnidadInvalida, PrecioIncorrecto;
  Reporte guardarOReemplazarReporteDeHoy(Reporte reporte)
    throws CamposObligatoriosVacios, UnidadInvalida, PrecioIncorrecto;
  Reporte buscarReporte(Long id);
  List<Reporte> buscarPorProducto(Long productoId);
  Reporte marcarDudoso(Long id, Usuario usuario);
  Reporte desmarcarDudoso(Long id, Usuario usuario);
  List<Reporte> listarTodos();
  Reporte buscarReporteDuplicado(Long usuarioId, Long productoId, Long comercioId, LocalDate fecha);
  List<Reporte> buscarPorNombre(String nombreProducto);
  List<Reporte> buscarPorComercio(Long comercioId);
  List<Reporte> buscarPorUsuarioId(Long usuarioId);
  void editarReporte(Long id, Reporte datos, Long usuarioId)
    throws CamposObligatoriosVacios, UnidadInvalida, PrecioIncorrecto, FueraDePlazoDeEdicion;
  boolean puedeEditar(Long reporteId, Long usuarioId);
  void eliminarReporte(Long id, Long usuarioId) throws AccesoDenegado;
  List<Object[]> contarUsuariosDistintosPorComercio(Long comercioId);
  Long contarReportesDudososPorComercio(Long comercioId);
  List<Object[]> rangoEtariosVisitantes(Long comercioId);
  List<Object[]> productosMasBuscadosPorLocalidad(String localidad, int limite);

  List<Reporte> ultimosReportesVisibles(int limite);

  List<ReporteConDistancia> ultimosReportesVisiblesMasCercanos(
    double latitud,
    double longitud,
    int limite
  );
  ResumenDeActividad resumenDeActividad();
  void validarFoto(byte[] foto) throws FotoInvalida;
  double precioPromedioDelComercio(Long comercioId);
  String productoEstrellaDelComercio(Long comercioId);
  List<Object[]> productosDudososPorComercio(Long comercioId);
}
