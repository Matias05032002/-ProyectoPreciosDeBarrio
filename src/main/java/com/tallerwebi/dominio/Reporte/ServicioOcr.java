package com.tallerwebi.dominio.Reporte;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.tallerwebi.dominio.Comercio.TipoDeComercio;
import com.tallerwebi.dominio.Gemini.ServicioGemini;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

@Service
public class ServicioOcr {

  private static final String CAMPO_NOMBRE = "nombre";
  private static final String CAMPO_MARCA = "marca";
  private static final String CAMPO_CATEGORIA = "categoria";
  private static final String CAMPO_UNIDAD = "unidad";
  private static final String CAMPO_PRECIO = "precio";
  private static final String CAMPO_TEXTO = "text";
  private static final String CAMPO_TIPO_COMERCIO = "tipoComercio";
  private static final String DEFAULT_MARCA = "Sin marca";
  private static final String DEFAULT_CATEGORIA = "Varios";
  private static final String DEFAULT_UNIDAD = "1u";
  private static final String MIME_JPEG = "image/jpeg";
  private static final double PRECIO_MAXIMO = 1_000_000;
  private static final int LADO_MAXIMO = 1600;

  private final ServicioGemini geminiClient;

  public ServicioOcr(ServicioGemini geminiClient) {
    this.geminiClient = geminiClient;
  }

  public List<ItemTicket> extraerProductosDeTicket(byte[] imagenBytes) throws Exception {
    return leerTicket(imagenBytes).items();
  }

  // Una sola llamada a la IA devuelve los productos y el tipo de comercio que deduce del ticket.
  public TicketLeido leerTicket(byte[] imagenBytes) throws Exception {
    String response = geminiClient.llamarGemini(buildRequestBody(imagenBytes, buildPrompt()));
    return new TicketLeido(parseResponse(response), leerTipoDeComercio(response));
  }

  public ItemTicket reconocerProducto(byte[] imagenBytes) throws Exception {
    String prompt =
      "Sos un asistente que identifica productos de supermercado argentino a partir de fotos. " +
      "Analizá esta imagen y devolvé UN objeto JSON con: " +
      "nombre (string, nombre genérico del producto sin marca ni gramaje), " +
      "marca (string o null si no se ve), " +
      "categoria (string: uno EXACTAMENTE de: Almacen, Bebidas sin alcohol, Bebidas con alcohol, " +
      "Carnes y aves, Cereales y legumbres, Condimentos y salsas, Congelados, Enlatados y conservas, " +
      "Farmacia y perfumeria, Ferreteria y bazar, Fiambres y embutidos, Higiene personal, Lacteos, " +
      "Limpieza del hogar, Mascotas, Panaderia y reposteria, Pescados y mariscos, " +
      "Productos de dietetica, Snacks y golosinas, Tabaco, Verduras y frutas, Varios), " +
      "unidad (string con gramaje/volumen como '1kg', '500g', '1L', o null si no se ve). " +
      "Devolvé ÚNICAMENTE el objeto JSON, sin explicación, sin markdown.";
    String response = geminiClient.llamarGemini(buildRequestBody(imagenBytes, prompt));

    JsonObject obj = JsonParser
      .parseString(limpiarMarkdown(extraerTexto(response)))
      .getAsJsonObject();
    return new ItemTicket(
      leerString(obj, CAMPO_NOMBRE, ""),
      0,
      leerString(obj, CAMPO_CATEGORIA, DEFAULT_CATEGORIA),
      leerString(obj, CAMPO_MARCA, DEFAULT_MARCA),
      leerUnidad(obj)
    );
  }

  private String buildPrompt() {
    return (
      "Sos un asistente que extrae productos de tickets de supermercado argentino. " +
      "De esta imagen de ticket, extraé SOLO los productos comprados con su precio final. " +
      "Ignorá completamente: nombre del comercio, dirección, fecha, número de ticket, cajero, " +
      "subtotales, IVA, TOTAL, descuentos, leyendas, agradecimientos. " +
      "Para cada producto devolvé un objeto JSON con estos campos: " +
      "nombre (string, SOLO el nombre del producto real, de 1 a 3 palabras completas, sin marca, " +
      "gramaje, variedad, calidad ni descriptores. Si una palabra viene abreviada o cortada " +
      "(semidescre, noct, l/v) no la incluyas: dejá solo las palabras que se entienden. " +
      "Ejemplos: 'MELBA RELLENA' -> 'Melba'; 'PAPA NEGRA COMERCIAL' -> 'Papa negra'; " +
      "'LECHE SEMIDESCRE L/V' -> 'Leche'. Si en la línea solo aparece una marca, el nombre es el " +
      "producto por el que se la conoce: 'HELLMANNS' -> 'Mayonesa'; 'LA SERENISIMA' -> 'Leche'. " +
      "Escribilo con la primera letra en mayúscula), " +
      "precio (number, el PRECIO UNITARIO, sin signo $ ni separador de miles, ej: 2490.50; " +
      "si la línea muestra cantidad por precio unitario, por ejemplo '2,00 X 6890,00' con " +
      "importe 13780,00, el precio es 6890 y NO el importe total de la línea), " +
      "categoria (string: uno EXACTAMENTE de estos valores: Almacen, Bebidas sin alcohol, " +
      "Bebidas con alcohol, Carnes y aves, Cereales y legumbres, Condimentos y salsas, " +
      "Congelados, Enlatados y conservas, Farmacia y perfumeria, Ferreteria y bazar, " +
      "Fiambres y embutidos, Higiene personal, Lacteos, Limpieza del hogar, Mascotas, " +
      "Panaderia y reposteria, Pescados y mariscos, Productos de dietetica, " +
      "Snacks y golosinas, Tabaco, Verduras y frutas, Varios), " +
      "marca (string o null si no se ve), " +
      "unidad (string con el gramaje o volumen como '1kg', '500g', '1L', '250ml', o null si no se ve. " +
      "Si el producto se vende por peso (la línea muestra una cantidad con decimales y 'Kg', o dice " +
      "'granel'), el precio es por kilo y la unidad es '1kg'). " +
      "Devolvé ÚNICAMENTE un objeto JSON válido con dos campos: " +
      "tipoComercio (string: uno EXACTAMENTE de estos valores: Almacen, Verduleria, Carniceria, " +
      "Kiosco, Supermercado, Otro. Deducilo del encabezado del ticket y de los productos " +
      "comprados; si no estás seguro, 'Otro') y " +
      "productos (el array con los productos comprados). " +
      "Sin explicación, sin markdown, sin texto adicional."
    );
  }

  private String buildRequestBody(byte[] imagenBytes, String prompt) throws IOException {
    byte[] imagen = reducirImagen(imagenBytes);

    JsonObject parteTexto = new JsonObject();
    parteTexto.addProperty(CAMPO_TEXTO, prompt);

    JsonObject inlineData = new JsonObject();
    inlineData.addProperty("mime_type", detectarMimeType(imagen));
    inlineData.addProperty("data", Base64.getEncoder().encodeToString(imagen));

    JsonObject parteImagen = new JsonObject();
    parteImagen.add("inline_data", inlineData);

    JsonArray parts = new JsonArray();
    parts.add(parteTexto);
    parts.add(parteImagen);

    JsonObject content = new JsonObject();
    content.add("parts", parts);

    JsonArray contents = new JsonArray();
    contents.add(content);

    JsonObject thinking = new JsonObject();
    thinking.addProperty("thinkingLevel", "low");

    JsonObject config = new JsonObject();
    config.addProperty("responseMimeType", "application/json");
    config.add("thinkingConfig", thinking);

    JsonObject body = new JsonObject();
    body.add("contents", contents);
    body.add("generationConfig", config);
    return body.toString();
  }

  // Las fotos de celular pesan varios MB y hacen que Gemini tarde demasiado (Read timed out).
  // Para leer un ticket alcanza con 1600 px en el lado largo.
  private byte[] reducirImagen(byte[] bytes) throws IOException {
    BufferedImage original = ImageIO.read(new ByteArrayInputStream(bytes));
    if (original == null) {
      return bytes;
    }
    int ladoLargo = Math.max(original.getWidth(), original.getHeight());
    double escala = Math.min(1.0, (double) LADO_MAXIMO / ladoLargo);
    int ancho = (int) Math.round(original.getWidth() * escala);
    int alto = (int) Math.round(original.getHeight() * escala);

    BufferedImage reducida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
    Graphics2D grafico = reducida.createGraphics();
    grafico.setColor(Color.WHITE);
    grafico.fillRect(0, 0, ancho, alto);
    grafico.setRenderingHint(
      RenderingHints.KEY_INTERPOLATION,
      RenderingHints.VALUE_INTERPOLATION_BILINEAR
    );
    grafico.drawImage(original, 0, 0, ancho, alto, null);
    grafico.dispose();

    ByteArrayOutputStream salida = new ByteArrayOutputStream();
    ImageIO.write(reducida, "jpg", salida);
    return salida.toByteArray();
  }

  private String detectarMimeType(byte[] bytes) {
    boolean esPng = bytes.length > 3 && (bytes[0] & 0xFF) == 0x89 && bytes[1] == 'P';
    boolean esWebp = bytes.length > 11 && bytes[0] == 'R' && bytes[8] == 'W';
    if (esPng) {
      return "image/png";
    }
    if (esWebp) {
      return "image/webp";
    }
    return MIME_JPEG;
  }

  private List<ItemTicket> parseResponse(String jsonResponse) {
    List<ItemTicket> items = new ArrayList<>();
    JsonElement raiz = JsonParser.parseString(limpiarMarkdown(extraerTexto(jsonResponse)));
    for (JsonElement el : obtenerArray(raiz)) {
      ItemTicket item = convertirEnItem(el);
      if (item != null) {
        items.add(item);
      }
    }
    return items;
  }

  // Solo se acepta un tipo de la lista: cualquier otra cosa que invente el modelo se descarta
  private String leerTipoDeComercio(String jsonResponse) {
    JsonElement raiz = JsonParser.parseString(limpiarMarkdown(extraerTexto(jsonResponse)));
    if (!raiz.isJsonObject()) {
      return null;
    }
    JsonObject objeto = raiz.getAsJsonObject();
    if (!objeto.has(CAMPO_TIPO_COMERCIO) || !objeto.get(CAMPO_TIPO_COMERCIO).isJsonPrimitive()) {
      return null;
    }
    return TipoDeComercio.normalizar(objeto.get(CAMPO_TIPO_COMERCIO).getAsString());
  }

  private JsonArray obtenerArray(JsonElement raiz) {
    if (raiz.isJsonArray()) {
      return raiz.getAsJsonArray();
    }
    if (raiz.isJsonObject()) {
      for (String clave : raiz.getAsJsonObject().keySet()) {
        JsonElement valor = raiz.getAsJsonObject().get(clave);
        if (valor.isJsonArray()) {
          return valor.getAsJsonArray();
        }
      }
    }
    return new JsonArray();
  }

  private ItemTicket convertirEnItem(JsonElement el) {
    if (!el.isJsonObject()) {
      return null;
    }
    JsonObject obj = el.getAsJsonObject();
    double precio;
    try {
      precio = leerPrecio(obj);
    } catch (NumberFormatException | IllegalStateException e) {
      return null;
    }
    if (precio <= 0 || precio >= PRECIO_MAXIMO) {
      return null;
    }
    return new ItemTicket(
      leerString(obj, CAMPO_NOMBRE, ""),
      precio,
      leerString(obj, CAMPO_CATEGORIA, DEFAULT_CATEGORIA),
      leerString(obj, CAMPO_MARCA, DEFAULT_MARCA),
      leerUnidad(obj)
    );
  }

  private String extraerTexto(String jsonResponse) {
    StringBuilder sb = new StringBuilder();
    for (JsonElement p : obtenerParts(jsonResponse)) {
      JsonObject part = p.getAsJsonObject();
      boolean esPensamiento = part.has("thought") && part.get("thought").getAsBoolean();
      if (part.has(CAMPO_TEXTO) && !esPensamiento) {
        sb.append(part.get(CAMPO_TEXTO).getAsString());
      }
    }
    return sb.toString().trim();
  }

  // Si Gemini bloquea la imagen o no responde nada, avisamos con el JSON para poder diagnosticar
  private JsonArray obtenerParts(String jsonResponse) {
    JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
    JsonArray candidatos = root.getAsJsonArray("candidates");
    if (candidatos == null || candidatos.isEmpty()) {
      throw new IllegalStateException("Gemini no devolvio resultados: " + jsonResponse);
    }
    JsonObject contenido = candidatos.get(0).getAsJsonObject().getAsJsonObject("content");
    if (contenido == null || !contenido.has("parts")) {
      throw new IllegalStateException("Gemini devolvio una respuesta vacia: " + jsonResponse);
    }
    return contenido.getAsJsonArray("parts");
  }

  private String limpiarMarkdown(String texto) {
    return texto.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
  }

  private String leerString(JsonObject obj, String campo, String porDefecto) {
    if (obj.has(campo) && !obj.get(campo).isJsonNull()) {
      return obj.get(campo).getAsString();
    }
    return porDefecto;
  }

  private String leerUnidad(JsonObject obj) {
    String unidad = leerString(obj, CAMPO_UNIDAD, DEFAULT_UNIDAD)
      .replace(",", ".")
      .replace(" ", "");
    return unidad.matches(ServicioReporteImpl.FORMATO_UNIDAD) ? unidad : DEFAULT_UNIDAD;
  }

  private double leerPrecio(JsonObject obj) {
    JsonElement el = obj.get(CAMPO_PRECIO);
    if (el == null || el.isJsonNull()) {
      return 0;
    }
    JsonPrimitive primitivo = el.getAsJsonPrimitive();
    if (primitivo.isNumber()) {
      return primitivo.getAsDouble();
    }
    String texto = primitivo.getAsString().replaceAll("[^0-9.,]", "");
    if (texto.contains(",")) {
      texto = texto.replace(".", "").replace(",", ".");
    } else if (!texto.matches("\\d+\\.\\d{1,2}")) {
      texto = texto.replace(".", "");
    }
    return texto.isEmpty() ? 0 : Double.parseDouble(texto);
  }
}
