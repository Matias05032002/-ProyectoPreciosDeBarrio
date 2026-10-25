package com.tallerwebi.dominio.Paginador;

import java.util.List;

public interface ServicioPaginador {
  <T> List<T> paginar(List<T> lista, int pagina, int tamano);
  int calcularTotalPaginas(int total, int tamano);
}
