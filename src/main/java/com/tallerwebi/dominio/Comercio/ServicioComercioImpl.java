package com.tallerwebi.dominio.Comercio;


import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ServicioComercio")
@Transactional
public class ServicioComercioImpl implements ServicioComercio {

    private RepositorioComercio repositorioComercio;

    @Autowired
    public ServicioComercioImpl(RepositorioComercio repositorioComercio) {
        this.repositorioComercio = repositorioComercio;
    }

    @Override
    public Comercio guardarComercio(Comercio comercio) {
        return repositorioComercio.guardarComercio(comercio);
    }

    @Override
    public List<Comercio> listarTodos() {
        return repositorioComercio.listarTodos();
    }

    @Override
    public Comercio buscarComercio(Long id) {
        return repositorioComercio.buscarComercio(id);
    }

    @Override
    public Comercio buscarComercioPorNombre(String nombre) {
        return repositorioComercio.buscarComercioPorNombre(nombre);
    }
}
