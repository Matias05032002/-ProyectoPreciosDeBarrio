package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Lista.ItemLista;
import com.tallerwebi.dominio.Lista.Lista;
import com.tallerwebi.dominio.Lista.RepositorioLista;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioListaImpl implements RepositorioLista {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioListaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Lista lista) {
    sessionFactory.getCurrentSession().persist(lista);
  }

  @Override
  public Lista buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Lista.class, id);
  }

  @Override
  public List<Lista> buscarPorUsuarioId(Long usuarioId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM Lista WHERE usuarioId = :usuarioId ORDER BY fechaCreacion DESC",
        Lista.class
      )
      .setParameter("usuarioId", usuarioId)
      .list();
  }

  @Override
  public void agregarItem(ItemLista item) {
    sessionFactory.getCurrentSession().persist(item);
    sessionFactory.getCurrentSession().flush();
  }

  @Override
  public void eliminarItem(Long itemId) {
    ItemLista item = buscarItemPorId(itemId);
    if (item != null) {
      Lista lista = item.getLista();
      lista.getItems().remove(item);
      sessionFactory.getCurrentSession().merge(lista);
    }
  }

  @Override
  public ItemLista buscarItemPorId(Long itemId) {
    return sessionFactory.getCurrentSession().get(ItemLista.class, itemId);
  }

  @Override
  public void eliminarLista(Long listaId) {
    Lista lista = buscarPorId(listaId);
    if (lista != null) {
      sessionFactory.getCurrentSession().remove(lista);
    }
  }

  @Override
  public void editarCantidadItem(Long itemId, int cantidad) {
    ItemLista item = buscarItemPorId(itemId);
    if (item != null) {
      item.setCantidad(cantidad);
      sessionFactory.getCurrentSession().merge(item);
    }
  }
}
