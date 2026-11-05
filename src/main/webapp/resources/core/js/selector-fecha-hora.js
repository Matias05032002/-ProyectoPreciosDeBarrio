/*
 * Selector de fecha y hora de Precios de Barrio.
 *
 * Mejora los <input type="datetime-local"> y <input type="time"> que tengan la clase
 * "js-selector". El input nativo se queda en el formulario (es el que se envía y el que
 * lee el controlador) y el selector solo le escribe el valor, así que sin JavaScript el
 * formulario sigue funcionando con el control del navegador.
 *
 * - datetime-local: calendario + ruedas de hora y minuto.
 * - time: solo las ruedas.
 * - data-desde="idDelOtroInput": en el calendario deshabilita los días anteriores a ese input.
 * - En pantallas angostas el panel sube desde abajo como una hoja.
 */
(function () {
  "use strict";

  var MESES = [
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
  ];
  var MESES_CORTOS = [
    "ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"
  ];
  var DIAS_CORTOS = ["dom", "lun", "mar", "mié", "jue", "vie", "sáb"];
  var DIAS_SEMANA = ["L", "M", "M", "J", "V", "S", "D"];
  var PASO_MINUTOS = 5;
  var HORA_INICIAL = 9;
  var ANCHO_HOJA = 576;
  var abierto = null;

  function dos(n) {
    return (n < 10 ? "0" : "") + n;
  }

  function aTexto(fecha) {
    return fecha.getFullYear() + "-" + dos(fecha.getMonth() + 1) + "-" + dos(fecha.getDate());
  }

  function mismoDia(a, b) {
    return a.getFullYear() === b.getFullYear() &&
            a.getMonth() === b.getMonth() &&
            a.getDate() === b.getDate();
  }

  function dia(fecha) {
    return new Date(fecha.getFullYear(), fecha.getMonth(), fecha.getDate());
  }

  // "2026-11-04T10:30" o "09:00:00" -> { fecha, hora, minuto } (lo que falte queda en null)
  function leer(valor, conFecha) {
    var m;
    var resultado = { fecha: null, hora: null, minuto: null };
    if (!valor) {
      return resultado;
    }
    if (conFecha) {
      m = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})/.exec(valor);
      if (m) {
        resultado.fecha = new Date(Number(m[1]), Number(m[2]) - 1, Number(m[3]));
        resultado.hora = Number(m[4]);
        resultado.minuto = Number(m[5]);
      }
      return resultado;
    }
    m = /^(\d{2}):(\d{2})/.exec(valor);
    if (m) {
      resultado.hora = Number(m[1]);
      resultado.minuto = Number(m[2]);
    }
    return resultado;
  }

  function el(etiqueta, clase, texto) {
    var nodo = document.createElement(etiqueta);
    if (clase) {
      nodo.className = clase;
    }
    if (texto !== undefined) {
      nodo.textContent = texto;
    }
    return nodo;
  }

  function boton(clase, texto, etiquetaAria) {
    var b = el("button", clase, texto);
    b.type = "button";
    if (etiquetaAria) {
      b.setAttribute("aria-label", etiquetaAria);
    }
    return b;
  }

  function Selector(input) {
    this.input = input;
    this.conFecha = input.type === "datetime-local";
    this.etiqueta = input.id ? document.querySelector("label[for='" + input.id + "']") : null;
    this.estado = leer(input.value, this.conFecha);
    this.mes = null;
    this.panel = null;
    this.armar();
    this.actualizarTexto();
  }

  Selector.prototype.armar = function () {
    var self = this;
    var input = this.input;
    var contenedor = el("div", "sf");
    input.parentNode.insertBefore(contenedor, input);
    contenedor.appendChild(input);

    input.classList.add("sf-nativo");
    input.tabIndex = -1;
    input.setAttribute("aria-hidden", "true");

    this.boton = boton("sf-boton cm-control", "");
    this.boton.setAttribute("aria-haspopup", "dialog");
    this.boton.setAttribute("aria-expanded", "false");
    this.icono = el("i", "bi " + (this.conFecha ? "bi-calendar-event" : "bi-clock"));
    this.icono.setAttribute("aria-hidden", "true");
    this.texto = el("span", "sf-texto");
    this.boton.appendChild(this.icono);
    this.boton.appendChild(this.texto);
    contenedor.appendChild(this.boton);
    this.contenedor = contenedor;

    this.boton.addEventListener("click", function () {
      if (abierto === self) {
        self.cerrar(false);
      } else {
        self.abrir();
      }
    });

    input.addEventListener("input", function () {
      self.estado = leer(input.value, self.conFecha);
      self.actualizarTexto();
    });
    input.addEventListener("change", function () {
      self.estado = leer(input.value, self.conFecha);
      self.actualizarTexto();
    });
    // Si el formulario no se puede enviar por este campo, el aviso lleva al botón
    input.addEventListener("invalid", function () {
      self.boton.classList.add("sf-invalido");
      self.boton.focus();
    });

    new MutationObserver(function () {
      self.actualizarTexto();
    }).observe(input, { attributes: true, attributeFilter: ["disabled"] });
  };

  // "Fecha de inicio", o "Lunes: Desde" en los horarios
  Selector.prototype.nombreDelCampo = function () {
    var grupo = this.input.closest(".cm-hora");
    var diaDeLaSemana = this.input.closest(".cm-dia");
    var nombreDia = diaDeLaSemana ? diaDeLaSemana.querySelector(".cm-dia-nombre") : null;
    if (grupo && grupo.firstElementChild) {
      return (nombreDia ? nombreDia.textContent.trim() + ": " : "") +
                grupo.firstElementChild.textContent.trim();
    }
    return this.etiqueta ? this.etiqueta.textContent.replace(/\(.*\)/, "").trim() : "Valor";
  };

  Selector.prototype.textoVisible = function () {
    var e = this.estado;
    var hora;
    if (e.hora === null) {
      return "";
    }
    hora = dos(e.hora) + ":" + dos(e.minuto);
    if (!this.conFecha) {
      return hora;
    }
    if (!e.fecha) {
      return "";
    }
    return DIAS_CORTOS[e.fecha.getDay()] + " " + e.fecha.getDate() + " " +
            MESES_CORTOS[e.fecha.getMonth()] + " " + e.fecha.getFullYear() + " · " + hora;
  };

  Selector.prototype.actualizarTexto = function () {
    var visible = this.textoVisible();
    var vacio = visible === "";
    this.texto.textContent = vacio ?
      (this.conFecha ? "Elegí fecha y hora" : "--:--") :
      visible;
    this.boton.classList.toggle("sf-vacio", vacio);
    this.boton.disabled = this.input.disabled;
    this.boton.setAttribute("aria-label", this.nombreDelCampo() + ": " +
            (vacio ? "sin elegir" : visible));
    if (!vacio) {
      this.boton.classList.remove("sf-invalido");
    }
  };

  Selector.prototype.limiteInferior = function () {
    var otro;
    var datos;
    var id = this.input.getAttribute("data-desde");
    if (!id) {
      return null;
    }
    otro = document.getElementById(id);
    datos = otro ? leer(otro.value, true) : null;
    return datos && datos.fecha ? datos.fecha : null;
  };

  Selector.prototype.abrir = function () {
    var self = this;
    if (abierto) {
      abierto.cerrar(false);
    }
    abierto = this;
    this.borrador = {
      fecha: this.estado.fecha,
      hora: this.estado.hora === null ? HORA_INICIAL : this.estado.hora,
      minuto: this.estado.minuto === null ? 0 : this.estado.minuto
    };
    var base = this.borrador.fecha || this.limiteInferior() || new Date();
    this.mes = new Date(base.getFullYear(), base.getMonth(), 1);

    this.fondo = el("div", "sf-fondo");
    this.fondo.addEventListener("click", function () {
      self.cerrar(false);
    });
    this.panel = el("div", "sf-panel");
    this.panel.setAttribute("role", "dialog");
    this.panel.setAttribute("aria-label", this.nombreDelCampo());
    this.panel.addEventListener("keydown", function (e) {
      self.teclado(e);
    });
    this.dibujar();

    document.body.appendChild(this.fondo);
    document.body.appendChild(this.panel);
    this.boton.setAttribute("aria-expanded", "true");
    this.posicionar();
    this.panel.classList.add("sf-visible");
    this.fondo.classList.add("sf-visible");
    this.enfocarInicial();

    this.alRedimensionar = function () {
      self.posicionar();
    };
    this.alTeclear = function (e) {
      if (e.key === "Escape" && abierto === self) {
        e.preventDefault();
        self.cerrar(false);
      }
    };
    document.addEventListener("keydown", this.alTeclear);
    window.addEventListener("resize", this.alRedimensionar);
    window.addEventListener("scroll", this.alRedimensionar, true);
  };

  Selector.prototype.enfocarInicial = function () {
    var inicial = this.panel.querySelector(".sf-dia-elegido:not(:disabled), .sf-hoy:not(:disabled), .sf-dia:not(:disabled)");
    var rueda = this.panel.querySelector(".sf-opcion[aria-selected='true']");
    var objetivo = this.conFecha ? inicial : rueda;
    if (objetivo) {
      objetivo.focus({ preventScroll: true });
    }
  };

  Selector.prototype.posicionar = function () {
    var r;
    var alto;
    var ancho;
    var izquierda;
    var arriba;
    var panel = this.panel;
    if (!panel) {
      return;
    }
    if (window.innerWidth < ANCHO_HOJA) {
      panel.classList.add("sf-hoja");
      panel.style.left = "";
      panel.style.top = "";
      return;
    }
    panel.classList.remove("sf-hoja");
    r = this.boton.getBoundingClientRect();
    ancho = panel.offsetWidth;
    alto = panel.offsetHeight;
    izquierda = Math.min(Math.max(8, r.left), window.innerWidth - ancho - 8);
    arriba = r.bottom + 8;
    if (arriba + alto > window.innerHeight - 8 && r.top - alto - 8 > 8) {
      arriba = r.top - alto - 8;
    }
    // Si no entra ni arriba ni abajo, se acomoda dentro de la pantalla
    arriba = Math.min(arriba, window.innerHeight - 8 - alto);
    panel.style.left = izquierda + "px";
    panel.style.top = Math.max(8, arriba) + "px";
  };

  Selector.prototype.dibujar = function () {
    var self = this;
    var pie;
    var cancelar;
    var listo;
    this.panel.textContent = "";
    if (!this.conFecha) {
      this.panel.classList.add("sf-solo-hora");
    }
    if (this.conFecha) {
      this.panel.appendChild(this.dibujarCalendario());
    }
    this.panel.appendChild(this.dibujarHora());

    pie = el("div", "sf-pie");
    cancelar = boton("sf-btn sf-btn-neutro", "Cancelar");
    listo = boton("sf-btn sf-btn-ok", "Listo");
    cancelar.addEventListener("click", function () {
      self.cerrar(false);
    });
    listo.addEventListener("click", function () {
      self.cerrar(true);
    });
    pie.appendChild(cancelar);
    pie.appendChild(listo);
    this.panel.appendChild(pie);
  };

  Selector.prototype.dibujarCalendario = function () {
    var self = this;
    var cal = el("div", "sf-calendario");
    var cab = el("div", "sf-mes-cabecera");
    var anterior = boton("sf-nav", "", "Mes anterior");
    var siguiente = boton("sf-nav", "", "Mes siguiente");
    var titulo = el("div", "sf-mes-titulo", MESES[this.mes.getMonth()] + " " + this.mes.getFullYear());
    var atajos = el("div", "sf-atajos");
    var grilla = el("div", "sf-grilla");
    var primero = new Date(this.mes.getFullYear(), this.mes.getMonth(), 1);
    var desplazamiento = (primero.getDay() + 6) % 7;
    var total = new Date(this.mes.getFullYear(), this.mes.getMonth() + 1, 0).getDate();
    var minimo = this.limiteInferior();
    var hoy = dia(new Date());
    var i;

    anterior.innerHTML = "<i class=\"bi bi-chevron-left\" aria-hidden=\"true\"></i>";
    siguiente.innerHTML = "<i class=\"bi bi-chevron-right\" aria-hidden=\"true\"></i>";
    titulo.setAttribute("aria-live", "polite");
    anterior.addEventListener("click", function () {
      self.mes = new Date(self.mes.getFullYear(), self.mes.getMonth() - 1, 1);
      self.redibujarCalendario();
    });
    siguiente.addEventListener("click", function () {
      self.mes = new Date(self.mes.getFullYear(), self.mes.getMonth() + 1, 1);
      self.redibujarCalendario();
    });
    cab.appendChild(anterior);
    cab.appendChild(titulo);
    cab.appendChild(siguiente);
    cal.appendChild(cab);

    this.agregarAtajos(atajos, hoy, minimo);
    cal.appendChild(atajos);

    for (i = 0; i < DIAS_SEMANA.length; i++) {
      grilla.appendChild(el("span", "sf-semana", DIAS_SEMANA[i]));
    }
    for (i = 0; i < desplazamiento; i++) {
      grilla.appendChild(el("span", "sf-hueco"));
    }
    for (i = 1; i <= total; i++) {
      grilla.appendChild(this.celdaDelDia(new Date(this.mes.getFullYear(), this.mes.getMonth(), i), hoy, minimo));
    }
    cal.appendChild(grilla);
    return cal;
  };

  Selector.prototype.celdaDelDia = function (fecha, hoy, minimo) {
    var self = this;
    var b = boton("sf-dia", String(fecha.getDate()));
    b.setAttribute("aria-label", DIAS_CORTOS[fecha.getDay()] + " " + fecha.getDate() + " de " +
            MESES[fecha.getMonth()] + " de " + fecha.getFullYear());
    if (mismoDia(fecha, hoy)) {
      b.classList.add("sf-hoy");
    }
    if (this.borrador.fecha && mismoDia(fecha, this.borrador.fecha)) {
      b.classList.add("sf-dia-elegido");
      b.setAttribute("aria-pressed", "true");
    }
    if (minimo && fecha < dia(minimo)) {
      b.disabled = true;
    }
    b.addEventListener("click", function () {
      self.borrador.fecha = fecha;
      self.redibujarCalendario();
      self.panel.querySelector(".sf-dia-elegido").focus({ preventScroll: true });
    });
    return b;
  };

  Selector.prototype.agregarAtajos = function (contenedor, hoy, minimo) {
    var self = this;
    var base = minimo ? dia(minimo) : hoy;
    var opciones = minimo ?
      [["Mismo día", 0], ["+1 semana", 7], ["+15 días", 15], ["+1 mes", 30]] :
      [["Hoy", 0], ["Mañana", 1], ["En 1 semana", 7]];
    opciones.forEach(function (op) {
      var b = boton("sf-atajo", op[0]);
      b.addEventListener("click", function () {
        var f = new Date(base.getFullYear(), base.getMonth(), base.getDate() + op[1]);
        self.borrador.fecha = f;
        self.mes = new Date(f.getFullYear(), f.getMonth(), 1);
        self.redibujarCalendario();
      });
      contenedor.appendChild(b);
    });
  };

  Selector.prototype.redibujarCalendario = function () {
    var nuevo = this.dibujarCalendario();
    var viejo = this.panel.querySelector(".sf-calendario");
    this.panel.replaceChild(nuevo, viejo);
    this.posicionar();
  };

  Selector.prototype.dibujarHora = function () {
    var zona = el("div", "sf-hora");
    var titulo = el("div", "sf-hora-titulo");
    var ruedas = el("div", "sf-ruedas");
    var horas = [];
    var minutos = [];
    var i;
    for (i = 0; i < 24; i++) {
      horas.push(i);
    }
    for (i = 0; i < 60; i += PASO_MINUTOS) {
      minutos.push(i);
    }
    if (minutos.indexOf(this.borrador.minuto) === -1) {
      minutos.push(this.borrador.minuto);
      minutos.sort(function (a, b) {
        return a - b;
      });
    }
    titulo.textContent = "Hora";
    zona.appendChild(titulo);
    ruedas.appendChild(this.rueda("Horas", horas, "hora"));
    ruedas.appendChild(el("span", "sf-dos-puntos", ":"));
    ruedas.appendChild(this.rueda("Minutos", minutos, "minuto"));
    zona.appendChild(ruedas);
    return zona;
  };

  Selector.prototype.rueda = function (nombre, valores, campo) {
    var self = this;
    var rueda = el("div", "sf-rueda");
    rueda.setAttribute("role", "listbox");
    rueda.setAttribute("aria-label", nombre);
    valores.forEach(function (v) {
      var o = boton("sf-opcion", dos(v));
      var elegido = self.borrador[campo] === v;
      o.setAttribute("role", "option");
      o.setAttribute("aria-selected", elegido ? "true" : "false");
      o.tabIndex = elegido ? 0 : -1;
      o.addEventListener("click", function () {
        self.elegirEnRueda(rueda, o, campo, v);
      });
      rueda.appendChild(o);
    });
    // El scroll inicial se hace cuando el panel ya está en pantalla
    window.requestAnimationFrame(function () {
      var elegida = rueda.querySelector("[aria-selected='true']");
      if (elegida) {
        rueda.scrollTop = elegida.offsetTop - (rueda.clientHeight - elegida.offsetHeight) / 2;
      }
    });
    return rueda;
  };

  Selector.prototype.elegirEnRueda = function (rueda, opcion, campo, valor) {
    var previa = rueda.querySelector("[aria-selected='true']");
    this.borrador[campo] = valor;
    if (previa) {
      previa.setAttribute("aria-selected", "false");
      previa.tabIndex = -1;
    }
    opcion.setAttribute("aria-selected", "true");
    opcion.tabIndex = 0;
    opcion.focus({ preventScroll: true });
    rueda.scrollTo({
      top: opcion.offsetTop - (rueda.clientHeight - opcion.offsetHeight) / 2,
      behavior: window.matchMedia("(prefers-reduced-motion: reduce)").matches ? "auto" : "smooth"
    });
  };

  Selector.prototype.teclado = function (e) {
    var actual = document.activeElement;
    var hermano;
    if (actual && actual.classList.contains("sf-opcion") &&
            (e.key === "ArrowDown" || e.key === "ArrowUp")) {
      e.preventDefault();
      hermano = e.key === "ArrowDown" ? actual.nextElementSibling : actual.previousElementSibling;
      if (hermano && hermano.classList.contains("sf-opcion")) {
        hermano.click();
      }
      return;
    }
    if (e.key === "Tab") {
      this.atraparFoco(e);
    }
  };

  Selector.prototype.atraparFoco = function (e) {
    var foco = this.panel.querySelectorAll("button:not([disabled]):not([tabindex='-1'])");
    var primero = foco[0];
    var ultimo = foco[foco.length - 1];
    if (!primero) {
      return;
    }
    if (e.shiftKey && document.activeElement === primero) {
      e.preventDefault();
      ultimo.focus();
    } else if (!e.shiftKey && document.activeElement === ultimo) {
      e.preventDefault();
      primero.focus();
    }
  };

  Selector.prototype.cerrar = function (aplicar) {
    var panel = this.panel;
    var fondo = this.fondo;
    if (!panel) {
      return;
    }
    if (aplicar) {
      this.aplicar();
    }
    document.removeEventListener("keydown", this.alTeclear);
    window.removeEventListener("resize", this.alRedimensionar);
    window.removeEventListener("scroll", this.alRedimensionar, true);
    this.boton.setAttribute("aria-expanded", "false");
    panel.classList.remove("sf-visible");
    fondo.classList.remove("sf-visible");
    this.panel = null;
    this.fondo = null;
    abierto = null;
    setTimeout(function () {
      panel.remove();
      fondo.remove();
    }, 180);
    this.boton.focus({ preventScroll: true });
  };

  Selector.prototype.aplicar = function () {
    var b = this.borrador;
    var valor;
    if (this.conFecha && !b.fecha) {
      return;
    }
    valor = dos(b.hora) + ":" + dos(b.minuto);
    if (this.conFecha) {
      valor = aTexto(b.fecha) + "T" + valor;
    }
    this.input.value = valor;
    this.estado = leer(valor, this.conFecha);
    this.actualizarTexto();
    this.input.dispatchEvent(new Event("input", { bubbles: true }));
    this.input.dispatchEvent(new Event("change", { bubbles: true }));
  };

  function iniciar() {
    document.querySelectorAll("input.js-selector").forEach(function (input) {
      if (!input.dataset.selectorListo) {
        input.dataset.selectorListo = "1";
        new Selector(input);
      }
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", iniciar);
  } else {
    iniciar();
  }
}());