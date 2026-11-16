-- Datos de demostración de Precios de Barrio (San Justo / La Matanza).
-- Reportes: precios publicados en noviembre 2026 por cada cadena (ver FUENTES_PRECIOS.md).
-- Corre sobre una base ya creada por la app (Hibernate). Es seguro repetirlo: no duplica filas.
-- Contraseña de todas las cuentas de demostración: Barrio2026
SET NAMES utf8mb4;
SET @ahora = NOW();

-- 1) Usuarios
INSERT INTO Usuario(email,password,rol,activo,verificado,nombre,apellido,fechaNacimiento,localidad)
SELECT 'lucia.fernandez@example.com','$2a$10$fxfCv5IssqwcqONNkJVb0.mUPFXQREgcG1HI2Q95vF0IE3fLZJhum','USER',b'1',b'1','Lucía','Fernández','1996-03-14','La Matanza' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email='lucia.fernandez@example.com');
INSERT INTO Usuario(email,password,rol,activo,verificado,nombre,apellido,fechaNacimiento,localidad)
SELECT 'martin.gomez@example.com','$2a$10$fxfCv5IssqwcqONNkJVb0.mUPFXQREgcG1HI2Q95vF0IE3fLZJhum','USER',b'1',b'1','Martín','Gómez','1991-07-22','Morón' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email='martin.gomez@example.com');
INSERT INTO Usuario(email,password,rol,activo,verificado,nombre,apellido,fechaNacimiento,localidad)
SELECT 'carolina.rossi@example.com','$2a$10$fxfCv5IssqwcqONNkJVb0.mUPFXQREgcG1HI2Q95vF0IE3fLZJhum','USER',b'1',b'1','Carolina','Rossi','1988-11-05','Lomas de Zamora' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email='carolina.rossi@example.com');
INSERT INTO Usuario(email,password,rol,activo,verificado,nombre,apellido,fechaNacimiento,localidad)
SELECT 'diego.benitez@example.com','$2a$10$fxfCv5IssqwcqONNkJVb0.mUPFXQREgcG1HI2Q95vF0IE3fLZJhum','USER',b'1',b'1','Diego','Benítez','2000-01-30','La Matanza' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email='diego.benitez@example.com');
INSERT INTO Usuario(email,password,rol,activo,verificado,nombre,apellido,fechaNacimiento,localidad)
SELECT 'carrefour.express.sanjusto@example.com','$2a$10$fxfCv5IssqwcqONNkJVb0.mUPFXQREgcG1HI2Q95vF0IE3fLZJhum','COMERCIO',b'1',b'1','Carrefour Express','San Justo','1985-05-20','La Matanza' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email='carrefour.express.sanjusto@example.com');
INSERT INTO Usuario(email,password,rol,activo,verificado,nombre,apellido,fechaNacimiento,localidad)
SELECT 'dia.sanjusto@example.com','$2a$10$fxfCv5IssqwcqONNkJVb0.mUPFXQREgcG1HI2Q95vF0IE3fLZJhum','COMERCIO',b'1',b'1','Supermercados DIA','San Justo','1985-05-20','La Matanza' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email='dia.sanjusto@example.com');
INSERT INTO Usuario(email,password,rol,activo,verificado,nombre,apellido,fechaNacimiento,localidad)
SELECT 'changomas.sanjusto@example.com','$2a$10$fxfCv5IssqwcqONNkJVb0.mUPFXQREgcG1HI2Q95vF0IE3fLZJhum','COMERCIO',b'1',b'1','ChangoMás','San Justo','1985-05-20','La Matanza' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email='changomas.sanjusto@example.com');

-- 2) Comercios (direcciones reales)
INSERT INTO Comercio(nombre,direccion,descripcion,localidad,tipo,activo,verificado,solicitud_verificacion,fechaRegistro,latitud,longitud,usuarioId)
SELECT 'Carrefour Express San Justo','Dr. Ignacio Arieta 2850','Supermercado de cercanía en San Justo. Almacén, lácteos, frescos, limpieza y perfumería con precios de góndola de Carrefour.','La Matanza','Supermercado',b'1',b'1',b'0',DATE_SUB(@ahora, INTERVAL 40 DAY),-34.676217,-58.563547,(SELECT id FROM Usuario WHERE email='carrefour.express.sanjusto@example.com') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza');
INSERT INTO Comercio(nombre,direccion,descripcion,localidad,tipo,activo,verificado,solicitud_verificacion,fechaRegistro,latitud,longitud,usuarioId)
SELECT 'Supermercados DIA San Justo','Av. Brig. Gral. Juan Manuel de Rosas 3634','Sucursal de DIA sobre Av. Rosas. Marca propia, ofertas semanales y descuentos con bancos y billeteras virtuales.','La Matanza','Supermercado',b'1',b'1',b'0',DATE_SUB(@ahora, INTERVAL 38 DAY),-34.6831474,-58.5550613,(SELECT id FROM Usuario WHERE email='dia.sanjusto@example.com') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza');
INSERT INTO Comercio(nombre,direccion,descripcion,localidad,tipo,activo,verificado,solicitud_verificacion,fechaRegistro,latitud,longitud,usuarioId)
SELECT 'ChangoMás San Justo','Av. Brig. Gral. Juan Manuel de Rosas 3900','Hipermercado en San Justo, cerca de la UNLaM. Se sumó a Precios de Barrio y carga sus propios precios.','La Matanza','Supermercado',b'1',b'0',b'1',DATE_SUB(@ahora, INTERVAL 6 DAY),-34.6860172,-58.5559192,(SELECT id FROM Usuario WHERE email='changomas.sanjusto@example.com') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza');
INSERT INTO Comercio(nombre,direccion,descripcion,localidad,tipo,activo,verificado,solicitud_verificacion,fechaRegistro,latitud,longitud,usuarioId)
SELECT 'Vea Villa Madero','Av. Vélez Sarsfield 567','Supermercado Vea en Villa Madero.','La Matanza','Supermercado',b'1',b'0',b'0',DATE_SUB(@ahora, INTERVAL 0 DAY),-34.6926309,-58.5004601,NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza');
INSERT INTO Comercio(nombre,direccion,descripcion,localidad,tipo,activo,verificado,solicitud_verificacion,fechaRegistro,latitud,longitud,usuarioId)
SELECT 'Disco Lomas de Zamora','Av. Meeks 256','Supermercado Disco sobre Av. Meeks.','Lomas de Zamora','Supermercado',b'1',b'0',b'0',DATE_SUB(@ahora, INTERVAL 0 DAY),-34.7638342,-58.3985223,NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora');

-- 3) Horarios (1 = lunes ... 7 = domingo)
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza'),1,'07:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza') AND diaSemana=1);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza'),2,'07:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza') AND diaSemana=2);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza'),3,'07:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza') AND diaSemana=3);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza'),4,'07:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza') AND diaSemana=4);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza'),5,'07:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza') AND diaSemana=5);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza'),6,'07:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza') AND diaSemana=6);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza'),7,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza') AND diaSemana=7);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza'),1,'08:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza') AND diaSemana=1);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza'),2,'08:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza') AND diaSemana=2);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza'),3,'08:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza') AND diaSemana=3);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza'),4,'08:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza') AND diaSemana=4);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza'),5,'08:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza') AND diaSemana=5);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza'),6,'08:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza') AND diaSemana=6);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza'),7,'08:00:00','21:30:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza') AND diaSemana=7);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza'),1,'08:00:00','22:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza') AND diaSemana=1);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza'),2,'08:00:00','22:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza') AND diaSemana=2);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza'),3,'08:00:00','22:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza') AND diaSemana=3);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza'),4,'08:00:00','22:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza') AND diaSemana=4);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza'),5,'08:00:00','22:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza') AND diaSemana=5);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza'),6,'08:00:00','22:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza') AND diaSemana=6);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza'),7,'08:00:00','22:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza') AND diaSemana=7);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza'),1,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza') AND diaSemana=1);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza'),2,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza') AND diaSemana=2);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza'),3,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza') AND diaSemana=3);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza'),4,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza') AND diaSemana=4);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza'),5,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza') AND diaSemana=5);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza'),6,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza') AND diaSemana=6);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza'),7,'09:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Vélez Sarsfield 567' AND localidad='La Matanza') AND diaSemana=7);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora'),1,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora') AND diaSemana=1);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora'),2,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora') AND diaSemana=2);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora'),3,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora') AND diaSemana=3);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora'),4,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora') AND diaSemana=4);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora'),5,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora') AND diaSemana=5);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora'),6,'08:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora') AND diaSemana=6);
INSERT INTO HorarioComercio(comercioId,diaSemana,horaApertura,horaCierre,abierto)
SELECT (SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora'),7,'09:00:00','21:00:00',b'1' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM HorarioComercio WHERE comercioId=(SELECT id FROM Comercio WHERE direccion='Av. Meeks 256' AND localidad='Lomas de Zamora') AND diaSemana=7);

-- 4) Productos
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Aceite de girasol','Cañuelas','Almacen','1.5l','Aceite de girasol Cañuelas 1.5l',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Aceite de girasol' AND marca='Cañuelas' AND unidad='1.5l');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Aceite de girasol','Cañuelas','Almacen','900ml','Aceite de girasol Cañuelas 900ml',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Aceite de girasol' AND marca='Cañuelas' AND unidad='900ml');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Aceite de girasol','Cocinero','Almacen','1.5l','Aceite de girasol Cocinero 1.5l',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Aceite de girasol' AND marca='Cocinero' AND unidad='1.5l');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Aceite de girasol','Natura','Almacen','1.5l','Aceite de girasol Natura 1.5l',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Aceite de girasol' AND marca='Natura' AND unidad='1.5l');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Arroz integral','Lucchetti','Cereales y legumbres','1kg','Arroz integral Lucchetti 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Arroz integral' AND marca='Lucchetti' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Arroz largo fino','Máximo','Cereales y legumbres','500g','Arroz largo fino Máximo 500g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Arroz largo fino' AND marca='Máximo' AND unidad='500g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Arroz parboil','Lucchetti','Cereales y legumbres','1kg','Arroz parboil Lucchetti 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Arroz parboil' AND marca='Lucchetti' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Arroz parboil','Lucchetti','Cereales y legumbres','500g','Arroz parboil Lucchetti 500g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Arroz parboil' AND marca='Lucchetti' AND unidad='500g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Azúcar','Azucel','Almacen','1kg','Azúcar Azucel 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Azúcar' AND marca='Azucel' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Azúcar común','Bulnez','Almacen','1kg','Azúcar común Bulnez 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Azúcar común' AND marca='Bulnez' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Azúcar común tipo A','DIA','Almacen','1kg','Azúcar común tipo A DIA 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Azúcar común tipo A' AND marca='DIA' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Azúcar refinado superior','Ledesma','Almacen','1kg','Azúcar refinado superior Ledesma 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Azúcar refinado superior' AND marca='Ledesma' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Café suave y espumoso','Arlistan','Almacen','170g','Café suave y espumoso Arlistan 170g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Café suave y espumoso' AND marca='Arlistan' AND unidad='170g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Café suave','Bonafide','Almacen','125g','Café suave Bonafide 125g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Café suave' AND marca='Bonafide' AND unidad='125g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Café instantáneo Tradición','Nescafé','Almacen','100g','Café instantáneo Tradición Nescafé 100g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Café instantáneo Tradición' AND marca='Nescafé' AND unidad='100g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Fideos bucattini','Matarazzo','Almacen','500g','Fideos bucattini Matarazzo 500g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Fideos bucattini' AND marca='Matarazzo' AND unidad='500g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Fideos coditos','Lucchetti','Almacen','500g','Fideos coditos Lucchetti 500g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Fideos coditos' AND marca='Lucchetti' AND unidad='500g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Fideos coditos','Matarazzo','Almacen','500g','Fideos coditos Matarazzo 500g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Fideos coditos' AND marca='Matarazzo' AND unidad='500g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Fideos tirabuzón','Cuquets','Almacen','500g','Fideos tirabuzón Cuquets 500g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Fideos tirabuzón' AND marca='Cuquets' AND unidad='500g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Fideos dedalitos','Lucchetti','Almacen','500g','Fideos dedalitos Lucchetti 500g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Fideos dedalitos' AND marca='Lucchetti' AND unidad='500g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Fideos moños','Lucchetti','Almacen','500g','Fideos moños Lucchetti 500g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Fideos moños' AND marca='Lucchetti' AND unidad='500g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Galletitas de granola','Cerealitas','Snacks y golosinas','243g','Galletitas de granola Cerealitas 243g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Galletitas de granola' AND marca='Cerealitas' AND unidad='243g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Galletitas de chocolate','Chocolina','Snacks y golosinas','250g','Galletitas de chocolate Chocolina 250g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Galletitas de chocolate' AND marca='Chocolina' AND unidad='250g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Galletitas Opera','Opera','Snacks y golosinas','55g','Galletitas Opera Opera 55g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Galletitas Opera' AND marca='Opera' AND unidad='55g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Galletitas vainilla','Porteñitas','Snacks y golosinas','139g','Galletitas vainilla Porteñitas 139g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Galletitas vainilla' AND marca='Porteñitas' AND unidad='139g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Galletitas Traviata','Traviata','Snacks y golosinas','540g','Galletitas Traviata Traviata 540g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Galletitas Traviata' AND marca='Traviata' AND unidad='540g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Harina 000','Caserita','Almacen','1kg','Harina 000 Caserita 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Harina 000' AND marca='Caserita' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Harina leudante','Chacabuco','Almacen','1kg','Harina leudante Chacabuco 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Harina leudante' AND marca='Chacabuco' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Harina de fuerza','Morixe','Almacen','1kg','Harina de fuerza Morixe 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Harina de fuerza' AND marca='Morixe' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Harina 0000','Morixe','Almacen','1kg','Harina 0000 Morixe 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Harina 0000' AND marca='Morixe' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Harina integral','Pureza','Almacen','1kg','Harina integral Pureza 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Harina integral' AND marca='Pureza' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Leche ultrapasteurizada 2%','DIA','Lacteos','1l','Leche ultrapasteurizada 2% DIA 1l',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Leche ultrapasteurizada 2%' AND marca='DIA' AND unidad='1l');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Leche entera sachet','La Serenísima','Lacteos','1l','Leche entera sachet La Serenísima 1l',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Leche entera sachet' AND marca='La Serenísima' AND unidad='1l');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Leche parcialmente descremada','Manfrey','Lacteos','1l','Leche parcialmente descremada Manfrey 1l',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Leche parcialmente descremada' AND marca='Manfrey' AND unidad='1l');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Leche protein','La Serenísima','Lacteos','1l','Leche protein La Serenísima 1l',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Leche protein' AND marca='La Serenísima' AND unidad='1l');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Manteca','Ilolay','Lacteos','100g','Manteca Ilolay 100g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Manteca' AND marca='Ilolay' AND unidad='100g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Manteca extra calidad','Ilolay','Lacteos','200g','Manteca extra calidad Ilolay 200g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Manteca extra calidad' AND marca='Ilolay' AND unidad='200g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Manteca clásica','La Serenísima','Lacteos','100g','Manteca clásica La Serenísima 100g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Manteca clásica' AND marca='La Serenísima' AND unidad='100g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Pan lactal blanco','Lactal','Panaderia y reposteria','315g','Pan lactal blanco Lactal 315g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Pan lactal blanco' AND marca='Lactal' AND unidad='315g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Pan lactal blanco','Lactal','Panaderia y reposteria','550g','Pan lactal blanco Lactal 550g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Pan lactal blanco' AND marca='Lactal' AND unidad='550g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Pan de pancho x6','Lactal','Panaderia y reposteria','6u','Pan de pancho x6 Lactal 6u',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Pan de pancho x6' AND marca='Lactal' AND unidad='6u');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Yerba mate tradicional','Amanda','Almacen','1kg','Yerba mate tradicional Amanda 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Yerba mate tradicional' AND marca='Amanda' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Yerba mate tradicional','Amanda','Almacen','500g','Yerba mate tradicional Amanda 500g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Yerba mate tradicional' AND marca='Amanda' AND unidad='500g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Yerba mate tradicional','Cachamai','Almacen','1kg','Yerba mate tradicional Cachamai 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Yerba mate tradicional' AND marca='Cachamai' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Yerba mate','Chamigo','Almacen','500g','Yerba mate Chamigo 500g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Yerba mate' AND marca='Chamigo' AND unidad='500g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Yerba mate suave','Playadito','Almacen','1kg','Yerba mate suave Playadito 1kg',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Yerba mate suave' AND marca='Playadito' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Yerba mate suave','Playadito','Almacen','500g','Yerba mate suave Playadito 500g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Yerba mate suave' AND marca='Playadito' AND unidad='500g');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Yerba mate liviana','Unión','Almacen','500g','Yerba mate liviana Unión 500g',NULL,b'1',DATE_SUB(@ahora, INTERVAL 30 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Yerba mate liviana' AND marca='Unión' AND unidad='500g');

-- 5) Reportes
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 1 HOUR),0,2430.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Leche protein' AND p.marca='La Serenísima' AND p.unidad='1l'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 6 HOUR),0,3229.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Yerba mate liviana' AND p.marca='Unión' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 11 HOUR),0,5069.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Yerba mate tradicional' AND p.marca='Amanda' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 5 HOUR),0,6985.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Aceite de girasol' AND p.marca='Cañuelas' AND p.unidad='1.5l'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 10 HOUR),0,2489.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Arroz parboil' AND p.marca='Lucchetti' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 4 HOUR),0,1315.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Arroz parboil' AND p.marca='Lucchetti' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 9 HOUR),0,1849.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Fideos coditos' AND p.marca='Lucchetti' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 3 HOUR),0,1849.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Fideos dedalitos' AND p.marca='Lucchetti' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 8 HOUR),0,2199.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Fideos bucattini' AND p.marca='Matarazzo' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 2 HOUR),0,1390.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Azúcar común' AND p.marca='Bulnez' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 7 HOUR),0,2149.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Harina integral' AND p.marca='Pureza' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 1 HOUR),0,2949.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Manteca clásica' AND p.marca='La Serenísima' AND p.unidad='100g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 6 HOUR),0,4905.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Manteca extra calidad' AND p.marca='Ilolay' AND p.unidad='200g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 11 HOUR),0,2340.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Pan lactal blanco' AND p.marca='Lactal' AND p.unidad='315g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 5 HOUR),0,3000.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Galletitas de granola' AND p.marca='Cerealitas' AND p.unidad='243g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 10 HOUR),0,2225.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Leche protein' AND p.marca='La Serenísima' AND p.unidad='1l'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 4 HOUR),0,1965.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Leche ultrapasteurizada 2%' AND p.marca='DIA' AND p.unidad='1l'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 9 HOUR),0,1990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Leche parcialmente descremada' AND p.marca='Manfrey' AND p.unidad='1l'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 3 HOUR),0,4550.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Yerba mate suave' AND p.marca='Playadito' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 8 HOUR),0,2790.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Yerba mate suave' AND p.marca='Playadito' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 2 HOUR),0,1950.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Yerba mate' AND p.marca='Chamigo' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 7 HOUR),0,6925.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Aceite de girasol' AND p.marca='Cocinero' AND p.unidad='1.5l'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 1 HOUR),0,750.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Arroz largo fino' AND p.marca='Máximo' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 6 HOUR),0,690.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Fideos tirabuzón' AND p.marca='Cuquets' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 11 HOUR),0,1619.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Azúcar refinado superior' AND p.marca='Ledesma' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 5 HOUR),0,995.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Azúcar común tipo A' AND p.marca='DIA' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 10 HOUR),0,1100.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Harina 000' AND p.marca='Caserita' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 4 HOUR),0,2615.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Manteca' AND p.marca='Ilolay' AND p.unidad='100g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 9 HOUR),0,5265.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Pan lactal blanco' AND p.marca='Lactal' AND p.unidad='550g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 3 HOUR),0,1491.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Galletitas vainilla' AND p.marca='Porteñitas' AND p.unidad='139g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 8 HOUR),0,3650.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Café suave' AND p.marca='Bonafide' AND p.unidad='125g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 2 HOUR),0,4399.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='changomas.sanjusto@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Aceite de girasol' AND p.marca='Cañuelas' AND p.unidad='900ml'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 7 HOUR),0,5899.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='changomas.sanjusto@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Aceite de girasol' AND p.marca='Cañuelas' AND p.unidad='1.5l'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 1 HOUR),0,2199.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='changomas.sanjusto@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Fideos bucattini' AND p.marca='Matarazzo' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 6 HOUR),0,1479.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='changomas.sanjusto@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Azúcar' AND p.marca='Azucel' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 11 HOUR),0,1860.65,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='changomas.sanjusto@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Harina 0000' AND p.marca='Morixe' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 5 HOUR),0,1579.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='changomas.sanjusto@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Harina leudante' AND p.marca='Chacabuco' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 10 HOUR),0,2019.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='changomas.sanjusto@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Harina integral' AND p.marca='Pureza' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 4 HOUR),0,1869.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='changomas.sanjusto@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Harina de fuerza' AND p.marca='Morixe' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 9 HOUR),0,2619.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='changomas.sanjusto@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Manteca' AND p.marca='Ilolay' AND p.unidad='100g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 3 HOUR),0,3418.35,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='changomas.sanjusto@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Pan lactal blanco' AND p.marca='Lactal' AND p.unidad='550g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 8 HOUR),0,2839.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='changomas.sanjusto@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Galletitas de chocolate' AND p.marca='Chocolina' AND p.unidad='250g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 2 HOUR),0,6929.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='changomas.sanjusto@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Aceite de girasol' AND p.marca='Cocinero' AND p.unidad='1.5l'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 7 HOUR),0,2190.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Vélez Sarsfield 567' AND c.localidad='La Matanza' AND p.nombre='Leche entera sachet' AND p.marca='La Serenísima' AND p.unidad='1l'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 1 HOUR),0,5190.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Vélez Sarsfield 567' AND c.localidad='La Matanza' AND p.nombre='Yerba mate suave' AND p.marca='Playadito' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 6 HOUR),0,2690.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Vélez Sarsfield 567' AND c.localidad='La Matanza' AND p.nombre='Yerba mate tradicional' AND p.marca='Amanda' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 11 HOUR),0,5190.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Vélez Sarsfield 567' AND c.localidad='La Matanza' AND p.nombre='Yerba mate tradicional' AND p.marca='Cachamai' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 5 HOUR),0,7390.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Vélez Sarsfield 567' AND c.localidad='La Matanza' AND p.nombre='Aceite de girasol' AND p.marca='Natura' AND p.unidad='1.5l'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 10 HOUR),0,2590.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Vélez Sarsfield 567' AND c.localidad='La Matanza' AND p.nombre='Arroz integral' AND p.marca='Lucchetti' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 4 HOUR),0,1990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Vélez Sarsfield 567' AND c.localidad='La Matanza' AND p.nombre='Fideos coditos' AND p.marca='Matarazzo' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 9 HOUR),0,1390.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Vélez Sarsfield 567' AND c.localidad='La Matanza' AND p.nombre='Azúcar' AND p.marca='Azucel' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 3 HOUR),0,9990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Vélez Sarsfield 567' AND c.localidad='La Matanza' AND p.nombre='Café instantáneo Tradición' AND p.marca='Nescafé' AND p.unidad='100g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 8 HOUR),0,940.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Vélez Sarsfield 567' AND c.localidad='La Matanza' AND p.nombre='Galletitas Opera' AND p.marca='Opera' AND p.unidad='55g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 2 HOUR),0,2690.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Vélez Sarsfield 567' AND c.localidad='La Matanza' AND p.nombre='Pan de pancho x6' AND p.marca='Lactal' AND p.unidad='6u'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 7 HOUR),0,3031.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Leche protein' AND p.marca='La Serenísima' AND p.unidad='1l'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 1 HOUR),0,3500.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Yerba mate liviana' AND p.marca='Unión' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 6 HOUR),0,3000.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Fideos moños' AND p.marca='Lucchetti' AND p.unidad='500g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 11 HOUR),0,3548.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Galletitas Traviata' AND p.marca='Traviata' AND p.unidad='540g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 5 HOUR),0,11700.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Café suave y espumoso' AND p.marca='Arlistan' AND p.unidad='170g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 10 HOUR),0,5350.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Manteca extra calidad' AND p.marca='Ilolay' AND p.unidad='200g'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);

-- 6) Reportes dudosos (ficticios, con precio fuera de mercado: muestran el cartel de dudoso)
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 2 HOUR),7,1890.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Yerba mate suave' AND p.marca='Playadito' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT IGNORE INTO reporte_usuarios_dudoso(reporte_id,usuario_id)
SELECT r.id,u.id FROM Reporte r JOIN Usuario u ON u.email='lucia.fernandez@example.com'
JOIN Comercio c ON c.id=r.comercio_id JOIN Producto p ON p.id=r.producto_id
WHERE c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Yerba mate suave' AND p.marca='Playadito' AND p.unidad='1kg' AND r.precio=1890.00 AND r.usuario_id=(SELECT id FROM Usuario WHERE email='diego.benitez@example.com');
INSERT IGNORE INTO reporte_usuarios_dudoso(reporte_id,usuario_id)
SELECT r.id,u.id FROM Reporte r JOIN Usuario u ON u.email='martin.gomez@example.com'
JOIN Comercio c ON c.id=r.comercio_id JOIN Producto p ON p.id=r.producto_id
WHERE c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Yerba mate suave' AND p.marca='Playadito' AND p.unidad='1kg' AND r.precio=1890.00 AND r.usuario_id=(SELECT id FROM Usuario WHERE email='diego.benitez@example.com');
INSERT IGNORE INTO reporte_usuarios_dudoso(reporte_id,usuario_id)
SELECT r.id,u.id FROM Reporte r JOIN Usuario u ON u.email='carolina.rossi@example.com'
JOIN Comercio c ON c.id=r.comercio_id JOIN Producto p ON p.id=r.producto_id
WHERE c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Yerba mate suave' AND p.marca='Playadito' AND p.unidad='1kg' AND r.precio=1890.00 AND r.usuario_id=(SELECT id FROM Usuario WHERE email='diego.benitez@example.com');
INSERT IGNORE INTO reporte_usuarios_dudoso(reporte_id,usuario_id)
SELECT r.id,u.id FROM Reporte r JOIN Usuario u ON u.email='carrefour.express.sanjusto@example.com'
JOIN Comercio c ON c.id=r.comercio_id JOIN Producto p ON p.id=r.producto_id
WHERE c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Yerba mate suave' AND p.marca='Playadito' AND p.unidad='1kg' AND r.precio=1890.00 AND r.usuario_id=(SELECT id FROM Usuario WHERE email='diego.benitez@example.com');
INSERT IGNORE INTO reporte_usuarios_dudoso(reporte_id,usuario_id)
SELECT r.id,u.id FROM Reporte r JOIN Usuario u ON u.email='dia.sanjusto@example.com'
JOIN Comercio c ON c.id=r.comercio_id JOIN Producto p ON p.id=r.producto_id
WHERE c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Yerba mate suave' AND p.marca='Playadito' AND p.unidad='1kg' AND r.precio=1890.00 AND r.usuario_id=(SELECT id FROM Usuario WHERE email='diego.benitez@example.com');
INSERT IGNORE INTO reporte_usuarios_dudoso(reporte_id,usuario_id)
SELECT r.id,u.id FROM Reporte r JOIN Usuario u ON u.email='changomas.sanjusto@example.com'
JOIN Comercio c ON c.id=r.comercio_id JOIN Producto p ON p.id=r.producto_id
WHERE c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Yerba mate suave' AND p.marca='Playadito' AND p.unidad='1kg' AND r.precio=1890.00 AND r.usuario_id=(SELECT id FROM Usuario WHERE email='diego.benitez@example.com');
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 2 HOUR),7,12990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Aceite de girasol' AND p.marca='Cocinero' AND p.unidad='1.5l'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT IGNORE INTO reporte_usuarios_dudoso(reporte_id,usuario_id)
SELECT r.id,u.id FROM Reporte r JOIN Usuario u ON u.email='lucia.fernandez@example.com'
JOIN Comercio c ON c.id=r.comercio_id JOIN Producto p ON p.id=r.producto_id
WHERE c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Aceite de girasol' AND p.marca='Cocinero' AND p.unidad='1.5l' AND r.precio=12990.00 AND r.usuario_id=(SELECT id FROM Usuario WHERE email='carolina.rossi@example.com');
INSERT IGNORE INTO reporte_usuarios_dudoso(reporte_id,usuario_id)
SELECT r.id,u.id FROM Reporte r JOIN Usuario u ON u.email='martin.gomez@example.com'
JOIN Comercio c ON c.id=r.comercio_id JOIN Producto p ON p.id=r.producto_id
WHERE c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Aceite de girasol' AND p.marca='Cocinero' AND p.unidad='1.5l' AND r.precio=12990.00 AND r.usuario_id=(SELECT id FROM Usuario WHERE email='carolina.rossi@example.com');
INSERT IGNORE INTO reporte_usuarios_dudoso(reporte_id,usuario_id)
SELECT r.id,u.id FROM Reporte r JOIN Usuario u ON u.email='diego.benitez@example.com'
JOIN Comercio c ON c.id=r.comercio_id JOIN Producto p ON p.id=r.producto_id
WHERE c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Aceite de girasol' AND p.marca='Cocinero' AND p.unidad='1.5l' AND r.precio=12990.00 AND r.usuario_id=(SELECT id FROM Usuario WHERE email='carolina.rossi@example.com');
INSERT IGNORE INTO reporte_usuarios_dudoso(reporte_id,usuario_id)
SELECT r.id,u.id FROM Reporte r JOIN Usuario u ON u.email='carrefour.express.sanjusto@example.com'
JOIN Comercio c ON c.id=r.comercio_id JOIN Producto p ON p.id=r.producto_id
WHERE c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Aceite de girasol' AND p.marca='Cocinero' AND p.unidad='1.5l' AND r.precio=12990.00 AND r.usuario_id=(SELECT id FROM Usuario WHERE email='carolina.rossi@example.com');
INSERT IGNORE INTO reporte_usuarios_dudoso(reporte_id,usuario_id)
SELECT r.id,u.id FROM Reporte r JOIN Usuario u ON u.email='dia.sanjusto@example.com'
JOIN Comercio c ON c.id=r.comercio_id JOIN Producto p ON p.id=r.producto_id
WHERE c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Aceite de girasol' AND p.marca='Cocinero' AND p.unidad='1.5l' AND r.precio=12990.00 AND r.usuario_id=(SELECT id FROM Usuario WHERE email='carolina.rossi@example.com');
INSERT IGNORE INTO reporte_usuarios_dudoso(reporte_id,usuario_id)
SELECT r.id,u.id FROM Reporte r JOIN Usuario u ON u.email='changomas.sanjusto@example.com'
JOIN Comercio c ON c.id=r.comercio_id JOIN Producto p ON p.id=r.producto_id
WHERE c.direccion='Av. Meeks 256' AND c.localidad='Lomas de Zamora' AND p.nombre='Aceite de girasol' AND p.marca='Cocinero' AND p.unidad='1.5l' AND r.precio=12990.00 AND r.usuario_id=(SELECT id FROM Usuario WHERE email='carolina.rossi@example.com');

-- 7) Promociones
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT '30% de descuento los martes con Banco Ciudad y Santander','Presentá tu tarjeta de crédito o débito de Banco Ciudad o Santander los martes y te descontamos el 30% en la compra.','METODO_PAGO',NULL,NULL,'CREDITO',30,DATE_ADD(@ahora, INTERVAL -2 DAY),DATE_ADD(@ahora, INTERVAL 45 DAY),b'1',(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='30% de descuento los martes con Banco Ciudad y Santander' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza'));
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT 'Arroz parboil Lucchetti 1 kg','Llevate el arroz parboil Lucchetti de 1 kg a precio promocional hasta agotar stock.','PRODUCTO','Arroz parboil',2199.00,NULL,NULL,DATE_ADD(@ahora, INTERVAL -1 DAY),DATE_ADD(@ahora, INTERVAL 10 DAY),b'1',(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='Arroz parboil Lucchetti 1 kg' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza'));
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT 'Aceite de girasol Cañuelas 1,5 L','Aceite Cañuelas de 1,5 L con precio especial por la semana.','PRODUCTO','Aceite de girasol',5990.00,NULL,NULL,DATE_ADD(@ahora, INTERVAL -1 DAY),DATE_ADD(@ahora, INTERVAL 7 DAY),b'1',(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='Aceite de girasol Cañuelas 1,5 L' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza'));
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT '10% pagando con QR o billetera virtual','Pagá con Mercado Pago, MODO o cualquier QR interoperable y obtené 10% de descuento.','METODO_PAGO',NULL,NULL,'BILLETERA_VIRTUAL',10,DATE_ADD(@ahora, INTERVAL -3 DAY),DATE_ADD(@ahora, INTERVAL 30 DAY),b'1',(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='10% pagando con QR o billetera virtual' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Dr. Ignacio Arieta 2850' AND localidad='La Matanza'));
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT 'Miércoles de descuentos con bancos','Los miércoles pagando con tarjetas de crédito de bancos adheridos tenés 20% de descuento.','METODO_PAGO',NULL,NULL,'CREDITO',20,DATE_ADD(@ahora, INTERVAL -1 DAY),DATE_ADD(@ahora, INTERVAL 60 DAY),b'1',(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='Miércoles de descuentos con bancos' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza'));
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT 'Yerba Playadito suave 1 kg','Yerba mate Playadito suave de 1 kg en promoción.','PRODUCTO','Yerba mate suave',4199.00,NULL,NULL,DATE_ADD(@ahora, INTERVAL -1 DAY),DATE_ADD(@ahora, INTERVAL 10 DAY),b'1',(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='Yerba Playadito suave 1 kg' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza'));
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT 'Manteca Ilolay extra calidad 200 g','Manteca extra calidad Ilolay de 200 g a precio de oferta.','PRODUCTO','Manteca extra calidad',3790.00,NULL,NULL,DATE_ADD(@ahora, INTERVAL -2 DAY),DATE_ADD(@ahora, INTERVAL 8 DAY),b'1',(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='Manteca Ilolay extra calidad 200 g' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza'));
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT '15% los sábados con Mercado Pago','Todos los sábados pagando con Mercado Pago tenés 15% de descuento.','METODO_PAGO',NULL,NULL,'BILLETERA_VIRTUAL',15,DATE_ADD(@ahora, INTERVAL -4 DAY),DATE_ADD(@ahora, INTERVAL 40 DAY),b'1',(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='15% los sábados con Mercado Pago' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND localidad='La Matanza'));
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT 'Azúcar Azucel 1 kg','Azúcar Azucel de 1 kg a precio especial.','PRODUCTO','Azúcar',1290.00,NULL,NULL,DATE_ADD(@ahora, INTERVAL -1 DAY),DATE_ADD(@ahora, INTERVAL 10 DAY),b'1',(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='Azúcar Azucel 1 kg' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza'));
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT 'Pan lactal blanco Lactal 550 g','Pan lactal blanco de 550 g en oferta.','PRODUCTO','Pan lactal blanco',2990.00,NULL,NULL,DATE_ADD(@ahora, INTERVAL -2 DAY),DATE_ADD(@ahora, INTERVAL 6 DAY),b'1',(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='Pan lactal blanco Lactal 550 g' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza'));
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT '5% de descuento pagando en efectivo','Pagando en efectivo en caja tenés 5% de descuento en toda la compra.','METODO_PAGO',NULL,NULL,'EFECTIVO',5,DATE_ADD(@ahora, INTERVAL -5 DAY),DATE_ADD(@ahora, INTERVAL 30 DAY),b'1',(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='5% de descuento pagando en efectivo' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza'));
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT 'Jueves de débito: 15% de descuento','Los jueves, con tarjeta de débito, 15% de descuento.','METODO_PAGO',NULL,NULL,'DEBITO',15,DATE_ADD(@ahora, INTERVAL -3 DAY),DATE_ADD(@ahora, INTERVAL 35 DAY),b'1',(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='Jueves de débito: 15% de descuento' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza'));
INSERT INTO Promocion(titulo,descripcion,tipo,productoNombre,precioPromocional,metodoPago,porcentajeDescuento,fechaInicio,fechaFin,activa,comercio_id)
SELECT 'Fideos bucattini Matarazzo 500 g','Promoción vencida de la semana pasada.','PRODUCTO','Fideos bucattini',1899.00,NULL,NULL,DATE_ADD(@ahora, INTERVAL -14 DAY),DATE_ADD(@ahora, INTERVAL -7 DAY),b'0',(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Promocion WHERE titulo='Fideos bucattini Matarazzo 500 g' AND comercio_id=(SELECT id FROM Comercio WHERE direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND localidad='La Matanza'));

-- 8) Verificación: cantidades esperadas
SELECT 'usuarios demo' AS que, COUNT(*) AS cantidad FROM Usuario WHERE email IN ('lucia.fernandez@example.com','martin.gomez@example.com','carolina.rossi@example.com','diego.benitez@example.com','carrefour.express.sanjusto@example.com','dia.sanjusto@example.com','changomas.sanjusto@example.com')
UNION ALL SELECT 'comercios', COUNT(*) FROM Comercio WHERE localidad IN ('La Matanza','Lomas de Zamora') AND nombre IN ('Carrefour Express San Justo','Supermercados DIA San Justo','ChangoMás San Justo','Vea Villa Madero','Disco Lomas de Zamora')
UNION ALL SELECT 'reportes dudosos', COUNT(*) FROM Reporte WHERE puntuacion > 6
UNION ALL SELECT 'promociones', COUNT(*) FROM Promocion WHERE comercio_id IN (SELECT id FROM Comercio WHERE nombre IN ('Carrefour Express San Justo','Supermercados DIA San Justo','ChangoMás San Justo'));
