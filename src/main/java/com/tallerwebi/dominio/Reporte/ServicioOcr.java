package com.tallerwebi.dominio.Reporte;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

@Service
public class ServicioOcr {

  private static final String TESSERACT_PATH = "C:\\Program Files\\Tesseract-OCR\\tessdata";

  public List<ItemTicket> extraerProductosDeTicket(byte[] imagenBytes) throws TesseractException {
    BufferedImage imagen;
    try {
      imagen = ImageIO.read(new ByteArrayInputStream(imagenBytes));
    } catch (Exception e) {
      return new ArrayList<>();
    }
    if (imagen == null) {
      return new ArrayList<>();
    }
    Tesseract tesseract = new Tesseract();
    tesseract.setDatapath(TESSERACT_PATH);
    tesseract.setLanguage("spa+eng");
    String texto = tesseract.doOCR(imagen);
    return parsearTexto(texto);
  }

  List<ItemTicket> parsearTexto(String texto) {
    List<ItemTicket> items = new ArrayList<>();
    String[] lineas = texto.split("\n");
    Pattern patron = Pattern.compile("(.+?)\\s+(\\d+[.,]\\d{2})\\s*$");

    for (String lineaOriginal : lineas) {
      String linea = lineaOriginal.trim();
      if (linea.isEmpty()) continue;
      Matcher matcher = patron.matcher(linea);
      if (matcher.find()) {
        String nombre = matcher.group(1).trim();
        String precioStr = matcher.group(2).replace(",", ".");
        try {
          double precio = Double.parseDouble(precioStr);
          if (precio > 0 && precio < 100000) {
            items.add(new ItemTicket(nombre, precio));
          }
        } catch (NumberFormatException ex) {
          items.add(new ItemTicket(nombre, 0));
        }
      }
    }
    return items;
  }
}
