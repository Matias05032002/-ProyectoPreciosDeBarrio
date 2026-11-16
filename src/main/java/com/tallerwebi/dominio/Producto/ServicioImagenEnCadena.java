package com.tallerwebi.dominio.Producto;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

// La foto exacta de un producto se busca en varias fuentes, en orden, y se queda con la primera que
// la tiene: el catálogo de Carrefour, el de DIA, Mercado Libre y Open Food Facts. Si ninguna la
// tiene, quien llama pasa a la foto genérica.
@Primary
@Service("servicioImagenEnCadena")
public class ServicioImagenEnCadena implements ServicioImagenProducto {

  private static final String URL_CARREFOUR = "https://www.carrefour.com.ar";
  private static final String URL_DIA = "https://diaonline.supermercadosdia.com.ar";

  private final List<ServicioImagenProducto> fuentes;

  @Autowired
  public ServicioImagenEnCadena(ServicioImagenProductoImpl mercadoLibre) {
    this(
      List.of(
        new ServicioImagenSupermercadoImpl(URL_CARREFOUR),
        new ServicioImagenSupermercadoImpl(URL_DIA),
        mercadoLibre,
        new ServicioImagenOpenFoodFactsImpl()
      )
    );
  }

  ServicioImagenEnCadena(List<ServicioImagenProducto> fuentes) {
    this.fuentes = fuentes;
  }

  @Override
  public String buscarImagen(String nombre, String marca, String unidad) {
    for (ServicioImagenProducto fuente : fuentes) {
      String imagen = fuente.buscarImagen(nombre, marca, unidad);
      if (imagen != null) {
        return imagen;
      }
    }
    return null;
  }
}
