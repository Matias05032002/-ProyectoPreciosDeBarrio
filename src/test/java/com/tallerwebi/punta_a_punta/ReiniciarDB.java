package com.tallerwebi.punta_a_punta;

import java.io.IOException;

public class ReiniciarDB {

  public static void limpiarBaseDeDatos() {
    try {
      String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
      String dbPort = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
      String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "tallerwebi";
      String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "user";
      String dbPassword = System.getenv("DB_PASSWORD") != null
        ? System.getenv("DB_PASSWORD")
        : "user";

      // BCrypt hash de "test"
      String hashTest = "$2a$10$PBL5JMWUB3K.CaSCXa.VQ.hqBGTDm0DhQcj6h5YcpOxD.ntfvyUkm";
      String hashComercio = "$2a$10$PBL5JMWUB3K.CaSCXa.VQ.hqBGTDm0DhQcj6h5YcpOxD.ntfvyUkm";

      String sqlCommands =
        "SET FOREIGN_KEY_CHECKS=0; " +
        "DELETE FROM ITEM_LISTA; " +
        "DELETE FROM LISTA; " +
        "DELETE FROM reporte_usuarios_dudoso; " +
        "DELETE FROM Reporte; " +
        "DELETE FROM Producto; " +
        "DELETE FROM Comercio; " +
        "DELETE FROM Usuario; " +
        "ALTER TABLE LISTA AUTO_INCREMENT = 1; " +
        "ALTER TABLE ITEM_LISTA AUTO_INCREMENT = 1; " +
        "ALTER TABLE Reporte AUTO_INCREMENT = 1; " +
        "ALTER TABLE Producto AUTO_INCREMENT = 1; " +
        "ALTER TABLE Comercio AUTO_INCREMENT = 1; " +
        "ALTER TABLE Usuario AUTO_INCREMENT = 1; " +
        "SET FOREIGN_KEY_CHECKS=1; " +
        "INSERT INTO Usuario(id, email, password, rol, activo) VALUES(null, 'test@unlam.edu.ar', '" +
        hashTest +
        "', 'ADMIN', b'1'); " +
        "INSERT INTO Usuario(id, email, password, rol, activo) VALUES(null, 'test-comercio@unlam.edu.ar', '" +
        hashComercio +
        "', 'COMERCIO', b'1');";

      String dockerCmd = String.format(
        "docker exec tallerwebi-mysql mysql -h %s -P %s -u %s -p%s %s -e \"%s\"",
        dbHost,
        dbPort,
        dbUser,
        dbPassword,
        dbName,
        sqlCommands
      );

      String[] comando;
      if (System.getProperty("os.name").toLowerCase().contains("win")) {
        comando = new String[] { "cmd.exe", "/c", dockerCmd };
      } else {
        comando = new String[] { "/bin/bash", "-c", dockerCmd };
      }

      Process process = Runtime.getRuntime().exec(comando);
      int exitCode = process.waitFor();

      if (exitCode == 0) {
        System.out.println("Base de datos limpiada exitosamente");
      } else {
        System.err.println("Error al limpiar la base de datos. Exit code: " + exitCode);
      }
    } catch (IOException | InterruptedException e) {
      System.err.println("Error ejecutando script de limpieza: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void sembrarListaConItem() {
    try {
      String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
      String dbPort = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
      String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "tallerwebi";
      String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "user";
      String dbPassword = System.getenv("DB_PASSWORD") != null
        ? System.getenv("DB_PASSWORD")
        : "user";

      String sqlCommands =
        "INSERT INTO Comercio(nombre, direccion, descripcion, localidad, tipo, activo, latitud, longitud, fechaRegistro, usuarioId) " +
        "VALUES('Almacen Test', 'Calle 123', 'desc', 'BA', 'ALMACEN', b'1', -34.6, -58.4, NOW(), 1); " +
        "INSERT INTO Producto(nombre, marca, categoria, descripcion, unidad, precio, disponible, fechaDeSubida) " +
        "VALUES('Leche', 'La Serenisima', 'Lacteos', 'Leche entera', '1L', 100.0, b'1', NOW()); " +
        "INSERT INTO Reporte(precio, puntuacion, fechaDeReporte, comercio_id, producto_id, usuario_id) " +
        "VALUES(100.00, 8, NOW(), 1, 1, 1); " +
        "INSERT INTO LISTA(nombre, usuarioId, fechaCreacion) VALUES('Lista con item', 1, NOW()); " +
        "INSERT INTO ITEM_LISTA(lista_id, reporte_id, cantidad) VALUES(1, 1, 1);";

      String dockerCmd = String.format(
        "docker exec tallerwebi-mysql mysql -h %s -P %s -u %s -p%s %s -e \"%s\"",
        dbHost,
        dbPort,
        dbUser,
        dbPassword,
        dbName,
        sqlCommands
      );

      String[] comando;
      if (System.getProperty("os.name").toLowerCase().contains("win")) {
        comando = new String[] { "cmd.exe", "/c", dockerCmd };
      } else {
        comando = new String[] { "/bin/bash", "-c", dockerCmd };
      }

      Process process = Runtime.getRuntime().exec(comando);
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        System.out.println("Lista con item sembrada exitosamente");
      } else {
        System.err.println("Error al sembrar lista con item. Exit code: " + exitCode);
        new java.util.Scanner(process.getErrorStream())
          .useDelimiter("\\A")
          .forEachRemaining(System.err::println);
      }
    } catch (IOException | InterruptedException e) {
      System.err.println("Error sembrando datos: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void sembrarProductoConReporte() {
    try {
      String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
      String dbPort = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
      String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "tallerwebi";
      String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "user";
      String dbPassword = System.getenv("DB_PASSWORD") != null
        ? System.getenv("DB_PASSWORD")
        : "user";

      String sqlCommands =
        "INSERT INTO Comercio(nombre, direccion, descripcion, localidad, tipo, activo, latitud, longitud, fechaRegistro, usuarioId) " +
        "VALUES('Almacen Test', 'Calle 123', 'desc', 'BA', 'ALMACEN', b'1', -34.6, -58.4, NOW(), 1); " +
        "INSERT INTO Producto(nombre, marca, categoria, descripcion, unidad, precio, disponible, fechaDeSubida) " +
        "VALUES('Leche', 'La Serenisima', 'Lacteos', 'Leche entera', '1L', 100.0, b'1', NOW()); " +
        "INSERT INTO Reporte(precio, puntuacion, fechaDeReporte, comercio_id, producto_id, usuario_id) " +
        "VALUES(100.00, 8, NOW(), 1, 1, 1);";

      String dockerCmd = String.format(
        "docker exec tallerwebi-mysql mysql -h %s -P %s -u %s -p%s %s -e \"%s\"",
        dbHost,
        dbPort,
        dbUser,
        dbPassword,
        dbName,
        sqlCommands
      );

      String[] comando;
      if (System.getProperty("os.name").toLowerCase().contains("win")) {
        comando = new String[] { "cmd.exe", "/c", dockerCmd };
      } else {
        comando = new String[] { "/bin/bash", "-c", dockerCmd };
      }

      Process process = Runtime.getRuntime().exec(comando);
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        System.out.println("Producto con reporte sembrado exitosamente");
      } else {
        System.err.println("Error al sembrar producto. Exit code: " + exitCode);
        new java.util.Scanner(process.getErrorStream())
          .useDelimiter("\\A")
          .forEachRemaining(System.err::println);
      }
    } catch (IOException | InterruptedException e) {
      System.err.println("Error sembrando datos: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
