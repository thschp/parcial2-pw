<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Inicio de sesión</title>
<%@ include file="style01.jspf" %>
</head>
<body>
<h1>Consultas médicas</h1>
<h2>Iniciar sesión</h2>
<form action="/login" method="post">
<label>Usuario <input name="username" required></label>
<label>Contraseña <input name="password" type="password" required></label>
<button type="submit">Ingresar</button>
</form>
<% if (request.getParameter("error") != null) { %>
<p class="error">Usuario o contraseña incorrectos</p>
<% } %>
<p>Usuarios de prueba: admin / admin123, medico / medico123, paciente / paciente123</p>
<p>Para cada médico, la contraseña es medico123</p>
</body>
</html>
