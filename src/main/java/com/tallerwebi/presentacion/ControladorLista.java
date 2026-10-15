package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Lista.Lista;
import com.tallerwebi.dominio.Lista.ServicioLista;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorLista {

  private final ServicioLista servicioLista;
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_MIS_LISTAS = "redirect:/mis-listas";
  private static final String PARAM_LISTA_ID = "listaId";

  @Autowired
  public ControladorLista(ServicioLista servicioLista) {
    this.servicioLista = servicioLista;
  }

  @GetMapping("/mis-listas")
  public ModelAndView misListas(HttpSession session) {
    Long usuarioId = (Long) session.getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);

    List<Lista> listas = servicioLista.obtenerListasDeUsuario(usuarioId);

    Map<String, Object> model = new HashMap<>();
    model.put("listas", listas);
    return new ModelAndView("lista/mis-listas", model);
  }

  @PostMapping("/mis-listas/crear")
  public ModelAndView crearLista(@RequestParam("nombre") String nombre, HttpSession session) {
    Long usuarioId = (Long) session.getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);
    Lista lista = servicioLista.crearLista(nombre, usuarioId);
    return new ModelAndView(REDIRECT_MIS_LISTAS + "/" + lista.getId());
  }

  @GetMapping("/mis-listas/{id}")
  public ModelAndView detalleLista(@PathVariable("id") Long id, HttpSession session) {
    Long usuarioId = (Long) session.getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);

    Lista lista = servicioLista.obtenerListaPorId(id);
    if (lista == null || !usuarioId.equals(lista.getUsuarioId())) {
      return new ModelAndView(REDIRECT_MIS_LISTAS);
    }

    Map<String, Object> model = new HashMap<>();
    model.put("lista", lista);
    return new ModelAndView("lista/detalle-lista", model);
  }

  @PostMapping("/mis-listas/agregar-producto")
  public ModelAndView agregarProducto(
    @RequestParam(PARAM_LISTA_ID) Long listaId,
    @RequestParam("reporteId") Long reporteId,
    @RequestParam(value = "cantidad", defaultValue = "1") int cantidad,
    HttpSession session
  ) {
    Long usuarioId = (Long) session.getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);

    servicioLista.agregarProductoALista(listaId, reporteId, cantidad);
    return new ModelAndView(REDIRECT_MIS_LISTAS + "/" + listaId);
  }

  @PostMapping("/mis-listas/eliminar-item")
  public ModelAndView eliminarItem(
    @RequestParam("itemId") Long itemId,
    @RequestParam(PARAM_LISTA_ID) Long listaId,
    HttpSession session
  ) {
    Long usuarioId = (Long) session.getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);

    servicioLista.eliminarItemDeLista(itemId);
    return new ModelAndView(REDIRECT_MIS_LISTAS + "/" + listaId);
  }

  @PostMapping("/mis-listas/eliminar")
  public ModelAndView eliminarLista(
    @RequestParam(PARAM_LISTA_ID) Long listaId,
    HttpSession session
  ) {
    Long usuarioId = (Long) session.getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);

    Lista lista = servicioLista.obtenerListaPorId(listaId);
    if (lista == null || !usuarioId.equals(lista.getUsuarioId())) {
      return new ModelAndView(REDIRECT_MIS_LISTAS);
    }

    servicioLista.eliminarLista(listaId);
    return new ModelAndView(REDIRECT_MIS_LISTAS);
  }

  @PostMapping("/mis-listas/editar-item")
  public ModelAndView editarItem(
    @RequestParam("itemId") Long itemId,
    @RequestParam(PARAM_LISTA_ID) Long listaId,
    @RequestParam("cantidad") int cantidad,
    HttpSession session
  ) {
    Long usuarioId = (Long) session.getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);

    servicioLista.editarCantidadItem(itemId, cantidad);
    return new ModelAndView(REDIRECT_MIS_LISTAS + "/" + listaId);
  }
}
