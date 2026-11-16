-- Deja sin foto a los productos cuya imagen NO viene de Mercado Libre, Carrefour, DIA u Open Food Facts
-- (las genericas de Pixabay / Wikipedia), para que la app las busque de nuevo al reiniciar con la
-- cadena nueva: Mercado Libre -> Carrefour -> DIA -> Open Food Facts -> Pixabay -> icono gris.
-- Las fotos de esas cuatro fuentes y las subidas en reportes no se tocan.
SET NAMES utf8mb4;

UPDATE Producto
SET imagenUrl = NULL
WHERE imagenUrl IS NOT NULL
  AND imagenUrl NOT LIKE '%mlstatic.com%'
  AND imagenUrl NOT LIKE '%vteximg.com.br%'
  AND imagenUrl NOT LIKE '%openfoodfacts.org%';

SELECT COUNT(*) AS productos_para_rellenar FROM Producto WHERE imagenUrl IS NULL;