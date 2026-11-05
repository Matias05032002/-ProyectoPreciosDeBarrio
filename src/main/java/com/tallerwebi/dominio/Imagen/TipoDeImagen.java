package com.tallerwebi.dominio.Imagen;

// Reconoce el formato real de una imagen por sus primeros bytes, sin creer en el tipo que declara
// el cliente (que cualquiera puede falsificar)
public final class TipoDeImagen {

  public static final String JPEG = "image/jpeg";
  public static final String PNG = "image/png";
  public static final String WEBP = "image/webp";

  private static final int[] FIRMA_JPEG = { 0xFF, 0xD8, 0xFF };
  private static final int[] FIRMA_PNG = { 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
  private static final int[] FIRMA_RIFF = { 'R', 'I', 'F', 'F' };
  private static final int[] FIRMA_WEBP = { 'W', 'E', 'B', 'P' };
  private static final int INICIO_WEBP = 8;

  private TipoDeImagen() {}

  // Devuelve el tipo (image/jpeg, image/png o image/webp) o null si no es una imagen permitida
  public static String detectar(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    if (empiezaCon(bytes, 0, FIRMA_JPEG)) {
      return JPEG;
    }
    if (empiezaCon(bytes, 0, FIRMA_PNG)) {
      return PNG;
    }
    if (empiezaCon(bytes, 0, FIRMA_RIFF) && empiezaCon(bytes, INICIO_WEBP, FIRMA_WEBP)) {
      return WEBP;
    }
    return null;
  }

  private static boolean empiezaCon(byte[] bytes, int desde, int... firma) {
    if (bytes.length < desde + firma.length) {
      return false;
    }
    for (int i = 0; i < firma.length; i++) {
      if ((bytes[desde + i] & 0xFF) != firma[i]) {
        return false;
      }
    }
    return true;
  }
}
