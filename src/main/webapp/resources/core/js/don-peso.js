const raiz = document.getElementById("don-peso-widget");

if (raiz) {
    const base = raiz.dataset.base;
    const botonAbrir = raiz.querySelector("#dpw-abrir");
    const panel = raiz.querySelector("#dpw-panel");
    const mensajes = raiz.querySelector("#dpw-mensajes");
    const sugerencias = raiz.querySelector("#dpw-sugerencias");
    const formulario = raiz.querySelector("#dpw-form");
    const entrada = raiz.querySelector("#dpw-entrada");
    const botonEnviar = raiz.querySelector(".dpw-enviar");

    const SUGERENCIAS = [
        "¿Dónde está más barato el aceite?",
        "Armame una compra con $10.000",
        "¿Qué promociones hay hoy?",
        "¿Cómo reporto un precio?"
    ];
    const SALUDO =
        "¡Hola! Soy **Don Peso**. Te ayudo a comprar más barato en tu barrio: " +
        "comparo precios, busco promociones y te explico cómo usar la app. ¿Qué necesitás?";
    const MENSAJE_ERROR = "No pude responder ahora. Probá de nuevo en un ratito.";

    let latitud = null;
    let longitud = null;
    let historialCargado = false;
    let enviando = false;

    // Lo que escribe el vecino o cargan otros comercios puede traer HTML:
    // se escapa antes de aplicar el formato propio.
    const escaparHtml = (texto) =>
        String(texto)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");

    const aplicarNegrita = (linea) => linea.replace(/\*\*(.+?)\*\*/g, "<strong>$1</strong>");

    // Soporta negrita, listas con guiones y saltos de línea.
    const formatear = (texto) => {
        const lineas = escaparHtml(texto).split("\n");
        let html = "";
        let enLista = false;
        lineas.forEach((linea) => {
            const item = linea.match(/^\s*[-•*]\s+(.*)$/);
            if (item) {
                html += (enLista ? "" : "<ul>") + "<li>" + aplicarNegrita(item[1]) + "</li>";
                enLista = true;
                return;
            }
            if (enLista) {
                html += "</ul>";
                enLista = false;
            }
            if (linea.trim() !== "") {
                html += "<p>" + aplicarNegrita(linea) + "</p>";
            }
        });
        return html + (enLista ? "</ul>" : "");
    };

    const bajarAlFinal = () => {
        mensajes.scrollTop = mensajes.scrollHeight;
    };

    const agregarMensaje = (texto, autor) => {
        const burbuja = document.createElement("div");
        burbuja.className = "dpw-msg dpw-msg--" + autor;
        if (autor === "bot") {
            burbuja.innerHTML = formatear(texto);
        } else {
            burbuja.textContent = texto;
        }
        mensajes.appendChild(burbuja);
        bajarAlFinal();
        return burbuja;
    };

    const mostrarEscribiendo = () => {
        const burbuja = document.createElement("div");
        burbuja.className = "dpw-msg dpw-msg--bot dpw-escribiendo";
        burbuja.setAttribute("aria-label", "Don Peso está escribiendo");
        burbuja.innerHTML = "<span></span><span></span><span></span>";
        mensajes.appendChild(burbuja);
        bajarAlFinal();
        return burbuja;
    };

    const dibujarSugerencias = () => {
        sugerencias.replaceChildren();
        SUGERENCIAS.forEach((texto) => {
            const chip = document.createElement("button");
            chip.type = "button";
            chip.className = "dpw-chip";
            chip.textContent = texto;
            chip.addEventListener("click", () => enviar(texto));
            sugerencias.appendChild(chip);
        });
        sugerencias.hidden = false;
    };

    const empezarDeCero = () => {
        mensajes.replaceChildren();
        agregarMensaje(SALUDO, "bot");
        dibujarSugerencias();
    };

    const cargarHistorial = async () => {
        historialCargado = true;
        try {
            const respuesta = await fetch(base + "/historial", { headers: { Accept: "application/json" } });
            const historial = respuesta.ok ? await respuesta.json() : [];
            if (Array.isArray(historial) && historial.length > 0) {
                historial.forEach((turno) =>
                    agregarMensaje(turno.content, turno.role === "user" ? "user" : "bot")
                );
                sugerencias.hidden = true;
                return;
            }
        } catch {
            // Sin historial se arranca una conversación nueva.
        }
        empezarDeCero();
    };

    const pedirUbicacion = () => {
        if (!navigator.geolocation || latitud !== null) {
            return;
        }
        navigator.geolocation.getCurrentPosition(
            (posicion) => {
                latitud = posicion.coords.latitude;
                longitud = posicion.coords.longitude;
            },
            () => {}
        );
    };

    const bloquear = (bloqueado) => {
        enviando = bloqueado;
        entrada.disabled = bloqueado;
        botonEnviar.disabled = bloqueado;
    };

    const consultar = async (pregunta) => {
        try {
            const resultado = await fetch(base + "/consultar", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ pregunta, latitud, longitud })
            });
            const datos = await resultado.json();
            return datos.respuesta || datos.error || MENSAJE_ERROR;
        } catch {
            return MENSAJE_ERROR;
        }
    };

    async function enviar(texto) {
        const pregunta = texto.trim();
        if (!pregunta || enviando) {
            return;
        }
        sugerencias.hidden = true;
        agregarMensaje(pregunta, "user");
        entrada.value = "";
        bloquear(true);
        const escribiendo = mostrarEscribiendo();

        const respuesta = await consultar(pregunta);
        escribiendo.remove();
        agregarMensaje(respuesta, "bot");
        bloquear(false);
        entrada.focus();
    }

    const abrir = () => {
        panel.hidden = false;
        botonAbrir.setAttribute("aria-expanded", "true");
        raiz.classList.add("dpw--abierto");
        document.body.classList.add("dpw-sin-scroll");
        if (!historialCargado) {
            cargarHistorial();
            pedirUbicacion();
        }
        entrada.focus();
    };

    const cerrar = () => {
        panel.hidden = true;
        botonAbrir.setAttribute("aria-expanded", "false");
        raiz.classList.remove("dpw--abierto");
        document.body.classList.remove("dpw-sin-scroll");
        botonAbrir.focus();
    };

    const reiniciar = async () => {
        try {
            await fetch(base + "/reiniciar", { method: "POST" });
        } catch {
            // Si falla el servidor igual se limpia la pantalla.
        }
        empezarDeCero();
        entrada.focus();
    };

    botonAbrir.addEventListener("click", () => (panel.hidden ? abrir() : cerrar()));
    raiz.querySelector("#dpw-cerrar").addEventListener("click", cerrar);
    raiz.querySelector("#dpw-nueva").addEventListener("click", reiniciar);
    formulario.addEventListener("submit", (evento) => {
        evento.preventDefault();
        enviar(entrada.value);
    });
    document.addEventListener("keydown", (evento) => {
        if (evento.key === "Escape" && !panel.hidden) {
            cerrar();
        }
    });
}