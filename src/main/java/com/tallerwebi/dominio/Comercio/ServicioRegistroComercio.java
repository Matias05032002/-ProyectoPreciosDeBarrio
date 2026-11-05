package com.tallerwebi.dominio.Comercio;

import com.tallerwebi.dominio.excepcion.ComercioYaRegistrado;
import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.DatosDeComercioInvalidos;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface ServicioRegistroComercio {
  /**
   * Crea la cuenta del comerciante y su comercio en un solo paso. Si en esa dirección y localidad
   * ya hay un comercio sin dueño (cargado antes por un vecino), la cuenta se queda con ese.
   */
  void registrar(
    String email,
    String password,
    String nombre,
    String tipo,
    String direccion,
    String localidad
  )
    throws DatosDeComercioInvalidos, ComercioYaRegistrado, UsuarioExistente, EmailInvalido, ContrasenaInvalida;
}
