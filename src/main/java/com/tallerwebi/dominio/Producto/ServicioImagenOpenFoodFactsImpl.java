package com.tallerwebi.dominio.Producto;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;

// Open Food Facts es una base de datos abierta de productos envasados que arma la gente con la foto
// del frente del paquete. No pide clave, pero sí que el pedido diga quién lo hace.
class ServicioImagenOpenFoodFactsImpl extends ServicioImagenDeCatalogo {

  private static final String URL_BUSQUEDA =
    "https://world.openfoodfacts.org/cgi/search.pl?search_simple=1&action=process&json=1" +
    "&page_size=10&fields=product_name,brands,quantity,image_front_url&search_terms=";
  private static final String DOMINIO_DE_IMAGENES = "openfoodfacts.org";

  ServicioImagenOpenFoodFactsImpl() {
    this(clienteReal(Duration.ofSeconds(5)));
  }

  ServicioImagenOpenFoodFactsImpl(ClienteHttp cliente) {
    super(cliente);
  }

  @Override
  String urlDeBusqueda(String nombre, String marca) {
    return URL_BUSQUEDA + codificar(textoDeBusqueda(nombre, marca));
  }

  @Override
  String fuente() {
    return "Open Food Facts";
  }

  // El tamaño no viene en el nombre sino en "quantity": se junta con él para compararlo
  @Override
  String primeraImagenValida(JsonNode catalogo, String nombre, String marca, String unidad) {
    for (JsonNode producto : catalogo.path("products")) {
      String encontrado =
        producto.path("product_name").asText("") + " " + producto.path("quantity").asText("");
      if (
        CoincidenciaDeProducto.coincide(
          nombre,
          marca,
          unidad,
          encontrado,
          producto.path("brands").asText("")
        )
      ) {
        String imagen = imagenValida(producto.path("image_front_url"), DOMINIO_DE_IMAGENES);
        if (imagen != null) {
          return imagen;
        }
      }
    }
    return null;
  }
}
