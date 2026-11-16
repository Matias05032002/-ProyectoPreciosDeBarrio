-- Refresca las fechas de los reportes de demostracion para que la app los muestre como "Fresco"
-- (la app marca "Fresco" hasta 3 dias de antiguedad). Se puede correr las veces que haga falta.
UPDATE Reporte r
JOIN Usuario u ON u.id = r.usuario_id
SET r.fechaDeReporte = DATE_SUB(DATE_SUB(NOW(), INTERVAL (r.id MOD 4) DAY), INTERVAL ((r.id * 5) MOD 11) + 1 HOUR)
WHERE u.email IN ('lucia.fernandez@example.com','martin.gomez@example.com','carolina.rossi@example.com','diego.benitez@example.com','carrefour.express.sanjusto@example.com','dia.sanjusto@example.com','changomas.sanjusto@example.com')
  AND r.puntuacion < 7;

UPDATE Reporte r
JOIN Usuario u ON u.id = r.usuario_id
SET r.fechaDeReporte = DATE_SUB(NOW(), INTERVAL 5 HOUR)
WHERE u.email IN ('lucia.fernandez@example.com','martin.gomez@example.com','carolina.rossi@example.com','diego.benitez@example.com','carrefour.express.sanjusto@example.com','dia.sanjusto@example.com','changomas.sanjusto@example.com')
  AND r.puntuacion >= 7;

SELECT DATEDIFF(NOW(), fechaDeReporte) AS dias_de_antiguedad, COUNT(*) AS reportes
FROM Reporte GROUP BY dias_de_antiguedad ORDER BY dias_de_antiguedad;
