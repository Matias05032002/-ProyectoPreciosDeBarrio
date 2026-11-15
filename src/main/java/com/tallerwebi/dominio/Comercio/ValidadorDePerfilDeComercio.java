package com.tallerwebi.dominio.Comercio;

import com.tallerwebi.dominio.excepcion.DatosDeComercioInvalidos;

// Revisa los textos que escribe el comerciante en su perfil: obligatorios y con largos que entran
// en las columnas de la base (sin esto, un texto largo termina en un error 500)
final class ValidadorDePerfilDeComercio {

  private static final int MAX_NOMBRE = 150;
  private static final int MAX_DIRECCION = 200;
  private static final int MAX_LOCALIDAD = 100;
  private static final int MAX_DESCRIPCION = 500;

  private ValidadorDePerfilDeComercio() {}

  static void validar(Comercio datos) throws DatosDeComercioInvalidos {
    if (datos == null) {
      throw new DatosDeComercioInvalidos("Faltan los datos del comercio");
    }
    exigirTexto(datos.getNombre(), MAX_NOMBRE, "El nombre");
    exigirTexto(datos.getDireccion(), MAX_DIRECCION, "La dirección");
    exigirTexto(datos.getLocalidad(), MAX_LOCALIDAD, "La localidad");
    limitarLargo(datos.getDescripcion(), MAX_DESCRIPCION, "La descripción");
  }

  private static void exigirTexto(String valor, int maximo, String campo)
    throws DatosDeComercioInvalidos {
    if (valor == null || valor.isBlank()) {
      throw new DatosDeComercioInvalidos(campo + " es obligatorio");
    }
    limitarLargo(valor, maximo, campo);
  }

  private static void limitarLargo(String valor, int maximo, String campo)
    throws DatosDeComercioInvalidos {
    if (valor != null && valor.trim().length() > maximo) {
      throw new DatosDeComercioInvalidos(campo + " no puede superar los " + maximo + " caracteres");
    }
  }
}
