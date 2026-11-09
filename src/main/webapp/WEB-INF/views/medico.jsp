<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Consultas asignadas</title>
<%@ include file="style01.jspf" %>
</head>
<body>
<h1>Consultas asignadas</h1>
<p><a href="/logout">Cerrar sesión</a></p>
<table><thead><tr><th>Consultorio</th><th>Paciente</th><th>Motivo</th><th>Horario</th><th>Médico</th></tr></thead><tbody id="consultas"></tbody></table>
<script>
async function cargar() {
  const res = await fetch("/api/consultas");
  if (!res.ok) { location.href = "/403"; return; }
  const lista = await res.json(), cuerpo = document.querySelector("#consultas");
  lista.forEach(c => {
    const fila = document.createElement("tr");
    [c.consultorio, c.paciente, c.motivo, `${c.horaInicio.replace("T", " ")} a ${c.horaFin.substring(11, 16)}`, c.medico.nombre].forEach(valor => {
      const td = document.createElement("td"); td.textContent = valor; fila.appendChild(td);
    });
    cuerpo.appendChild(fila);
  });
}
cargar();
</script>
</body>
</html>
