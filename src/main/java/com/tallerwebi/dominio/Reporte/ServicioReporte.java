package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.excepcion.ReporteExistente;
import java.time.LocalDate;
import java.util.List;

public interface ServicioReporte {
  Reporte guardarReporte(Reporte reporte) throws ReporteExistente;
  Reporte buscarReporte(Long id);
  List<Reporte> buscarPorPorducto(Long productoId);
  Reporte marcarDudoso(Long id);
  List<Reporte> listarTodos();
  Reporte buscarReporteDuplicado(Long usuarioId, Long productoId, Long comercioId, LocalDate fecha);
  List<Reporte> buscarPorNombre(String nombreProducto);
}
