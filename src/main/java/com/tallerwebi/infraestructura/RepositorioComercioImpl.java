package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.RepositorioComercio;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioComercioImpl implements RepositorioComercio {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioComercioImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Comercio guardarComercio(Comercio comercio) {
        sessionFactory.getCurrentSession().persist(comercio);
        return comercio;
    }

    @Override
    public List<Comercio> listarTodos() {
        return sessionFactory.getCurrentSession().createQuery("from Comercio", Comercio.class).list();
    }

    @Override
    public Comercio buscarComercio(Long id) {
        return sessionFactory.getCurrentSession().get(Comercio.class, id);
    }

    @Override
    public Comercio buscarComercioPorNombre(String nombre) {
        return sessionFactory
                .getCurrentSession()
                .createQuery("from Comercio where nombre = :nombre", Comercio.class)
                .setParameter("nombre", nombre)
                .uniqueResult();
    }
}
