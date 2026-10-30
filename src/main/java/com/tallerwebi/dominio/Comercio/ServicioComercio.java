package com.tallerwebi.dominio.Comercio;

import java.util.List;

public interface ServicioComercio {
  Comercio guardarComercio(Comercio comercio);
  List<Comercio> listarTodos();
  Comercio buscarComercio(Long id);
  List<Comercio> buscarComercioPorNombre(String nombre);
  Comercio buscarComercioPorUsuarioId(Long usuarioId);
  Comercio vincularComercioAUsuario(Comercio comercio, Long usuarioId);
  void actualizarComercio(Comercio comercio);
  Comercio buscarPorDireccionYLocalidad(String direccion, String localidad);
  void guardarHorarios(Long comercioId, List<HorarioComercio> horarios);
  List<HorarioComercio> obtenerHorarios(Long comercioId);
  Boolean estaAbierto(Long comercioId);
  void guardarFoto(Long comercioId, byte[] foto, String fotoTipo);
}
