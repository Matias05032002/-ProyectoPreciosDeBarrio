package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/producto")
public class ControladorProducto {

    private ServicioProducto servicioProducto;
    private static final String VISTA_PRODUCTOS = "productos";

    @Autowired
    public ControladorProducto(ServicioProducto servicioProducto) {
        this.servicioProducto = servicioProducto;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView guardarProducto(@ModelAttribute Producto producto) {
        servicioProducto.guardarProducto(producto);
        return new ModelAndView("redirect:/productos");
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listarProductos() {
        ModelAndView mav = new ModelAndView("lista-productos");
        mav.addObject(VISTA_PRODUCTOS, servicioProducto.listarTodos());
        return mav;
    }

    @RequestMapping(path = "/buscar", method = RequestMethod.GET)
    public ModelAndView buscarProducto(@RequestParam("nombre") String nombre) {
        List<Producto> productos = servicioProducto.buscarPorNombre(nombre);
        ModelAndView mav = new ModelAndView(VISTA_PRODUCTOS);
        mav.addObject("productos", productos);
        return mav;
    }

    public ModelAndView buscarPorId(Long id) {
        Producto producto = servicioProducto.buscarProductoPorId(id);
        ModelAndView mav = new ModelAndView(VISTA_PRODUCTOS);
        mav.addObject(VISTA_PRODUCTOS, producto);
        return mav;
    }
}
