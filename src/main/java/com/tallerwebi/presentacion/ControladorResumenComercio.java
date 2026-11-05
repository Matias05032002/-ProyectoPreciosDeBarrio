package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.ResumenComercio;
import com.tallerwebi.dominio.Comercio.ServicioResumenComercio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Datos del inicio del comercio: la página los pide y los dibuja
@RestController
@RequestMapping("/inicio-comercio")
public class ControladorResumenComercio {

  private static final String ROL_COMERCIO = "COMERCIO";

  private final ServicioResumenComercio servicioResumenComercio;

  @Autowired
  public ControladorResumenComercio(ServicioResumenComercio servicioResumenComercio) {
    this.servicioResumenComercio = servicioResumenComercio;
  }

  @GetMapping("/resumen")
  public ResponseEntity<ResumenComercio> resumen(HttpSession session) {
    if (!ROL_COMERCIO.equals(session.getAttribute("ROL"))) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    // El dueño siempre sale de la sesión, nunca de un parámetro
    Long usuarioId = session.getAttribute("ID") instanceof Long id ? id : null;
    ResumenComercio resumen = servicioResumenComercio.obtenerResumen(usuarioId);
    return resumen == null
      ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
      : ResponseEntity.ok(resumen);
  }
}
