package com.tallerwebi.infraestructura.Lista;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Lista.ItemLista;
import com.tallerwebi.dominio.Lista.Lista;
import com.tallerwebi.dominio.Lista.RepositorioLista;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.infraestructura.RepositorioListaImpl;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
public class RepositorioListaTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioLista repositorioLista;

  @BeforeEach
  public void init() {
    repositorioLista = new RepositorioListaImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnaLista() {
    Lista lista = new Lista("Lista del super", 1L);

    repositorioLista.guardar(lista);

    assertThat(lista.getId(), is(notNullValue()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarUnaListaPorId() {
    Lista lista = new Lista("Lista del super", 1L);
    repositorioLista.guardar(lista);

    Lista resultado = repositorioLista.buscarPorId(lista.getId());

    assertThat(resultado, is(notNullValue()));
    assertThat(resultado.getNombre(), is("Lista del super"));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarListasPorUsuarioId() {
    repositorioLista.guardar(new Lista("Lista 1", 1L));
    repositorioLista.guardar(new Lista("Lista 2", 1L));
    repositorioLista.guardar(new Lista("Lista de otro", 2L));

    List<Lista> resultado = repositorioLista.buscarPorUsuarioId(1L);

    assertThat(resultado, hasSize(2));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaAgregarUnItemALaLista() {
    Lista lista = new Lista("Mi lista", 1L);
    repositorioLista.guardar(lista);

    Reporte reporte = dadoQueExisteUnReporte();
    ItemLista item = new ItemLista(lista, reporte, 1);
    repositorioLista.agregarItem(item);

    assertThat(item.getId(), is(notNullValue()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaEliminarUnItemDeLaLista() {
    Lista lista = new Lista("Mi lista", 1L);
    repositorioLista.guardar(lista);

    Reporte reporte = dadoQueExisteUnReporte();
    ItemLista item = new ItemLista(lista, reporte, 1);
    repositorioLista.agregarItem(item);
    Long itemId = item.getId();

    repositorioLista.eliminarItem(itemId);

    assertThat(repositorioLista.buscarItemPorId(itemId), is(nullValue()));
  }

  private Reporte dadoQueExisteUnReporte() {
    Producto producto = new Producto();
    producto.setNombre("Leche");
    sessionFactory.getCurrentSession().persist(producto);

    Comercio comercio = new Comercio();
    comercio.setNombre("Almacen");
    sessionFactory.getCurrentSession().persist(comercio);

    Reporte reporte = new Reporte();
    reporte.setProducto(producto);
    reporte.setComercio(comercio);
    reporte.setPrecio(100.0);
    reporte.setFechaDeReporte(LocalDateTime.now());
    sessionFactory.getCurrentSession().persist(reporte);

    return reporte;
  }
}
