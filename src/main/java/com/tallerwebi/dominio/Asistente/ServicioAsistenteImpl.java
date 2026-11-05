package com.tallerwebi.dominio.Asistente;

import com.google.gson.*;
import com.tallerwebi.dominio.Comercio.RepositorioComercio;
import com.tallerwebi.dominio.Comercio.RepositorioHorario;
import com.tallerwebi.dominio.Comercio.RepositorioPromocion;
import com.tallerwebi.dominio.Gemini.ServicioGemini;
import com.tallerwebi.dominio.Lista.RepositorioLista;
import com.tallerwebi.dominio.Producto.RepositorioProducto;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioAsistenteImpl implements ServicioAsistente {

  private static final int MAX_TURNOS_HISTORIAL = 10;
  private static final String PROMPT_DEL_SISTEMA =
    "Sos Don Peso, el asistente de Precios de Barrio, una app donde los vecinos comparan " +
    "precios de los comercios del barrio. Tu trabajo es ayudar al vecino a resolver sus " +
    "problemas de compras y a usar la app.\n" +
    "\n" +
    "Qué sabés hacer:\n" +
    "- Decir dónde está más barato un producto y comparar comercios.\n" +
    "- Armar una compra con un presupuesto: elegí los precios más bajos, sumá y mostrá " +
    "el total sin pasarte del presupuesto.\n" +
    "- Revisar las listas de compras del vecino y decirle cuánto puede ahorrar " +
    "comprando en otro comercio.\n" +
    "- Contar las promociones vigentes y tenerlas en cuenta al calcular (descuentos por " +
    "producto o por medio de pago).\n" +
    "- Recomendar comercios cercanos y decir cuáles están abiertos ahora.\n" +
    "- Explicar cómo usar la app.\n" +
    "\n" +
    "Reglas:\n" +
    "- Respondé en español rioplatense, cercano y claro, en hasta 8 líneas. Podés usar " +
    "listas con guiones y **negrita**.\n" +
    "- Precios, comercios, promociones, listas y productos: usá SOLO los datos que te " +
    "pasan. Nunca inventes. Si no hay datos, decilo y sugerí reportar el precio en la app.\n" +
    "- Si el vecino pregunta por un comercio abierto, usá solo el estado que figura en los " +
    "datos (\"abierto ahora\", \"cerrado ahora\", \"sin horario cargado\"). Nunca supongas un horario.\n" +
    "- Las promociones que figuran como \"todavía NO empezaron\" no se pueden usar hoy: " +
    "nunca las presentes como vigentes.\n" +
    "- Mencioná siempre el comercio y la dirección del precio que recomendás.\n" +
    "- Si un dato dice \"hace N días\" con N mayor a 5, o figura como \"dato viejo\", " +
    "avisá que puede haber cambiado. Si figura como dudoso, avisá que no es seguro.\n" +
    "- Si un producto está en el catálogo pero sin precios, decí que la app lo conoce pero " +
    "nadie reportó su precio y sugerí reportarlo.\n" +
    "- Si el vecino cuenta un problema o una duda, ayudalo paso a paso. Si te falta " +
    "información para ayudarlo (presupuesto, producto, cantidad), hacé UNA sola pregunta corta.\n" +
    "- Si el tema no tiene que ver con compras, precios o la app, decí amablemente que solo " +
    "podés ayudar con eso.\n" +
    "- No des consejos médicos, legales ni financieros.\n" +
    "\n" +
    "Guía de la app (para explicar cómo usarla; si no estás seguro de un paso exacto, " +
    "indicá en qué sección buscarlo, sin inventar botones):\n" +
    "- Menú: Productos, Comercios, Promociones, Reportes, Mis listas y Mi perfil. En el " +
    "celular hay una barra inferior y el botón Menú. En Opciones están Sobre la app, " +
    "Dar feedback y Cerrar sesión.\n" +
    "- Buscar un producto: en Productos se busca por nombre y se ve en qué comercio está más barato.\n" +
    "- Reportar un precio: se carga en Reportes, a mano o subiendo la foto del ticket (la app " +
    "reconoce los productos y los precios y se confirman antes de guardar). El precio tiene que " +
    "ser mayor a cero, con hasta 2 decimales, y no se puede reportar el mismo producto en el " +
    "mismo comercio más de una vez por día. El propio reporte se puede editar durante 2 días.\n" +
    "- Reportes dudosos: si un precio de otro vecino parece mal, se lo marca como dudoso. No se " +
    "puede marcar el propio ni marcar dos veces el mismo. Con varias marcas se muestra como " +
    "dudoso y, si llega al máximo, se elimina.\n" +
    "- Mis listas: se crea una lista, se le agregan productos desde un precio reportado, se " +
    "cambian las cantidades y se ve el total estimado antes de salir a comprar.\n" +
    "- Comercios: muestra el mapa con los comercios del barrio, sus horarios y cómo llegar.\n" +
    "- Promociones: ofertas de los comercios, por producto o por medio de pago.\n" +
    "- Mi perfil: datos personales, foto y cambio de contraseña.";

  private final ServicioGemini servicioGemini;
  private final ContextoAsistente contextoAsistente;

  public ServicioAsistenteImpl(
    RepositorioReporte repositorioReporte,
    ServicioGemini servicioGemini,
    RepositorioPromocion repositorioPromocion,
    RepositorioLista repositorioLista,
    RepositorioProducto repositorioProducto,
    RepositorioComercio repositorioComercio,
    RepositorioHorario repositorioHorario
  ) {
    this.servicioGemini = servicioGemini;
    this.contextoAsistente =
      new ContextoAsistente(
        repositorioReporte,
        repositorioPromocion,
        repositorioLista,
        repositorioProducto,
        repositorioComercio,
        repositorioHorario,
        Clock.systemDefaultZone()
      );
  }

  @Override
  public String consultar(
    String pregunta,
    Double latitud,
    Double longitud,
    Long usuarioId,
    List<Map<String, String>> historial
  ) throws Exception {
    String contexto = contextoAsistente.construir(pregunta, latitud, longitud, usuarioId);
    String userPrompt = construirUserPrompt(contexto, pregunta, historial);
    return llamarGemini(PROMPT_DEL_SISTEMA, userPrompt);
  }

  private String construirUserPrompt(
    String contexto,
    String pregunta,
    List<Map<String, String>> historial
  ) {
    StringBuilder sb = new StringBuilder();
    sb.append("Datos disponibles:\n").append(contexto).append("\n");

    if (historial != null && !historial.isEmpty()) {
      sb.append("Conversación anterior:\n");
      int desde = Math.max(0, historial.size() - MAX_TURNOS_HISTORIAL); // user + assistant
      for (int i = desde; i < historial.size(); i++) {
        Map<String, String> turno = historial.get(i);
        String rol = "user".equals(turno.get("role")) ? "Vecino" : "Don Peso";
        sb.append(rol).append(": ").append(turno.get("content")).append("\n");
      }
      sb.append("\n");
    }

    sb.append("Pregunta actual: ").append(pregunta);
    return sb.toString();
  }

  private String llamarGemini(String systemPrompt, String userPrompt) throws Exception {
    String response = servicioGemini.llamar(systemPrompt, userPrompt);
    JsonObject root = JsonParser.parseString(response).getAsJsonObject();
    return root
      .getAsJsonArray("choices")
      .get(0)
      .getAsJsonObject()
      .getAsJsonObject("message")
      .get("content")
      .getAsString()
      .trim();
  }
}
