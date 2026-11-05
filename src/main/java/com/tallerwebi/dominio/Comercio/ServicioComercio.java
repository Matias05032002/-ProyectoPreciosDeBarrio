package com.tallerwebi.dominio.Comercio;

import com.tallerwebi.dominio.excepcion.FotoInvalida;
import java.util.List;

public interface ServicioComercio {
  Comercio guardarComercio(Comercio comercio);
  List<Comercio> listarTodos();
  Comercio buscarComercio(Long id);
  List<Comercio> buscarComercioPorNombre(String nombre);
  Comercio buscarComercioPorUsuarioId(Long usuarioId);
  Comercio vincularComercioAUsuario(Comercio comercio, Long usuarioId);
  void actualizarComercio(Comercio comercio);
  void actualizarDatosDelComercio(
    Comercio existente,
    Comercio datos,
    Double latitudConfirmada,
    Double longitudConfirmada
  );
  List<UbicacionSugerida> sugerirUbicaciones(String direccion, String localidad);

  /**
   * Si el comercio no tiene ubicación, la vuelve a buscar y la guarda si aparece. Sirve para los
   * comercios cargados antes de Georef o durante una falla de red, que si no quedarían sin mapa.
   */
  void ubicarSiFalta(Comercio comercio);
  Comercio buscarPorDireccionYLocalidad(String direccion, String localidad);
  void guardarHorarios(Long comercioId, List<HorarioComercio> horarios);
  List<HorarioComercio> obtenerHorarios(Long comercioId);
  Boolean estaAbierto(Long comercioId);
  void guardarFoto(Long comercioId, byte[] foto) throws FotoInvalida;
  void solicitarVerificacion(Long comercioId);
  List<Comercio> listarSolicitudesVerificacion();
  void aprobarVerificacion(Long comercioId);
}
