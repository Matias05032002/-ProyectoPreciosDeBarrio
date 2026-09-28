package com.tallerwebi.dominio.Comercio;

import java.util.List;

public interface RepositorioComercio {
  Comercio guardarComercio(Comercio comercio);
  List<Comercio> listarTodos();
  Comercio buscarComercio(Long id);
  List<Comercio> buscarComercioPorNombre(String nombre);
}
