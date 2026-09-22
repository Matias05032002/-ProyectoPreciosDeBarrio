package com.tallerwebi.dominio.Comercio;

import java.util.List;

public interface ServicioComercio {
    Comercio guardarComercio(Comercio comercio);
    List<Comercio> listarTodos();
    Comercio buscarComercio(Long id);
    Comercio buscarComercioPorNombre(String nombre);
}
