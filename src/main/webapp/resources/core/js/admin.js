// Panel de administración: barras proporcionales, filtro de consultas y confirmación de aprobaciones
const raiz = document.querySelector(".ad");

if (raiz) {
  const numero = (texto) => Number(texto) || 0;

  // Cada ranking pinta sus barras en proporción a su valor más alto
  raiz.querySelectorAll("[data-barras]").forEach((lista) => {
    const filas = Array.from(lista.querySelectorAll("[data-valor]"));
    const maximo = Math.max(1, ...filas.map((fila) => numero(fila.dataset.valor)));
    filas.forEach((fila) => {
      const barra = fila.querySelector(".ad-barra > span");
      if (barra) {
        barra.style.width = `${(numero(fila.dataset.valor) / maximo) * 100}%`;
      }
    });
  });

  // Filtro de consultas por texto (nombre, email, asunto, mensaje)
  const buscador = document.getElementById("ad-buscar");
  const contador = document.getElementById("ad-contador");
  const sinResultados = document.getElementById("ad-sin-resultados");
  const consultas = Array.from(raiz.querySelectorAll(".ad-consulta[data-texto]"));

  if (buscador) {
    const filtrar = () => {
      const texto = buscador.value.trim().toLowerCase();
      let visibles = 0;
      consultas.forEach((consulta) => {
        const coincide = consulta.dataset.texto.includes(texto);
        consulta.hidden = !coincide;
        visibles += coincide ? 1 : 0;
      });
      if (contador) {
        contador.textContent = `${visibles} de ${consultas.length} consultas`;
      }
      if (sinResultados) {
        sinResultados.hidden = visibles > 0;
      }
    };
    buscador.addEventListener("input", filtrar);
  }

  // Aprobar una verificación pide confirmación en un diálogo accesible
  const dialogo = document.getElementById("ad-confirmar");
  if (dialogo && typeof dialogo.showModal === "function") {
    const detalle = document.getElementById("ad-confirmar-detalle");
    const aceptar = document.getElementById("ad-confirmar-si");
    const cancelar = document.getElementById("ad-confirmar-no");
    let formularioPendiente = null;

    raiz.querySelectorAll("form[data-confirmar]").forEach((formulario) => {
      formulario.addEventListener("submit", (evento) => {
        evento.preventDefault();
        formularioPendiente = formulario;
        detalle.textContent = formulario.dataset.confirmar;
        dialogo.showModal();
      });
    });

    aceptar.addEventListener("click", () => {
      dialogo.close();
      if (formularioPendiente) {
        formularioPendiente.submit();
      }
    });
    cancelar.addEventListener("click", () => {
      formularioPendiente = null;
      dialogo.close();
    });
  }
}