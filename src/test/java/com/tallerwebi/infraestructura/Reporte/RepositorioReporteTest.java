package com.tallerwebi.infraestructura.Reporte;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;

import com.tallerwebi.infraestructura.RepositorioReporteImpl;
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

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
public class RepositorioReporteTest {

    @Autowired
    private SessionFactory sessionFactory;

    private RepositorioReporte repositorioReporte;

    @BeforeEach
    public void init() {
        repositorioReporte = new RepositorioReporteImpl(sessionFactory);
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaGuardarUnNuevoReporte() {
        Reporte reporte = dadoQueTengoUnReporte("Leche", "Almacen", 100.0);

        Reporte resultado = cuandoGuardoUnReporte(reporte);

        entoncesElReporteSeGuardo(resultado);
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaBuscarUnReportePorId() {
        Reporte reporte = dadoQueTengoUnReporte("Arroz", "Kiosco", 200.0);
        dadoQueExisteElReporte(reporte);

        Reporte resultado = cuandoBuscoPorId(reporte.getId());

        entoncesElReporteEsCorrecto(resultado);
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaBuscarReportesPorNombreDeProducto() {
        Reporte reporte = dadoQueTengoUnReporte("Fideos", "Almacen", 150.0);
        dadoQueExisteElReporte(reporte);

        List<Reporte> resultado = cuandoBuscoPorNombre("Fideos");

        assertThat(resultado, not(empty()));
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaMarcarUnReporteComoDudoso() {
        Reporte reporte = dadoQueTengoUnReporte("Leche", "Almacen", 100.0);
        dadoQueExisteElReporte(reporte);

        int puntuacionAntes = reporte.getPuntuacion();
        Reporte resultado = repositorioReporte.marcarDudoso(reporte.getId());

        assertThat(resultado.getPuntuacion(), is(equalTo(puntuacionAntes + 1)));
    }

    @Test
    @Transactional
    @Rollback
    public void deberiaListarTodosLosReportes() {
        dadoQueExisteElReporte(dadoQueTengoUnReporte("Leche", "Almacen", 100.0));
        dadoQueExisteElReporte(dadoQueTengoUnReporte("Arroz", "Kiosco", 200.0));

        List<Reporte> resultado = repositorioReporte.listarTodos();

        assertThat(resultado.size(), is(greaterThanOrEqualTo(2)));
    }

    private Reporte dadoQueTengoUnReporte(String nombreProducto, String nombreComercio, Double precio) {
        Producto producto = new Producto();
        producto.setNombre(nombreProducto);
        sessionFactory.getCurrentSession().persist(producto);

        Comercio comercio = new Comercio();
        comercio.setNombre(nombreComercio);
        sessionFactory.getCurrentSession().persist(comercio);

        Reporte reporte = new Reporte();
        reporte.setProducto(producto);
        reporte.setComercio(comercio);
        reporte.setPrecio(precio);
        reporte.setFechaDeReporte(LocalDateTime.now());
        return reporte;
    }

    private void dadoQueExisteElReporte(Reporte reporte) {
        sessionFactory.getCurrentSession().persist(reporte);
    }

    private Reporte cuandoGuardoUnReporte(Reporte reporte) {
        return repositorioReporte.guardarReporte(reporte);
    }

    private Reporte cuandoBuscoPorId(Long id) {
        return repositorioReporte.buscarReporte(id);
    }

    private List<Reporte> cuandoBuscoPorNombre(String nombre) {
        return repositorioReporte.buscarPorNombre(nombre);
    }

    private void entoncesElReporteSeGuardo(Reporte resultado) {
        assertThat(resultado, is(notNullValue()));
        assertThat(resultado.getId(), is(notNullValue()));
    }

    private void entoncesElReporteEsCorrecto(Reporte resultado) {
        assertThat(resultado, is(notNullValue()));
    }
}