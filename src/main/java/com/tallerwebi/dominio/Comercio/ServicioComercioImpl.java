package com.tallerwebi.dominio.Comercio;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ServicioComercio")
@Transactional
public class ServicioComercioImpl implements ServicioComercio {

  private RepositorioComercio repositorioComercio;

  @Autowired
  public ServicioComercioImpl(RepositorioComercio repositorioComercio) {
    this.repositorioComercio = repositorioComercio;
  }

  @Override
  public Comercio guardarComercio(Comercio comercio) {
    try {
      String direccionCompleta =
        comercio.getDireccion() + ", " + comercio.getLocalidad() + ", Buenos Aires, Argentina";
      String url =
        "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" +
        java.net.URLEncoder.encode(direccionCompleta, "UTF-8");

      java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url)
        .openConnection();
      conn.setRequestProperty("User-Agent", "TallerWebi/1.0");

      String response;
      try (
        java.util.Scanner scanner = new java.util.Scanner(
          conn.getInputStream(),
          java.nio.charset.StandardCharsets.UTF_8
        )
      ) {
        response = scanner.useDelimiter("\\A").next();
      }

      int latIdx = response.indexOf("\"lat\":\"") + 7;
      int latEnd = response.indexOf("\"", latIdx);
      int lonIdx = response.indexOf("\"lon\":\"") + 7;
      int lonEnd = response.indexOf("\"", lonIdx);

      comercio.setLatitud(Double.parseDouble(response.substring(latIdx, latEnd)));
      comercio.setLongitud(Double.parseDouble(response.substring(lonIdx, lonEnd)));
    } catch (Exception e) {
      comercio.setLatitud(null);
      comercio.setLongitud(null);
    }
    return repositorioComercio.guardarComercio(comercio);
  }

  @Override
  public List<Comercio> listarTodos() {
    return repositorioComercio.listarTodos();
  }

  @Override
  public Comercio buscarComercio(Long id) {
    return repositorioComercio.buscarComercio(id);
  }

  @Override
  public List<Comercio> buscarComercioPorNombre(String nombre) {
    return repositorioComercio.buscarComercioPorNombre(nombre);
  }
}
