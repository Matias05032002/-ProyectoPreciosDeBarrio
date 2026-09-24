package com.tallerwebi.dominio.Reporte;

import java.time.LocalDate;
import java.util.List;

public interface RepositorioReporte {
  Reporte guardarReporte(Reporte reporte);
  Reporte buscarReporte(Long id);
  List<Reporte> buscarPorPorducto(Long productoId);
  List<Reporte> buscarPorNombre(String nombreProducto);
  Reporte marcarDudoso(Long id);
  List<Reporte> listarTodos();
  Reporte buscarReporteDuplicado(Long usuarioId, Long productoId, Long comercioId, LocalDate fecha);
}
