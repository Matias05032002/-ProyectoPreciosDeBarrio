package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.RepositorioProducto;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioProducto")
public class RepositorioProductoImpl implements RepositorioProducto {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioProductoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Producto guardarProducto(Producto producto) {
        sessionFactory.getCurrentSession().persist(producto);
        return producto;
    }

    @Override
    public List<Producto> listarTodos() {
        return sessionFactory.getCurrentSession().createQuery("from Producto", Producto.class).list();
    }

    @Override
    public Producto buscarProductoPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Producto.class, id);
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        return sessionFactory
                .getCurrentSession()
                .createQuery("from Producto where nombre like :nombre", Producto.class)
                .setParameter("nombre", "%" + nombre + "%")
                .list();
    }

    @Override
    public Producto buscarProductoPorNombreExacto(String nombre) {
        return sessionFactory
                .getCurrentSession()
                .createQuery("from Producto where nombre = :nombre", Producto.class)
                .setParameter("nombre", nombre)
                .uniqueResult();
    }
}
