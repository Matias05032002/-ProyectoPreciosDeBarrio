package com.tallerwebi.dominio.Reporte;

import com.google.gson.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioOcr {

  private static final String CAMPO_NOMBRE = "nombre";
  private static final String CAMPO_MARCA = "marca";
  private static final String CAMPO_CATEGORIA = "categoria";
  private static final String CAMPO_UNIDAD = "unidad";
  private static final String DEFAULT_MARCA = "Sin marca";
  private static final String DEFAULT_CATEGORIA = "Varios";
  private static final String DEFAULT_UNIDAD = "1u";

  private final GeminiClient geminiClient;

  public ServicioOcr(GeminiClient geminiClient) {
    this.geminiClient = geminiClient;
  }

  public List<ItemTicket> extraerProductosDeTicket(byte[] imagenBytes) throws Exception {
    try {
      String imagenBase64 = Base64.getEncoder().encodeToString(imagenBytes);
      String prompt = buildPrompt();
      String requestBody = buildRequestBody(imagenBase64, prompt);
      String response = geminiClient.llamar(requestBody);
      return parseResponse(response);
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }

  public ItemTicket reconocerProducto(byte[] imagenBytes) throws Exception {
    String imagenBase64 = Base64.getEncoder().encodeToString(imagenBytes);
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
    String requestBody = buildRequestBody(imagenBase64, prompt);
    String response = geminiClient.llamar(requestBody);

    JsonObject root = JsonParser.parseString(response).getAsJsonObject();
    String texto = root
      .getAsJsonArray("candidates")
      .get(0)
      .getAsJsonObject()
      .getAsJsonObject("content")
      .getAsJsonArray("parts")
      .get(0)
      .getAsJsonObject()
      .get("text")
      .getAsString()
      .trim();
    if (texto.startsWith("```")) {
      texto = texto.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
    }
    JsonObject obj = JsonParser.parseString(texto).getAsJsonObject();
    String nombre = obj.has(CAMPO_NOMBRE) ? obj.get(CAMPO_NOMBRE).getAsString() : "";
    String marca = obj.has(CAMPO_MARCA) && !obj.get(CAMPO_MARCA).isJsonNull()
      ? obj.get(CAMPO_MARCA).getAsString()
      : DEFAULT_MARCA;
    String categoria = obj.has(CAMPO_CATEGORIA) && !obj.get(CAMPO_CATEGORIA).isJsonNull()
      ? obj.get(CAMPO_CATEGORIA).getAsString()
      : DEFAULT_CATEGORIA;
    String unidad = obj.has(CAMPO_UNIDAD) && !obj.get(CAMPO_UNIDAD).isJsonNull()
      ? obj.get(CAMPO_UNIDAD).getAsString()
      : DEFAULT_UNIDAD;
    return new ItemTicket(nombre, 0, categoria, marca, unidad);
  }

  private String buildPrompt() {
    return (
      "Sos un asistente que extrae productos de tickets de supermercado argentino. " +
      "De esta imagen de ticket, extraé SOLO los productos comprados con su precio final. " +
      "Ignorá completamente: nombre del comercio, dirección, fecha, número de ticket, cajero, " +
      "subtotales, IVA, TOTAL, descuentos, leyendas, agradecimientos. " +
      "Para cada producto devolvé un objeto JSON con estos campos: " +
      "nombre (string, solo el nombre sin marca ni gramaje), " +
      "precio (number), " +
      "categoria (string: uno EXACTAMENTE de estos valores: Almacen, Bebidas sin alcohol, " +
      "Bebidas con alcohol, Carnes y aves, Cereales y legumbres, Condimentos y salsas, " +
      "Congelados, Enlatados y conservas, Farmacia y perfumeria, Ferreteria y bazar, " +
      "Fiambres y embutidos, Higiene personal, Lacteos, Limpieza del hogar, Mascotas, " +
      "Panaderia y reposteria, Pescados y mariscos, Productos de dietetica, " +
      "Snacks y golosinas, Tabaco, Verduras y frutas, Varios), " +
      "marca (string o null si no se ve), " +
      "unidad (string con el gramaje o volumen como '1kg', '500g', '1L', '250ml', o null si no se ve). " +
      "Devolvé ÚNICAMENTE un array JSON válido, sin explicación, sin markdown, sin texto adicional."
    );
  }

  private String buildRequestBody(String imagenBase64, String prompt) {
    JsonObject part1 = new JsonObject();
    part1.addProperty("text", prompt);

    JsonObject inlineData = new JsonObject();
    inlineData.addProperty("mime_type", "image/jpeg");
    inlineData.addProperty("data", imagenBase64);

    JsonObject part2 = new JsonObject();
    part2.add("inline_data", inlineData);

    JsonArray parts = new JsonArray();
    parts.add(part1);
    parts.add(part2);

    JsonObject content = new JsonObject();
    content.add("parts", parts);

    JsonArray contents = new JsonArray();
    contents.add(content);

    JsonObject body = new JsonObject();
    body.add("contents", contents);

    return body.toString();
  }

  @SuppressWarnings("PMD.CyclomaticComplexity")
  private List<ItemTicket> parseResponse(String jsonResponse) {
    List<ItemTicket> items = new ArrayList<>();
    try {
      JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
      String texto = root
        .getAsJsonArray("candidates")
        .get(0)
        .getAsJsonObject()
        .getAsJsonObject("content")
        .getAsJsonArray("parts")
        .get(0)
        .getAsJsonObject()
        .get("text")
        .getAsString()
        .trim();

      if (texto.startsWith("```")) {
        texto = texto.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
      }

      JsonArray array = JsonParser.parseString(texto).getAsJsonArray();
      for (JsonElement el : array) {
        JsonObject obj = el.getAsJsonObject();
        String nombre = obj.get(CAMPO_NOMBRE).getAsString();
        double precio = obj.get("precio").getAsDouble();
        String categoria = obj.has(CAMPO_CATEGORIA) && !obj.get(CAMPO_CATEGORIA).isJsonNull()
          ? obj.get(CAMPO_CATEGORIA).getAsString()
          : DEFAULT_CATEGORIA;
        String marca = obj.has(CAMPO_MARCA) && !obj.get(CAMPO_MARCA).isJsonNull()
          ? obj.get(CAMPO_MARCA).getAsString()
          : DEFAULT_MARCA;
        if (precio > 0 && precio < 1000000) {
          String unidad = obj.has(CAMPO_UNIDAD) && !obj.get(CAMPO_UNIDAD).isJsonNull()
            ? obj.get(CAMPO_UNIDAD).getAsString()
            : DEFAULT_UNIDAD;
          items.add(new ItemTicket(nombre, precio, categoria, marca, unidad));
        }
      }
    } catch (Exception e) {
      return items;
    }
    return items;
  }
}
