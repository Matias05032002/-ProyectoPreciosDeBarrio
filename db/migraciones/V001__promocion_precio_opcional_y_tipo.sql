-- Promociones por método de pago (ej. "20% con débito"): no tienen un precio promocional,
-- pero hbm2ddl.auto=update nunca afloja una columna que ya existía como NOT NULL.
-- Correr UNA vez sobre bases creadas antes de agregar ese tipo de promoción.

ALTER TABLE Promocion MODIFY precioPromocional DOUBLE NULL;

-- Las promociones viejas eran todas por producto
UPDATE Promocion SET tipo = 'PRODUCTO' WHERE tipo IS NULL;
