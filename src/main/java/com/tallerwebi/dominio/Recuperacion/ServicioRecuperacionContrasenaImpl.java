package com.tallerwebi.dominio.Recuperacion;

import com.tallerwebi.config.VariablesDeEntorno;
import com.tallerwebi.dominio.Email.ServicioEmail;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.TokenDeRecuperacionInvalido;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioRecuperacionContrasena")
@Transactional
public class ServicioRecuperacionContrasenaImpl implements ServicioRecuperacionContrasena {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    ServicioRecuperacionContrasenaImpl.class
  );
  static final int MINUTOS_DE_VALIDEZ = 30;
  static final int MINUTOS_ENTRE_PEDIDOS = 2;
  private static final int BYTES_DEL_TOKEN = 32;
  private static final String URL_POR_DEFECTO = "http://localhost:8080/spring";
  private static final String ASUNTO = "Restablecé tu contraseña de Precios de Barrio";

  private final RepositorioTokenRecuperacion repositorioToken;
  private final RepositorioUsuario repositorioUsuario;
  private final ServicioLogin servicioLogin;
  private final ServicioEmail servicioEmail;
  private final Clock reloj;
  private final String urlBase;
  private final SecureRandom generador = new SecureRandom();

  @Autowired
  public ServicioRecuperacionContrasenaImpl(
    RepositorioTokenRecuperacion repositorioToken,
    RepositorioUsuario repositorioUsuario,
    ServicioLogin servicioLogin,
    ServicioEmail servicioEmail
  ) {
    this(
      repositorioToken,
      repositorioUsuario,
      servicioLogin,
      servicioEmail,
      Clock.systemDefaultZone(),
      VariablesDeEntorno.leer("APP_URL")
    );
  }

  // La dirección del link sale de la configuración y nunca del pedido HTTP: si se armara con el
  // encabezado Host, alguien podría hacer que el mail de otro vecino apunte a su propio sitio.
  ServicioRecuperacionContrasenaImpl(
    RepositorioTokenRecuperacion repositorioToken,
    RepositorioUsuario repositorioUsuario,
    ServicioLogin servicioLogin,
    ServicioEmail servicioEmail,
    Clock reloj,
    String urlBase
  ) {
    this.repositorioToken = repositorioToken;
    this.repositorioUsuario = repositorioUsuario;
    this.servicioLogin = servicioLogin;
    this.servicioEmail = servicioEmail;
    this.reloj = reloj;
    String base = urlBase == null ? URL_POR_DEFECTO : urlBase;
    this.urlBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
  }

  @Override
  public void solicitarRecuperacion(String email) {
    if (email == null || email.isBlank()) return;

    String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
    Usuario usuario = repositorioUsuario.buscar(emailNormalizado);
    // Al vecino siempre se le muestra lo mismo; el motivo real queda solo en la consola del servidor
    if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
      LOGGER.info(
        "Recuperación pedida para {}: no hay una cuenta activa con ese email",
        emailNormalizado
      );
      return;
    }

    LocalDateTime ahora = LocalDateTime.now(reloj);
    List<TokenRecuperacion> vigentes = repositorioToken.buscarVigentesDelUsuario(
      usuario.getId(),
      ahora
    );
    // Evita que alguien llene de mails la casilla de otro tocando el botón muchas veces
    if (hayUnPedidoReciente(vigentes, ahora)) {
      LOGGER.info(
        "Recuperación pedida para {}: ya se mandó un link hace menos de {} minutos",
        emailNormalizado,
        MINUTOS_ENTRE_PEDIDOS
      );
      return;
    }

    // Solo sirve el último link enviado
    for (TokenRecuperacion anterior : vigentes) {
      anterior.marcarUsado();
      repositorioToken.actualizar(anterior);
    }

    String token = generarToken();
    repositorioToken.guardar(
      new TokenRecuperacion(
        hashear(token),
        usuario.getId(),
        ahora,
        ahora.plusMinutes(MINUTOS_DE_VALIDEZ)
      )
    );
    LOGGER.info("Recuperación pedida para {}: link generado, se envía por mail", emailNormalizado);
    servicioEmail.enviar(usuario.getEmail(), ASUNTO, armarCuerpo(usuario, token));
  }

  @Override
  public boolean esTokenValido(String token) {
    return buscarVigente(token) != null;
  }

  @Override
  public void restablecerContrasena(String token, String contrasenaNueva, String confirmacion)
    throws ContrasenaInvalida {
    TokenRecuperacion vigente = buscarVigente(token);
    if (vigente == null) {
      throw new TokenDeRecuperacionInvalido();
    }
    if (contrasenaNueva == null || !contrasenaNueva.equals(confirmacion)) {
      throw new ContrasenaInvalida("Las contraseñas no coinciden");
    }

    servicioLogin.restablecerContrasena(vigente.getUsuarioId(), contrasenaNueva);

    vigente.marcarUsado();
    repositorioToken.actualizar(vigente);
  }

  private TokenRecuperacion buscarVigente(String token) {
    if (token == null || token.isBlank()) return null;
    TokenRecuperacion encontrado = repositorioToken.buscarPorHash(hashear(token.trim()));
    if (encontrado == null || !encontrado.estaVigente(LocalDateTime.now(reloj))) return null;
    return encontrado;
  }

  private boolean hayUnPedidoReciente(List<TokenRecuperacion> vigentes, LocalDateTime ahora) {
    LocalDateTime limite = ahora.minusMinutes(MINUTOS_ENTRE_PEDIDOS);
    return vigentes
      .stream()
      .anyMatch(token ->
        token.getFechaCreacion() != null && token.getFechaCreacion().isAfter(limite)
      );
  }

  private String generarToken() {
    byte[] bytes = new byte[BYTES_DEL_TOKEN];
    generador.nextBytes(bytes);
    // Base64 "url": solo letras, números, "-" y "_", así el token va en el link sin codificar
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  static String hashear(String token) {
    try {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(sha256.digest(token.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("La JVM no tiene SHA-256", e);
    }
  }

  private String armarCuerpo(Usuario usuario, String token) {
    String saludo = usuario.getNombre() == null || usuario.getNombre().isBlank()
      ? "Hola,"
      : "Hola " + usuario.getNombre().trim() + ",";
    String link = urlBase + "/restablecer-contrasena?token=" + token;
    return (
      saludo +
      "\n\n" +
      "Recibimos un pedido para restablecer la contraseña de tu cuenta en Precios de Barrio.\n" +
      "Para elegir una nueva, entrá a este link (vence en " +
      MINUTOS_DE_VALIDEZ +
      " minutos):\n\n" +
      link +
      "\n\n" +
      "Si no lo pediste vos, ignorá este mail: tu contraseña no cambia.\n\n" +
      "Precios de Barrio"
    );
  }
}
