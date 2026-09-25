package com.tallerwebi.infraestructura.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.RepositorioProducto;
import com.tallerwebi.infraestructura.RepositorioProductoImpl;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
public class RepositorioProductoTest {

    @Autowired
    private SessionFactory sessionFactory;

    private RepositorioProducto repositorioProducto;

    @BeforeEach
    public void init() {
        repositorioProducto = new RepositorioProductoImpl(sessionFactory);
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaGuardarUnNuevoProducto() {
        Producto producto = dadoQueTengoUnProducto("Leche");

        Producto resultado = cuandoGuardoUnProducto(producto);

        entoncesElProductoSeGuardo(resultado, "Leche");
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaBuscarUnProductoPorNombre() {
        Producto producto = dadoQueTengoUnProducto("Arroz");
        dadoQueExisteElProducto(producto);

        List<Producto> resultado = cuandoBuscoPorNombre("Arroz");

        assertThat(resultado, not(empty()));
        assertThat(resultado.get(0).getNombre(), is(equalTo("Arroz")));
    }

    @Test
    @Transactional
    public void noDeberiaBuscarUnProductoInexistente() {
        List<Producto> resultado = cuandoBuscoPorNombre("ProductoInexistente");
        assertThat(resultado, is(empty()));
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaListarTodosLosProductos() {
        dadoQueExisteElProducto(dadoQueTengoUnProducto("Leche"));
        dadoQueExisteElProducto(dadoQueTengoUnProducto("Arroz"));

        List<Producto> resultado = repositorioProducto.listarTodos();

        assertThat(resultado.size(), is(greaterThanOrEqualTo(2)));
    }
    @Test
    @Transactional
    @Rollback
    public void deberiaBuscarUnProductoPorId() {
        Producto producto = dadoQueTengoUnProducto("Leche");
        dadoQueExisteElProducto(producto);

        Producto resultado = cuandoBuscoPorId(producto.getId());

        assertThat(resultado, is(notNullValue()));
        entoncesElProductoEsCorrecto(resultado, "Leche");
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaBuscarUnProductoPorNombreExacto() {
        Producto producto = dadoQueTengoUnProducto("Arroz");
        dadoQueExisteElProducto(producto);

        Producto resultado = cuandoBuscoPorNombreExacto("Arroz");

        assertThat(resultado, is(notNullValue()));
        entoncesElProductoEsCorrecto(resultado, "Arroz");
    }

    @Test
    @Transactional
    public void noDeberiaBuscarUnProductoPorNombreExactoSiNoExiste() {
        Producto resultado = cuandoBuscoPorNombreExacto("Inexistente");
        entoncesElProductoEsNull(resultado);
    }

    private Producto dadoQueTengoUnProducto(String nombre) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        return producto;
    }

    private void dadoQueExisteElProducto(Producto producto) {
        sessionFactory.getCurrentSession().persist(producto);
    }

    private Producto cuandoGuardoUnProducto(Producto producto) {
        return repositorioProducto.guardarProducto(producto);
    }

    private List<Producto> cuandoBuscoPorNombre(String nombre) {
        return repositorioProducto.buscarPorNombre(nombre);
    }

    private void entoncesElProductoSeGuardo(Producto resultado, String nombreEsperado) {
        assertThat(resultado, is(notNullValue()));
        assertThat(resultado.getNombre(), is(equalTo(nombreEsperado)));
    }
    private Producto cuandoBuscoPorId(Long id) {
        return repositorioProducto.buscarProductoPorId(id);
    }

    private Producto cuandoBuscoPorNombreExacto(String nombre) {
        return repositorioProducto.buscarProductoPorNombreExacto(nombre);
    }

    private void entoncesElProductoEsNull(Producto resultado) {
        assertThat(resultado, is(nullValue()));
    }

    private void entoncesElProductoEsCorrecto(Producto resultado, String nombreEsperado) {
        assertThat(resultado, is(notNullValue()));
        assertThat(resultado.getNombre(), is(equalTo(nombreEsperado)));
    }
}