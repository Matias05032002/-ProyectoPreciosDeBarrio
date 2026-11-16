-- Productos reales de verdulería y carnicería (precios por kilo publicados en noviembre 2026
-- por Carrefour, DIA y ChangoMás). Corre sobre la base ya cargada con datos_reales.sql.
-- Es seguro repetirlo: no duplica filas. Las fotos se completan al reiniciar la app.
SET NAMES utf8mb4;
SET @ahora = NOW();

INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Tomate redondo','Sin marca','Verduras y frutas','1kg','Tomate redondo fresco, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Tomate redondo' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Tomate perita','Sin marca','Verduras y frutas','1kg','Tomate perita fresco, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Tomate perita' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Cebolla','Sin marca','Verduras y frutas','1kg','Cebolla común, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Cebolla' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Cebolla morada','Sin marca','Verduras y frutas','1kg','Cebolla morada, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Cebolla morada' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Papa','Sin marca','Verduras y frutas','1kg','Papa negra, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Papa' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Batata','Sin marca','Verduras y frutas','1kg','Batata, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Batata' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Zanahoria','Sin marca','Verduras y frutas','1kg','Zanahoria, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Zanahoria' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Zapallo anco','Sin marca','Verduras y frutas','1kg','Zapallo anco, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Zapallo anco' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Zapallito','Sin marca','Verduras y frutas','1kg','Zapallito, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Zapallito' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Lechuga','Sin marca','Verduras y frutas','1kg','Lechuga, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Lechuga' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Acelga','Sin marca','Verduras y frutas','1kg','Acelga, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Acelga' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Pimiento rojo','Sin marca','Verduras y frutas','1kg','Morrón rojo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Pimiento rojo' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Ajo','Sin marca','Verduras y frutas','1kg','Ajo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Ajo' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Berenjena','Sin marca','Verduras y frutas','1kg','Berenjena, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Berenjena' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Pepino','Sin marca','Verduras y frutas','1kg','Pepino, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Pepino' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Brócoli','Sin marca','Verduras y frutas','1kg','Brócoli, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Brócoli' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Coliflor','Sin marca','Verduras y frutas','1kg','Coliflor, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Coliflor' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Repollo blanco','Sin marca','Verduras y frutas','1kg','Repollo blanco, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Repollo blanco' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Remolacha','Sin marca','Verduras y frutas','1kg','Remolacha, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Remolacha' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Banana','Sin marca','Verduras y frutas','1kg','Banana, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Banana' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Manzana roja','Sin marca','Verduras y frutas','1kg','Manzana roja, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Manzana roja' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Manzana verde','Sin marca','Verduras y frutas','1kg','Manzana verde, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Manzana verde' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Naranja','Sin marca','Verduras y frutas','1kg','Naranja de jugo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Naranja' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Mandarina','Sin marca','Verduras y frutas','1kg','Mandarina, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Mandarina' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Limón','Sin marca','Verduras y frutas','1kg','Limón, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Limón' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Pera','Sin marca','Verduras y frutas','1kg','Pera, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Pera' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Durazno','Sin marca','Verduras y frutas','1kg','Durazno, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Durazno' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Sandía','Sin marca','Verduras y frutas','1kg','Sandía, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Sandía' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Pomelo','Sin marca','Verduras y frutas','1kg','Pomelo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Pomelo' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Asado de tira','Sin marca','Carnes y aves','1kg','Asado de novillo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Asado de tira' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Vacío','Sin marca','Carnes y aves','1kg','Vacío de novillo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Vacío' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Nalga','Sin marca','Carnes y aves','1kg','Nalga de novillo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Nalga' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Cuadril','Sin marca','Carnes y aves','1kg','Cuadril de novillo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Cuadril' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Peceto','Sin marca','Carnes y aves','1kg','Peceto de novillo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Peceto' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Bola de lomo','Sin marca','Carnes y aves','1kg','Bola de lomo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Bola de lomo' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Carne picada','Sin marca','Carnes y aves','1kg','Carne picada especial, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Carne picada' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Matambre','Sin marca','Carnes y aves','1kg','Matambre de novillo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Matambre' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Entraña','Sin marca','Carnes y aves','1kg','Entraña de novillo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Entraña' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Osobuco','Sin marca','Carnes y aves','1kg','Osobuco de novillo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Osobuco' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Lomo','Sin marca','Carnes y aves','1kg','Lomo de novillo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Lomo' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Bife de chorizo','Sin marca','Carnes y aves','1kg','Bife de chorizo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Bife de chorizo' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Milanesa de nalga','Sin marca','Carnes y aves','1kg','Milanesa de nalga, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Milanesa de nalga' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Pollo entero','Sin marca','Carnes y aves','1kg','Pollo entero, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Pollo entero' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Pechuga de pollo','Sin marca','Carnes y aves','1kg','Pechuga de pollo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Pechuga de pollo' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Bondiola de cerdo','Sin marca','Carnes y aves','1kg','Bondiola de cerdo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Bondiola de cerdo' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Pechito de cerdo','Sin marca','Carnes y aves','1kg','Pechito de cerdo, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Pechito de cerdo' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Producto(nombre,marca,categoria,unidad,descripcion,precio,disponible,fechaDeSubida)
SELECT 'Morcilla','Sin marca','Carnes y aves','1kg','Morcilla fresca, precio por kilo',NULL,b'1',DATE_SUB(@ahora, INTERVAL 20 DAY) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE nombre='Morcilla' AND marca='Sin marca' AND unidad='1kg');
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 2 HOUR),0,5999.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Tomate redondo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 7 HOUR),0,4290.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Tomate redondo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 12 HOUR),0,4899.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Tomate redondo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 17 HOUR),0,5899.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Tomate perita' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 2 HOUR),0,2499.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Cebolla' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 7 HOUR),0,2690.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Cebolla' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 12 HOUR),0,2599.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Cebolla' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 17 HOUR),0,4489.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Cebolla morada' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 2 HOUR),0,2590.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Papa' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 7 HOUR),0,2499.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Papa' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 12 HOUR),0,3399.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Batata' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 17 HOUR),0,3790.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Batata' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 2 HOUR),0,2999.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Batata' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 7 HOUR),0,1899.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Zanahoria' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 12 HOUR),0,1990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Zanahoria' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 17 HOUR),0,1899.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Zanahoria' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 2 HOUR),0,3999.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Zapallo anco' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 7 HOUR),0,2790.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Zapallo anco' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 12 HOUR),0,3799.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Zapallito' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 17 HOUR),0,2790.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Zapallito' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 2 HOUR),0,2999.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Lechuga' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 7 HOUR),0,3990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Lechuga' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 12 HOUR),0,2899.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Lechuga' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 17 HOUR),0,2990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Acelga' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 2 HOUR),0,6999.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Pimiento rojo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 7 HOUR),0,5690.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Pimiento rojo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 12 HOUR),0,9499.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Ajo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 17 HOUR),0,4999.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Berenjena' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 2 HOUR),0,4990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Berenjena' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 7 HOUR),0,4399.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Berenjena' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 12 HOUR),0,4199.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Pepino' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 17 HOUR),0,3890.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Pepino' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 2 HOUR),0,5499.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Brócoli' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 7 HOUR),0,4999.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Coliflor' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 12 HOUR),0,1690.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Repollo blanco' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 17 HOUR),0,2490.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Repollo blanco' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 2 HOUR),0,3299.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Remolacha' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 7 HOUR),0,4590.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Remolacha' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 12 HOUR),0,2299.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Banana' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 17 HOUR),0,2790.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Banana' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 2 HOUR),0,1759.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Banana' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 7 HOUR),0,4999.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Manzana roja' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 12 HOUR),0,4990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Manzana roja' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 17 HOUR),0,2999.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Manzana roja' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 2 HOUR),0,5299.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Manzana verde' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 7 HOUR),0,5990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Manzana verde' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 12 HOUR),0,899.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Naranja' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 17 HOUR),0,890.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Naranja' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 2 HOUR),0,799.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Naranja' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 7 HOUR),0,999.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Mandarina' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 12 HOUR),0,1890.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Mandarina' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 17 HOUR),0,2299.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Limón' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 2 HOUR),0,1990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Limón' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 7 HOUR),0,1519.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Limón' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 12 HOUR),0,3499.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Pera' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 17 HOUR),0,3290.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Pera' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 2 HOUR),0,2719.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Pera' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 7 HOUR),0,9999.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Durazno' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 12 HOUR),0,6890.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Durazno' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 17 HOUR),0,1499.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Sandía' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 2 HOUR),0,3990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Sandía' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 7 HOUR),0,1899.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Pomelo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 12 HOUR),0,1390.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Pomelo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 17 HOUR),0,12990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Asado de tira' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 2 HOUR),0,15900.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Asado de tira' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 7 HOUR),0,17299.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Asado de tira' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 12 HOUR),0,18990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Vacío' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 17 HOUR),0,19900.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Vacío' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 2 HOUR),0,21199.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Vacío' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 7 HOUR),0,17990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Nalga' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 12 HOUR),0,21300.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Nalga' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 17 HOUR),0,21399.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Nalga' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 2 HOUR),0,19800.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Cuadril' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 7 HOUR),0,20990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Cuadril' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 12 HOUR),0,21299.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Cuadril' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 17 HOUR),0,18990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Peceto' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 2 HOUR),0,25700.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Peceto' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 7 HOUR),0,24899.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Peceto' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 12 HOUR),0,14990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Bola de lomo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 17 HOUR),0,20199.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Bola de lomo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 2 HOUR),0,12990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Carne picada' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 7 HOUR),0,16399.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Carne picada' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 12 HOUR),0,11990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Matambre' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 17 HOUR),0,16990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Matambre' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 2 HOUR),0,19799.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Matambre' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 7 HOUR),0,14000.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Entraña' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 12 HOUR),0,30990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Entraña' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 17 HOUR),0,34699.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Entraña' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 2 HOUR),0,11200.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Osobuco' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 7 HOUR),0,11890.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Osobuco' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 12 HOUR),0,27900.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Lomo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 17 HOUR),0,32990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Lomo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 2 HOUR),0,24500.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Bife de chorizo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 7 HOUR),0,25990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Bife de chorizo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 12 HOUR),0,33899.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Bife de chorizo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 17 HOUR),0,21000.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Milanesa de nalga' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 2 HOUR),0,23650.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Milanesa de nalga' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 7 HOUR),0,2990.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Pollo entero' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 12 HOUR),0,4390.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Pollo entero' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 17 HOUR),0,3789.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Pollo entero' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 2 HOUR),0,7490.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Pechuga de pollo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 7 HOUR),0,10500.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Bondiola de cerdo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 12 HOUR),0,14700.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3634' AND c.localidad='La Matanza' AND p.nombre='Bondiola de cerdo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 17 HOUR),0,18089.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='diego.benitez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Bondiola de cerdo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 2 DAY), INTERVAL 2 HOUR),0,8000.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='lucia.fernandez@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Pechito de cerdo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 0 DAY), INTERVAL 7 HOUR),0,10499.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='martin.gomez@example.com' AND c.direccion='Av. Brig. Gral. Juan Manuel de Rosas 3900' AND c.localidad='La Matanza' AND p.nombre='Pechito de cerdo' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);
INSERT INTO Reporte(fechaDeReporte,puntuacion,precio,usuario_id,comercio_id,producto_id)
SELECT DATE_SUB(DATE_SUB(@ahora, INTERVAL 1 DAY), INTERVAL 12 HOUR),0,8039.00,u.id,c.id,p.id
FROM Usuario u, Comercio c, Producto p
WHERE u.email='carolina.rossi@example.com' AND c.direccion='Dr. Ignacio Arieta 2850' AND c.localidad='La Matanza' AND p.nombre='Morcilla' AND p.marca='Sin marca' AND p.unidad='1kg'
AND NOT EXISTS (SELECT 1 FROM Reporte r WHERE r.usuario_id=u.id AND r.comercio_id=c.id AND r.producto_id=p.id);

SELECT categoria, COUNT(*) AS productos FROM Producto WHERE marca='Sin marca' AND categoria IN ('Verduras y frutas','Carnes y aves') GROUP BY categoria;
