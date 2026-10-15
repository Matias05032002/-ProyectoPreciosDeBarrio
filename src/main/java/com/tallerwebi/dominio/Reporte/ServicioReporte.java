package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.*;
import java.time.LocalDate;
import java.util.List;

public interface ServicioReporte {
  Reporte guardarReporte(Reporte reporte)
    throws ReporteExistente, CamposObligatoriosVacios, UnidadInvalida, PrecioIncorrecto;
  Reporte buscarReporte(Long id);
  List<Reporte> buscarPorProducto(Long productoId);
  Reporte marcarDudoso(Long id, Usuario usuario);
  List<Reporte> listarTodos();
  Reporte buscarReporteDuplicado(Long usuarioId, Long productoId, Long comercioId, LocalDate fecha);
  List<Reporte> buscarPorNombre(String nombreProducto);
  List<Reporte> buscarPorComercio(Long comercioId);
  List<Reporte> buscarPorUsuarioId(Long usuarioId);
  void editarReporte(Long id, Reporte datos, Long usuarioId)
    throws CamposObligatoriosVacios, UnidadInvalida, PrecioIncorrecto, FueraDePlazoDeEdicion;
  boolean puedeEditar(Long reporteId, Long usuarioId);
}
