const RADIO_DE_LA_TIERRA_KM = 6371;

function aRadianes(grados) {
  return grados * Math.PI / 180;
}

// Haversine: distancia en línea recta entre dos puntos, en kilómetros
export function distanciaEnKm(latitud1, longitud1, latitud2, longitud2) {
  const dLat = aRadianes(latitud2 - latitud1);
  const dLng = aRadianes(longitud2 - longitud1);
  const haversine =
        Math.sin(dLat / 2) ** 2 +
        Math.cos(aRadianes(latitud1)) * Math.cos(aRadianes(latitud2)) * Math.sin(dLng / 2) ** 2;
  return RADIO_DE_LA_TIERRA_KM * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
}

// "A 400 m" hasta 1 km, "A 1,2 km" después; sin distancia, sin texto
export function textoDeDistancia(km) {
  if (typeof km !== "number" || Number.isNaN(km)) {
    return "";
  }
  if (km < 0.1) {
    return "A menos de 100 m";
  }
  const metros = Math.round(km * 100) * 10;
  if (metros < 1000) {
    return "A " + metros + " m";
  }
  return "A " + km.toLocaleString("es-AR", { maximumFractionDigits: 1 }) + " km";
}