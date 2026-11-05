-- Datos de demostración para Precios de Barrio (MySQL).
-- Correr UNA sola vez sobre una base recién creada, después de arrancar la app una vez
-- (para que Hibernate cree las tablas):
--
--   docker exec -i tallerwebi-mysql mysql -u user -p<clave> tallerwebi < db/datos-demo.sql
--
-- Todos los usuarios tienen la contraseña "test". No usar nunca en producción.

SET @clave = '$2a$10$PBL5JMWUB3K.CaSCXa.VQ.hqBGTDm0DhQcj6h5YcpOxD.ntfvyUkm';

-- Vecinos y dueños de comercio
INSERT IGNORE INTO Usuario(email, password, rol, activo, verificado, nombre, apellido, localidad) VALUES
  ('vecina@demo.com',   @clave, 'USER',     b'1', b'0', 'Lucía',  'Gómez',  'La Matanza'),
  ('vecino@demo.com',   @clave, 'USER',     b'1', b'0', 'Martín', 'Pérez',  'La Matanza'),
  ('almacen@demo.com',  @clave, 'COMERCIO', b'1', b'0', 'Almacén Don José', NULL, 'La Matanza'),
  ('verduleria@demo.com', @clave, 'COMERCIO', b'1', b'0', 'Verdulería La Esquina', NULL, 'La Matanza');

SET @vecina = (SELECT id FROM Usuario WHERE email = 'vecina@demo.com');
SET @vecino = (SELECT id FROM Usuario WHERE email = 'vecino@demo.com');
SET @dueno1 = (SELECT id FROM Usuario WHERE email = 'almacen@demo.com');
SET @dueno2 = (SELECT id FROM Usuario WHERE email = 'verduleria@demo.com');

-- Comercios (dirección + localidad son únicas en conjunto)
INSERT IGNORE INTO Comercio(nombre, direccion, descripcion, localidad, tipo, activo, verificado, solicitud_verificacion, latitud, longitud, fechaRegistro, usuarioId) VALUES
  ('Almacén Don José', 'Rivadavia 1234', 'Almacén de barrio con fiambres y limpieza', 'La Matanza', 'Almacen', b'1', b'1', b'0', -34.6700, -58.5620, NOW(), @dueno1),
  ('Verdulería La Esquina', 'Arieta 2500', 'Frutas y verduras de estación', 'La Matanza', 'Verduleria', b'1', b'0', b'1', -34.6690, -58.5600, NOW(), @dueno2);

SET @almacen = (SELECT id FROM Comercio WHERE direccion = 'Rivadavia 1234' AND localidad = 'La Matanza');
SET @verduleria = (SELECT id FROM Comercio WHERE direccion = 'Arieta 2500' AND localidad = 'La Matanza');

-- Productos (nombre + marca + unidad son únicos en conjunto)
INSERT IGNORE INTO Producto(nombre, marca, categoria, descripcion, unidad, precio, disponible, fechaDeSubida) VALUES
  ('Leche entera', 'La Serenísima', 'Lácteos',   'Leche entera larga vida', '1l',   1150.0, b'1', NOW()),
  ('Aceite de girasol', 'Cocinero', 'Almacén',   'Aceite de girasol',       '900ml', 2320.0, b'1', NOW()),
  ('Arroz largo fino', 'Gallo', 'Almacén',       'Arroz largo fino',        '1kg',  1480.0, b'1', NOW()),
  ('Tomate', 'Sin marca', 'Verdulería',          'Tomate redondo',          '1kg',  1900.0, b'1', NOW());

SET @leche  = (SELECT id FROM Producto WHERE nombre = 'Leche entera' AND unidad = '1l');
SET @aceite = (SELECT id FROM Producto WHERE nombre = 'Aceite de girasol' AND unidad = '900ml');
SET @arroz  = (SELECT id FROM Producto WHERE nombre = 'Arroz largo fino' AND unidad = '1kg');
SET @tomate = (SELECT id FROM Producto WHERE nombre = 'Tomate' AND unidad = '1kg');

-- Reportes de precios de los últimos días (puntuación 0 = nadie los marcó como dudosos)
INSERT INTO Reporte(precio, puntuacion, fechaDeReporte, comercio_id, producto_id, usuario_id) VALUES
  (1150.00, 0, NOW() - INTERVAL 1 HOUR,  @almacen,    @leche,  @vecina),
  (1190.00, 0, NOW() - INTERVAL 1 DAY,   @almacen,    @leche,  @vecino),
  (2320.00, 0, NOW() - INTERVAL 2 HOUR,  @almacen,    @aceite, @vecina),
  (1480.00, 0, NOW() - INTERVAL 3 DAY,   @almacen,    @arroz,  @vecino),
  (1900.00, 0, NOW() - INTERVAL 5 HOUR,  @verduleria, @tomate, @vecina),
  (  99.00, 7, NOW() - INTERVAL 1 DAY,   @almacen,    @aceite, @vecino);  -- este queda como dudoso

-- Una promoción vigente y una que empieza mañana ("Próximamente")
INSERT INTO Promocion(titulo, descripcion, tipo, productoNombre, precioPromocional, metodoPago, porcentajeDescuento, fechaInicio, fechaFin, activa, comercio_id) VALUES
  ('2x1 en leche', 'Llevás dos y pagás uno', 'PRODUCTO', 'Leche entera 1l', 1150.0, NULL, NULL, NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 7 DAY, b'1', @almacen),
  ('20% con débito', 'Todos los martes', 'METODO_PAGO', NULL, NULL, 'DEBITO', 20, NOW() + INTERVAL 1 DAY, NOW() + INTERVAL 8 DAY, b'1', @almacen);
