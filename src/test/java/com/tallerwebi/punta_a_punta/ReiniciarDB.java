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
        "DELETE FROM Promocion; " +
        "DELETE FROM TokenRecuperacion; " +
        "DELETE FROM Reporte; " +
        "DELETE FROM Producto; " +
        "DELETE FROM Comercio; " +
        "DELETE FROM Usuario; " +
        "ALTER TABLE LISTA AUTO_INCREMENT = 1; " +
        "ALTER TABLE Promocion AUTO_INCREMENT = 1; " +
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

  // Le da un comercio (con ubicación) al usuario COMERCIO del seed, sin pasar por el mapa ni por Georef
  public static void sembrarComercioDelUsuarioComercio() {
    ejecutarSql(
      "INSERT INTO Comercio(nombre, direccion, descripcion, localidad, tipo, activo, latitud, longitud, fechaRegistro, usuarioId) " +
      "VALUES('Almacen Promo', 'Calle 456', 'desc', 'BA', 'ALMACEN', b'1', -34.6, -58.4, NOW(), 2);",
      "Comercio del usuario comercio sembrado exitosamente"
    );
  }

  // Deja un link de recuperación vigente (30 min) para el usuario indicado.
  // En la base solo se guarda el SHA-256 del token, igual que en la app.
  public static void sembrarTokenDeRecuperacion(int usuarioId, String tokenPlano) {
    // Los usuarios del seed no tienen nombre y la entidad lo exige (nullable = false): sin esto,
    // Hibernate rechaza el merge al cambiar la contraseña.
    ejecutarSql(
      "UPDATE Usuario SET nombre = 'Usuario Test' WHERE id = " +
      usuarioId +
      " AND nombre IS NULL; " +
      "INSERT INTO TokenRecuperacion(tokenHash, usuarioId, fechaCreacion, fechaVencimiento, usado) " +
      "VALUES('" +
      sha256Hex(tokenPlano) +
      "', " +
      usuarioId +
      ", NOW(), NOW() + INTERVAL 30 MINUTE, b'0');",
      "Token de recuperacion sembrado exitosamente"
    );
  }

  private static String sha256Hex(String texto) {
    try {
      java.security.MessageDigest sha256 = java.security.MessageDigest.getInstance("SHA-256");
      return java.util.HexFormat
        .of()
        .formatHex(sha256.digest(texto.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new IllegalStateException("La JVM no tiene SHA-256", e);
    }
  }

  private static void ejecutarSql(String sqlCommands, String mensajeOk) {
    try {
      String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
      String dbPort = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
      String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "tallerwebi";
      String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "user";
      String dbPassword = System.getenv("DB_PASSWORD") != null
        ? System.getenv("DB_PASSWORD")
        : "user";

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
        System.out.println(mensajeOk);
      } else {
        System.err.println("Error ejecutando SQL de prueba. Exit code: " + exitCode);
        new java.util.Scanner(process.getErrorStream())
          .useDelimiter("\\A")
          .forEachRemaining(System.err::println);
      }
    } catch (IOException | InterruptedException e) {
      System.err.println("Error sembrando datos: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // El reporte es del usuario 1, el mismo con el que se loguean los E2E
  public static void sembrarProductoConReporte() {
    sembrarProductoConReporteDelUsuario(1);
  }

  // El reporte es del usuario 2: sirve para probar acciones que solo se hacen sobre reportes ajenos
  public static void sembrarProductoConReporteDeOtroUsuario() {
    sembrarProductoConReporteDelUsuario(2);
  }

  private static void sembrarProductoConReporteDelUsuario(int usuarioIdDelReporte) {
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
        "VALUES(100.00, 8, NOW(), 1, 1, " +
        usuarioIdDelReporte +
        ");";

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
