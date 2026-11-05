const raiz = document.getElementById("inicio-comercio");

if (raiz) {
  const URLS = {
    foto: raiz.dataset.urlPerfil,
    descripcion: raiz.dataset.urlPerfil,
    ubicacion: raiz.dataset.urlPerfil,
    horarios: raiz.dataset.urlHorarios,
    verificacion: raiz.dataset.urlPerfil
  };
  const formatoPrecio = new Intl.NumberFormat("es-AR", {
    style: "currency",
    currency: "ARS",
    minimumFractionDigits: 2
  });

  const buscar = (id) => document.getElementById(id);

  const crear = (etiqueta, clase, texto) => {
    const elemento = document.createElement(etiqueta);
    if (clase) {
      elemento.className = clase;
    }
    if (texto !== undefined) {
      elemento.textContent = texto;
    }
    return elemento;
  };

  const hace = (fechaIso) => {
    if (!fechaIso) {
      return "";
    }
    const minutos = Math.max(0, Math.round((Date.now() - new Date(fechaIso).getTime()) / 60000));
    if (minutos < 60) {
      return "Hace un momento";
    }
    const horas = Math.round(minutos / 60);
    if (horas < 24) {
      return "Hace " + horas + (horas === 1 ? " hora" : " horas");
    }
    const dias = Math.round(horas / 24);
    return "Hace " + dias + (dias === 1 ? " día" : " días");
  };

  const dibujarEstado = (resumen) => {
    const estado = buscar("ic-estado");
    const icono = crear("i", "bi " + (resumen.verificado ? "bi-patch-check-fill" : "bi-hourglass-split"));
    icono.setAttribute("aria-hidden", "true");
    let texto = "Comercio sin verificar";
    if (resumen.verificado) {
      texto = "Comercio verificado";
    } else if (resumen.solicitudVerificacion) {
      texto = "Verificación en revisión";
    }
    estado.replaceChildren(icono, document.createTextNode(texto));
    estado.classList.toggle("ic-estado--pendiente", !resumen.verificado);
    estado.hidden = false;
  };

  const dibujarCifras = (resumen) => {
    buscar("ic-cifra-precios").textContent = resumen.preciosReportados;
    buscar("ic-cifra-vecinos").textContent = resumen.vecinos;
    buscar("ic-cifra-promos").textContent = resumen.promocionesActivas;
    raiz.classList.remove("ic--cargando");
  };

  const dibujarPerfil = (resumen) => {
    const tarjeta = buscar("ic-perfil");
    if (resumen.porcentajePerfil >= 100) {
      tarjeta.hidden = true;
      return;
    }
    const anillo = buscar("ic-anillo");
    anillo.style.setProperty("--pct", resumen.porcentajePerfil);
    anillo.dataset.texto = resumen.porcentajePerfil + "%";
    const lista = buscar("ic-pasos");
    lista.replaceChildren();
    resumen.pasos.forEach((paso) => {
      const item = crear("li");
      const icono = crear("i", "bi " + (paso.completo ? "bi-check-circle-fill" : "bi-circle"));
      icono.setAttribute("aria-hidden", "true");
      if (paso.completo) {
        const fila = crear("span", "ic-paso ic-paso--hecho");
        fila.append(icono, document.createTextNode(paso.titulo));
        item.appendChild(fila);
      } else {
        const fila = crear("a", "ic-paso");
        fila.href = URLS[paso.clave] || URLS.foto;
        const flecha = crear("i", "bi bi-arrow-right ic-flecha");
        flecha.setAttribute("aria-hidden", "true");
        fila.append(icono, document.createTextNode(paso.titulo), flecha);
        item.appendChild(fila);
      }
      lista.appendChild(item);
    });
    tarjeta.hidden = false;
  };

  const dibujarUltimos = (resumen) => {
    const lista = buscar("ic-ultimos");
    const vacio = buscar("ic-ultimos-vacio");
    lista.replaceChildren();
    const hayPrecios = resumen.ultimosPrecios.length > 0;
    lista.hidden = !hayPrecios;
    vacio.hidden = hayPrecios;
    resumen.ultimosPrecios.forEach((precio) => {
      const fila = crear("li", "ic-fila");
      const texto = crear("div");
      texto.append(
        crear("span", "ic-fila-nombre", precio.producto),
        crear("span", "ic-fila-fecha", hace(precio.fecha))
      );
      fila.append(texto, crear("span", "ic-fila-precio", formatoPrecio.format(precio.precio)));
      lista.appendChild(fila);
    });
  };

  const mostrarSinComercio = () => {
    raiz.classList.remove("ic--cargando");
    buscar("ic-contenido").hidden = true;
    buscar("ic-sin-comercio").hidden = false;
  };

  const cargar = async () => {
    try {
      const respuesta = await fetch(raiz.dataset.resumenUrl, { headers: { Accept: "application/json" } });
      if (respuesta.status === 404) {
        mostrarSinComercio();
        return;
      }
      if (!respuesta.ok) {
        throw new Error("Respuesta " + respuesta.status);
      }
      const resumen = await respuesta.json();
      buscar("ic-nombre").textContent = resumen.nombre;
      document.title = resumen.nombre + " — Precios de Barrio";
      dibujarEstado(resumen);
      dibujarCifras(resumen);
      dibujarPerfil(resumen);
      dibujarUltimos(resumen);
    } catch {
      raiz.classList.remove("ic--cargando");
      buscar("ic-error").hidden = false;
    }
  };

  cargar();
}