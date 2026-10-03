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
}
