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
  List<Object[]> contarUsuariosDistintosPorComercio(Long comercioId);
  Long contarReportesDudososPorComercio(Long comercioId);
  List<Object[]> rangoEtariosVisitantes(Long comercioId);
  List<Object[]> productosMasBuscadosPorLocalidad(String localidad, int limite);

  /** Los últimos precios reportados, del más nuevo al más viejo, sin los marcados como dudosos. */
  List<Reporte> ultimosReportesVisibles(int limite);

  /**
   * De los últimos precios reportados, los de los comercios más cercanos a la ubicación dada,
   * del más cerca al más lejos. Deja afuera dudosos y comercios sin ubicación.
   */
  List<ReporteConDistancia> ultimosReportesVisiblesMasCercanos(
    double latitud,
    double longitud,
    int limite
  );

  /** Reportes de los últimos 7 días y comercios con precios confiables. */
  ResumenDeActividad resumenDeActividad();

  /** Revisa por su contenido real que sea una imagen JPG, PNG o WebP de hasta 10 MB. */
  void validarFoto(byte[] foto) throws FotoInvalida;

  double precioPromedioDelComercio(Long comercioId);

  /** Nombre del producto más reportado en el comercio, o "-" si todavía no hay reportes. */
  String productoEstrellaDelComercio(Long comercioId);
}
