<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<!doctype html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Mis consultas</title>
<%@ include file="style01.jspf" %>
</head>
<body>
<h1>Mis consultas</h1>
<p><a href="/logout">Cerrar sesión</a></p>
<table><thead><tr><th>Consultorio</th><th>Paciente</th><th>Motivo</th><th>Horario</th><th>Médico</th><th>Editar horario</th></tr></thead><tbody id="consultas"></tbody></table>
<section id="editar" hidden>
<h2>Actualizar horario de consultorio <span id="numero"></span></h2>
<label><input id="habilitar" type="checkbox"> Habilitar cambio de horario</label>
<form id="horarioForm">
<label>Inicio <input id="horaInicio" type="datetime-local" disabled required></label>
<label>Finalización <input id="horaFin" type="datetime-local" disabled required></label>
<button id="guardar" type="submit" disabled>Guardar horario</button>
</form>
</section>
<script>
async function cargar() {
  const res = await fetch("/api/consultas");
  if (!res.ok) { location.href = "/403"; return; }
  const lista = await res.json(), cuerpo = document.querySelector("#consultas"); cuerpo.replaceChildren();
  lista.forEach(c => {
    const fila = document.createElement("tr");
    [c.consultorio, c.paciente, c.motivo, `${c.horaInicio.replace("T", " ")} a ${c.horaFin.substring(11, 16)}`, c.medico.nombre].forEach(valor => {
      const td = document.createElement("td"); td.textContent = valor; fila.appendChild(td);
    });
    const accion = document.createElement("td"), boton = document.createElement("button");
    boton.textContent = "Cambiar horario"; boton.onclick = () => seleccionar(c); accion.appendChild(boton); fila.appendChild(accion); cuerpo.appendChild(fila);
  });
}
function seleccionar(c) {
  const id = String(c.id ?? "").trim();
  if (!/^\d+$/.test(id)) { alert("No se pudo identificar la consulta"); return; }
  document.querySelector("#horarioForm").dataset.id = id;
  document.querySelector("#numero").textContent = c.consultorio;
  document.querySelector("#horaInicio").value = c.horaInicio.substring(0, 16); document.querySelector("#horaFin").value = c.horaFin.substring(0, 16);
  document.querySelector("#editar").hidden = false; document.querySelector("#habilitar").checked = false; activar(false);
}
function activar(activo) { document.querySelector("#horaInicio").disabled = !activo; document.querySelector("#horaFin").disabled = !activo; document.querySelector("#guardar").disabled = !activo; }
document.querySelector("#habilitar").onchange = e => activar(e.target.checked);
document.querySelector("#horarioForm").onsubmit = async e => {
  e.preventDefault();
  const id = e.currentTarget.dataset.id || "";
  if (!/^\d+$/.test(id)) { alert("Seleccione una consulta antes de guardar"); return; }
  const datos = { horaInicio: document.querySelector("#horaInicio").value, horaFin: document.querySelector("#horaFin").value };
  const res = await fetch(`/api/consultas/${encodeURIComponent(id)}/horario`, { method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify(datos) });
  if (res.status === 400) { alert("Revise los datos:\n" + await res.text()); return; }
  if (!res.ok) { location.href = "/403"; return; }
  document.querySelector("#editar").hidden = true; delete e.currentTarget.dataset.id; cargar();
};
cargar();
</script>
</body>
</html>
