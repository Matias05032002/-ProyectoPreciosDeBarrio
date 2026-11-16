package com.tallerwebi.dominio.Producto;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;

// Busca la foto del producto en el catálogo público de un supermercado argentino (Carrefour, DIA).
// Esas tiendas usan VTEX, que publica el buscador sin clave en /api/catalog_system/pub/products/search
// y devuelve la foto de la góndola de cada producto con su marca.
class ServicioImagenSupermercadoImpl extends ServicioImagenDeCatalogo {

  private static final String RUTA_BUSQUEDA =
    "/api/catalog_system/pub/products/search?_from=0&_to=9&ft=";
  private static final String DOMINIO_DE_IMAGENES = "vteximg.com.br";

  private final String urlBase;

  ServicioImagenSupermercadoImpl(String urlBase) {
    this(urlBase, clienteReal(Duration.ofSeconds(4)));
  }

  ServicioImagenSupermercadoImpl(String urlBase, ClienteHttp cliente) {
    super(cliente);
    this.urlBase = urlBase;
  }

  @Override
  String urlDeBusqueda(String nombre, String marca) {
    return urlBase + RUTA_BUSQUEDA + codificar(textoDeBusqueda(nombre, marca));
  }

  @Override
  String fuente() {
    return urlBase;
  }

  @Override
  String primeraImagenValida(JsonNode catalogo, String nombre, String marca, String unidad) {
    if (!catalogo.isArray()) {
      return null;
    }
    for (JsonNode producto : catalogo) {
      if (
        CoincidenciaDeProducto.coincide(
          nombre,
          marca,
          unidad,
          producto.path("productName").asText(""),
          producto.path("brand").asText("")
        )
      ) {
        String imagen = primeraImagenDe(producto);
        if (imagen != null) {
          return imagen;
        }
      }
    }
    return null;
  }

  private static String primeraImagenDe(JsonNode producto) {
    for (JsonNode item : producto.path("items")) {
      for (JsonNode foto : item.path("images")) {
        String imagen = imagenValida(foto.path("imageUrl"), DOMINIO_DE_IMAGENES);
        if (imagen != null) {
          return imagen;
        }
      }
    }
    return null;
  }
}
