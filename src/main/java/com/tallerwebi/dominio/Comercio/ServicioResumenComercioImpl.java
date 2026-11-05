package com.tallerwebi.dominio.Comercio;

import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioResumenComercioImpl implements ServicioResumenComercio {

  private static final int MAX_ULTIMOS_PRECIOS = 5;
  private static final int PORCENTAJE_TOTAL = 100;

  private final ServicioComercio servicioComercio;
  private final ServicioReporte servicioReporte;
  private final ServicioPromocion servicioPromocion;

  @Autowired
  public ServicioResumenComercioImpl(
    ServicioComercio servicioComercio,
    ServicioReporte servicioReporte,
    ServicioPromocion servicioPromocion
  ) {
    this.servicioComercio = servicioComercio;
    this.servicioReporte = servicioReporte;
    this.servicioPromocion = servicioPromocion;
  }

  @Override
  public ResumenComercio obtenerResumen(Long usuarioId) {
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
    if (comercio == null) {
      return null;
    }
    List<Reporte> reportes = sinNulos(servicioReporte.buscarPorComercio(comercio.getId()));
    int vecinos = sinNulos(servicioReporte.contarUsuariosDistintosPorComercio(comercio.getId()))
      .size();
    long promocionesActivas = sinNulos(servicioPromocion.listarPorComercio(comercio.getId()))
      .stream()
      .filter(this::estaVigente)
      .count();
    List<ResumenComercio.PasoPerfil> pasos = armarPasos(comercio);
    long completos = pasos.stream().filter(ResumenComercio.PasoPerfil::completo).count();

    return new ResumenComercio(
      comercio.getNombre(),
      Boolean.TRUE.equals(comercio.getVerificado()),
      Boolean.TRUE.equals(comercio.getSolicitudVerificacion()),
      reportes.size(),
      vecinos,
      (int) promocionesActivas,
      (int) ((completos * PORCENTAJE_TOTAL) / pasos.size()),
      pasos,
      ultimosPrecios(reportes)
    );
  }

  private List<ResumenComercio.PasoPerfil> armarPasos(Comercio comercio) {
    List<HorarioComercio> horarios = sinNulos(servicioComercio.obtenerHorarios(comercio.getId()));
    List<ResumenComercio.PasoPerfil> pasos = new ArrayList<>();
    pasos.add(
      new ResumenComercio.PasoPerfil(
        "foto",
        "Foto de tu local",
        comercio.getFoto() != null && comercio.getFoto().length > 0
      )
    );
    pasos.add(
      new ResumenComercio.PasoPerfil(
        "descripcion",
        "Descripción del comercio",
        comercio.getDescripcion() != null && !comercio.getDescripcion().isBlank()
      )
    );
    pasos.add(
      new ResumenComercio.PasoPerfil("horarios", "Horarios de atención", !horarios.isEmpty())
    );
    pasos.add(
      new ResumenComercio.PasoPerfil(
        "ubicacion",
        "Ubicación en el mapa",
        comercio.getLatitud() != null && comercio.getLongitud() != null
      )
    );
    pasos.add(
      new ResumenComercio.PasoPerfil(
        "verificacion",
        "Comercio verificado",
        Boolean.TRUE.equals(comercio.getVerificado())
      )
    );
    return pasos;
  }

  private List<ResumenComercio.UltimoPrecio> ultimosPrecios(List<Reporte> reportes) {
    return reportes
      .stream()
      .filter(r -> r.getProducto() != null && r.getPrecio() != null)
      .sorted(
        Comparator.comparing(
          Reporte::getFechaDeReporte,
          Comparator.nullsLast(Comparator.reverseOrder())
        )
      )
      .limit(MAX_ULTIMOS_PRECIOS)
      .map(r ->
        new ResumenComercio.UltimoPrecio(
          r.getProducto().getNombre(),
          r.getPrecio(),
          r.getFechaDeReporte() == null ? null : r.getFechaDeReporte().toString()
        )
      )
      .toList();
  }

  // Una promoción cuenta solo si está activa y la fecha de hoy cae dentro de su vigencia
  private boolean estaVigente(Promocion promocion) {
    LocalDateTime ahora = LocalDateTime.now();
    return (
      Boolean.TRUE.equals(promocion.getActiva()) &&
      (promocion.getFechaInicio() == null || !ahora.isBefore(promocion.getFechaInicio())) &&
      (promocion.getFechaFin() == null || !ahora.isAfter(promocion.getFechaFin()))
    );
  }

  private <T> List<T> sinNulos(List<T> lista) {
    return lista == null ? Collections.emptyList() : lista;
  }
}
