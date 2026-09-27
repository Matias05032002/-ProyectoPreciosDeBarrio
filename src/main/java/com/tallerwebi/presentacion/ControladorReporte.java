package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.PrecioIncorrecto;
import com.tallerwebi.dominio.excepcion.ReporteExistente;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/reporte")
public class ControladorReporte {

    private ServicioReporte servicioReporte;
    private ServicioProducto servicioProducto;
    private ServicioComercio servicioComercio;
    private ServicioLogin servicioLogin;
    private static final String VISTA_REPORTES = "reporte/reportes";
    private static final String NOMBRE_PRODUCTOS = "productos";
    private static final String NOMBRE_COMERCIOS = "comercios";
    private static final String NOMBRE_REPORTES = "reportes";

    @Autowired
    public ControladorReporte(
            ServicioReporte servicioReporte,
            ServicioProducto servicioProducto,
            ServicioComercio servicioComercio,
            ServicioLogin servicioLogin
    ) {
        this.servicioReporte = servicioReporte;
        this.servicioProducto = servicioProducto;
        this.servicioComercio = servicioComercio;
        this.servicioLogin = servicioLogin;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView guardarReporte(@ModelAttribute Reporte reporte, HttpServletRequest request) {
        try {
            if (reporte.getPrecio() == null || reporte.getPrecio() <= 0) {
                throw new PrecioIncorrecto();
            }
            String email = (String) request.getSession().getAttribute("EMAIL");
            Usuario usuario = servicioLogin.buscarPorEmail(email);
            reporte.setUsuario(usuario);
            servicioReporte.guardarReporte(reporte);
        } catch (PrecioIncorrecto e) {
            ModelAndView mav = new ModelAndView(VISTA_REPORTES);
            mav.addObject("error", "El precio debe ser mayor a cero");
            mav.addObject(NOMBRE_REPORTES, servicioReporte.listarTodos());
            mav.addObject(NOMBRE_PRODUCTOS, servicioProducto.listarTodos());
            mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
            return mav;
        } catch (ReporteExistente e) {
            ModelAndView mav = new ModelAndView(VISTA_REPORTES);
            mav.addObject("error", "Ya reportaste este precio hoy");
            mav.addObject(NOMBRE_REPORTES, servicioReporte.listarTodos());
            mav.addObject(NOMBRE_PRODUCTOS, servicioProducto.listarTodos());
            mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
            return mav;
        }
        return new ModelAndView("redirect:/reporte");
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ModelAndView buscarReporte(@PathVariable Long id) {
        Reporte reporte = servicioReporte.buscarReporte(id);
        ModelAndView mav = new ModelAndView(VISTA_REPORTES);
        mav.addObject(NOMBRE_REPORTES, reporte);
        return mav;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listarTodos(HttpServletRequest request) {
        if (request.getSession().getAttribute("ROL") == null) {
            return new ModelAndView("redirect:/login");
        }
        List<Reporte> reportes = servicioReporte.listarTodos();
        ModelAndView mav = new ModelAndView("reporte/lista-reportes");
        mav.addObject(NOMBRE_REPORTES, reportes);
        mav.addObject(NOMBRE_PRODUCTOS, servicioProducto.listarTodos());
        mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
        return mav;
    }

    @RequestMapping(path = "/{id}/dudoso", method = RequestMethod.POST)
    public ModelAndView marcarDudoso(@PathVariable Long id) {
        servicioReporte.marcarDudoso(id);
        return new ModelAndView("redirect:/reporte");
    }

    @RequestMapping(path = "/buscar", method = RequestMethod.GET)
    public ModelAndView buscarPorNombre(
            @RequestParam("nombre") String nombre,
            HttpServletRequest request
    ) {
        if (request.getSession().getAttribute("ROL") == null) {
            return new ModelAndView("redirect:/login");
        }
        List<Reporte> resultados = servicioReporte.buscarPorNombre(nombre);
        ModelAndView mav = new ModelAndView(VISTA_REPORTES);
        mav.addObject(NOMBRE_REPORTES, resultados);
        mav.addObject(NOMBRE_PRODUCTOS, servicioProducto.listarTodos());
        mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
        mav.addObject("busqueda", nombre);
        return mav;
    }
}
