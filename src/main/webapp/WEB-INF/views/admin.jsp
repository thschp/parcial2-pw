<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<!doctype html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Administración</title>
<%@ include file="style01.jspf" %>
</head>
<body>
<h1>Administración de consultas médicas</h1>
<p><a href="/swagger-ui.html">Documentacion de servicios</a> | <a href="/logout">Cerrar sesión</a></p>
<h2>Registrar o editar consulta</h2>
<form id="consultaForm">
<label>Nombre del paciente <input id="paciente" maxlength="40" required></label>
<label>Motivo <input id="motivo" maxlength="100" required></label>
<label>Número de consultorio <input id="consultorio" type="number" min="1" max="99" required></label>
<label>Inicio <input id="horaInicio" type="datetime-local" required></label>
<label>Finalización <input id="horaFin" type="datetime-local" required></label>
<label>Médico <select id="medico" required></select></label>
<label>Cuenta del paciente <select id="pacienteCuenta" required></select></label>
<button id="consultaEnviar" type="submit">Guardar consulta</button>
<button type="button" onclick="limpiarConsulta()">Limpiar</button>
</form>
<h2>Consultas</h2>
<table><thead><tr><th>Consultorio</th><th>Paciente</th><th>Motivo</th><th>Horario</th><th>Médico</th><th>Acciones</th></tr></thead><tbody id="consultas"></tbody></table>
<h2>Registrar o editar médico</h2>
<form id="medicoForm">
<label>Nombre <input id="nombreMedico" maxlength="60" required></label>
<label>Especialidad <input id="especialidad" maxlength="60" required></label>
<button type="submit">Guardar médico</button>
<button type="button" onclick="limpiarMedico()">Limpiar</button>
</form>
<h2>Médicos</h2>
<table><thead><tr><th>Nombre</th><th>Especialidad</th><th>Usuario de acceso</th><th>Acciones</th></tr></thead><tbody id="medicos"></tbody></table>
<script>
async function api(url, options = {}) {
  const res = await fetch(url, options);
  const texto = await res.text();
  if (!res.ok) throw new Error(texto || `Error ${res.status}`);
  return texto && (res.headers.get("content-type") || "").includes("json") ? JSON.parse(texto) : texto;
}
function json(datos) { return { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(datos) }; }
function celda(fila, texto) { const td = document.createElement("td"); td.textContent = texto ?? ""; fila.appendChild(td); }
function option(select, id, texto) { const op = document.createElement("option"); op.value = id; op.textContent = texto; select.appendChild(op); }
async function cargar() {
  try {
    const [consultas, medicos, pacientes] = await Promise.all([
      api("/api/consultas"), api("/api/medicos"), api("/api/usuarios/pacientes")
    ]);
    document.querySelector("#medico").replaceChildren();
    document.querySelector("#pacienteCuenta").replaceChildren();
    option(document.querySelector("#medico"), "", "Seleccione un médico");
    option(document.querySelector("#pacienteCuenta"), "", "Seleccione la cuenta del paciente");
    medicos.forEach(m => option(document.querySelector("#medico"), m.id, `${m.nombre} — ${m.especialidad}`));
    pacientes.forEach(p => option(document.querySelector("#pacienteCuenta"), p.id, p.username));
    const cuerpo = document.querySelector("#consultas"); cuerpo.replaceChildren();
    consultas.forEach(c => {
      const fila = document.createElement("tr");
      celda(fila, c.consultorio); celda(fila, c.paciente); celda(fila, c.motivo);
      celda(fila, `${c.horaInicio.replace("T", " ")} a ${c.horaFin.substring(11, 16)}`); celda(fila, c.medico.nombre);
      const acciones = document.createElement("td");
      const editar = document.createElement("button"); editar.textContent = "Editar"; editar.onclick = () => editarConsulta(c);
      const borrar = document.createElement("button"); borrar.textContent = "Eliminar"; borrar.onclick = () => eliminar(`/api/consultas/${c.id}`, cargar);
      acciones.append(editar, borrar); fila.appendChild(acciones); cuerpo.appendChild(fila);
    });
    const tabla = document.querySelector("#medicos"); tabla.replaceChildren();
    medicos.forEach(m => {
      const fila = document.createElement("tr"); celda(fila, m.nombre); celda(fila, m.especialidad); celda(fila, m.cuenta.username);
      const acciones = document.createElement("td");
      const editar = document.createElement("button"); editar.textContent = "Editar"; editar.onclick = () => editarMedico(m);
      const borrar = document.createElement("button"); borrar.textContent = "Eliminar"; borrar.onclick = () => eliminar(`/api/medicos/${m.id}`, cargar);
      acciones.append(editar, borrar); fila.appendChild(acciones); tabla.appendChild(fila);
    });
  } catch (e) { alert(e.message); }
}
function editarConsulta(c) {
  const id = String(c.id ?? "").trim();
  if (!/^\d+$/.test(id)) { alert("No se pudo identificar la consulta seleccionada"); return; }
  const form = document.querySelector("#consultaForm"); form.dataset.id = id;
  document.querySelector("#paciente").value = c.paciente;
  document.querySelector("#motivo").value = c.motivo; document.querySelector("#consultorio").value = c.consultorio;
  document.querySelector("#horaInicio").value = c.horaInicio.substring(0, 16); document.querySelector("#horaFin").value = c.horaFin.substring(0, 16);
  document.querySelector("#medico").value = c.medico.id; document.querySelector("#pacienteCuenta").value = c.pacienteCuenta.id;
  document.querySelector("#consultaEnviar").textContent = "Actualizar consulta";
}
function limpiarConsulta() {
  const form = document.querySelector("#consultaForm"); form.reset(); delete form.dataset.id;
  document.querySelector("#consultaEnviar").textContent = "Guardar consulta";
}
function editarMedico(m) {
  const form = document.querySelector("#medicoForm"); form.dataset.id = String(m.id);
  document.querySelector("#nombreMedico").value = m.nombre; document.querySelector("#especialidad").value = m.especialidad;
}
function limpiarMedico() { const form = document.querySelector("#medicoForm"); form.reset(); delete form.dataset.id; }
async function eliminar(url, hecho) {
  if (!confirm("¿Confirma que desea eliminar este registro?")) return;
  try { await api(url, { method: "DELETE" }); hecho(); } catch (e) { alert(e.message); }
}
document.querySelector("#consultaForm").onsubmit = async e => {
  e.preventDefault();
  const id = e.currentTarget.dataset.id || "";
  const esEdicion = document.querySelector("#consultaEnviar").textContent === "Actualizar consulta";
  if (esEdicion && !/^\d+$/.test(id)) { alert("Seleccione una consulta antes de actualizar"); return; }
  const datos = {
    paciente: document.querySelector("#paciente").value, motivo: document.querySelector("#motivo").value,
    consultorio: Number(document.querySelector("#consultorio").value), horaInicio: document.querySelector("#horaInicio").value,
    horaFin: document.querySelector("#horaFin").value, medico: { id: Number(document.querySelector("#medico").value) },
    pacienteCuenta: { id: Number(document.querySelector("#pacienteCuenta").value) }
  };
  try { const url = id ? `/api/consultas/${encodeURIComponent(id)}` : "/api/consultas"; await api(url, { ...json(datos), method: id ? "PUT" : "POST" }); limpiarConsulta(); cargar(); }
  catch (e) { alert("Revise los datos:\n" + e.message); }
};
document.querySelector("#medicoForm").onsubmit = async e => {
  e.preventDefault();
  const datos = { nombre: document.querySelector("#nombreMedico").value, especialidad: document.querySelector("#especialidad").value };
  const id = e.currentTarget.dataset.id || "";
  try { await api(id ? `/api/medicos/${id}` : "/api/medicos", { ...json(datos), method: id ? "PUT" : "POST" }); limpiarMedico(); cargar(); }
  catch (e) { alert("Revise los datos:\n" + e.message); }
};
cargar();
</script>
</body>
</html>
