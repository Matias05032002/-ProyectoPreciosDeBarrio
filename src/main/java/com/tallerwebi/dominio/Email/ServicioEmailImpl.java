package com.tallerwebi.dominio.Email;

import com.tallerwebi.config.VariablesDeEntorno;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Envía mails por el SMTP de Gmail con la cuenta de MAIL_USUARIO y la contraseña de aplicación de
 * MAIL_CLAVE (ambas en el .env). Si faltan, no envía nada y escribe el mail en el log: sirve para
 * probar en local sin cuenta de correo.
 */
@Service("servicioEmail")
public class ServicioEmailImpl implements ServicioEmail {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServicioEmailImpl.class);
  private static final String SERVIDOR_SMTP = "smtp.gmail.com";
  private static final String PUERTO_SMTP = "587";
  private static final String ESPERA_MAXIMA_MS = "10000";
  private static final String NOMBRE_REMITENTE = "Precios de Barrio";
  private static final String UTF_8 = StandardCharsets.UTF_8.name();

  private final String usuario;
  private final String clave;

  public ServicioEmailImpl() {
    this(VariablesDeEntorno.leer("MAIL_USUARIO"), VariablesDeEntorno.leer("MAIL_CLAVE"));
  }

  ServicioEmailImpl(String usuario, String clave) {
    this.usuario = usuario;
    // Google muestra la contraseña de aplicación en grupos de 4 con espacios: se aceptan igual
    this.clave = clave == null ? "" : clave.replace(" ", "");
  }

  public boolean estaConfigurado() {
    return usuario != null && !clave.isEmpty();
  }

  @Override
  public void enviar(String destinatario, String asunto, String cuerpo) {
    if (!estaConfigurado()) {
      LOGGER.warn(
        "MAIL_USUARIO o MAIL_CLAVE no están configurados: no se envía el mail a {}.\n{}\n{}",
        destinatario,
        asunto,
        cuerpo
      );
      return;
    }
    // En segundo plano: hablar con Gmail tarda segundos y la respuesta al usuario no depende de eso
    CompletableFuture.runAsync(() -> enviarAhora(destinatario, asunto, cuerpo));
  }

  private void enviarAhora(String destinatario, String asunto, String cuerpo) {
    try {
      MimeMessage mensaje = new MimeMessage(crearSesion());
      mensaje.setFrom(new InternetAddress(usuario, NOMBRE_REMITENTE, UTF_8));
      mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario, true));
      mensaje.setSubject(asunto, UTF_8);
      mensaje.setText(cuerpo, UTF_8);
      Transport.send(mensaje);
      LOGGER.info("Mail enviado a {}", destinatario);
    } catch (MessagingException | UnsupportedEncodingException e) {
      registrarError(destinatario, e);
    }
  }

  private void registrarError(String destinatario, Exception error) {
    LOGGER.error("No se pudo enviar el mail a {}", destinatario, error);
  }

  private Session crearSesion() {
    Properties propiedades = new Properties();
    propiedades.put("mail.smtp.host", SERVIDOR_SMTP);
    propiedades.put("mail.smtp.port", PUERTO_SMTP);
    propiedades.put("mail.smtp.auth", "true");
    propiedades.put("mail.smtp.starttls.enable", "true");
    propiedades.put("mail.smtp.starttls.required", "true");
    propiedades.put("mail.smtp.connectiontimeout", ESPERA_MAXIMA_MS);
    propiedades.put("mail.smtp.timeout", ESPERA_MAXIMA_MS);
    propiedades.put("mail.smtp.writetimeout", ESPERA_MAXIMA_MS);
    return Session.getInstance(
      propiedades,
      new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(usuario, clave);
        }
      }
    );
  }
}
