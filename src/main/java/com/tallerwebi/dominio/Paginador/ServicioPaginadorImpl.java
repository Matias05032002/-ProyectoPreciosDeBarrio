package com.tallerwebi.dominio.Paginador;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioPaginadorImpl implements ServicioPaginador {

  @Override
  public <T> List<T> paginar(List<T> lista, int pagina, int tamano) {
    int desde = Math.max(0, pagina) * tamano;
    if (desde >= lista.size()) return Collections.emptyList();
    return lista.subList(desde, Math.min(desde + tamano, lista.size()));
  }

  @Override
  public int calcularTotalPaginas(int total, int tamano) {
    if (tamano <= 0) return 0;
    return (int) Math.ceil((double) total / tamano);
  }
}
