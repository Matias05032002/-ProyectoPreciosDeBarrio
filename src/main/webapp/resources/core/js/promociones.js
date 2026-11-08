import { distanciaEnKm, textoDeDistancia } from "./distancia.js";

const boton = document.getElementById("btn-distancia-promociones");
const aviso = document.getElementById("estado-distancia");
const tarjetas = Array.from(document.querySelectorAll("[data-promocion-lat]"));

function avisar(texto) {
  aviso.textContent = texto;
  aviso.hidden = !texto;
}

function mostrarDistancias(latitud, longitud) {
  tarjetas.forEach((tarjeta) => {
    const lat = parseFloat(tarjeta.dataset.promocionLat);
    const lng = parseFloat(tarjeta.dataset.promocionLng);
    const chip = tarjeta.querySelector(".cm-distancia");
    if (!chip || Number.isNaN(lat) || Number.isNaN(lng)) {
      return;
    }
    chip.textContent = textoDeDistancia(distanciaEnKm(latitud, longitud, lat, lng));
    chip.hidden = false;
  });
  boton.setAttribute("aria-pressed", "true");
  boton.querySelector("span").textContent = "Distancias desde tu ubicación";
  avisar("");
}

function pedirUbicacion() {
  if (!navigator.geolocation) {
    avisar("Tu navegador no permite usar la ubicación.");
    return;
  }
  avisar("Buscando tu ubicación…");
  navigator.geolocation.getCurrentPosition(
    (posicion) => mostrarDistancias(posicion.coords.latitude, posicion.coords.longitude),
    () => avisar("No se pudo obtener tu ubicación. Activá el GPS o los permisos del navegador."),
    { enableHighAccuracy: false, timeout: 10000, maximumAge: 300000 }
  );
}

if (boton && aviso && tarjetas.length > 0) {
  boton.addEventListener("click", pedirUbicacion);
}