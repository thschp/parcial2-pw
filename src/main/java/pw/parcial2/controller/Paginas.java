package pw.parcial2.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Vistas", description = "Páginas web de la aplicación")
@ApiResponses({
	@ApiResponse(responseCode = "200", description = "Página cargada"),
	@ApiResponse(responseCode = "401", description = "Debe iniciar sesión"),
	@ApiResponse(responseCode = "403", description = "El rol no tiene permiso")
})
public class Paginas {
	@GetMapping("/")
	@Operation(summary = "Ir al inicio", description = "Redirige a la vista del rol del usuario autenticado.")
	public String inicio(Authentication usuario) {
		if (usuario == null) return "redirect:/login";
		if (usuario.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) return "redirect:/admin";
		if (usuario.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEDICO"))) return "redirect:/medico";
		return "redirect:/paciente";
	}

	@GetMapping("/login")
	@Operation(summary = "Iniciar sesión", description = "Muestra el formulario público de inicio de sesión.")
	public String login() {
		return "login";
	}

	@GetMapping("/403")
	@Operation(summary = "Ver acceso restringido", description = "Muestra la página pública cuando el rol no tiene permiso.")
	public String denegado() {
		return "403";
	}

	@GetMapping("/admin")
	@Operation(summary = "Panel de administración", description = "Solo ADMIN registra y gestiona médicos y consultas.")
	public String admin() {
		return "admin";
	}

	@GetMapping("/medico")
	@Operation(summary = "Panel del médico", description = "Solo MEDICO ve las consultas asignadas a su cuenta.")
	public String medico() {
		return "medico";
	}

	@GetMapping("/paciente")
	@Operation(summary = "Panel del paciente", description = "Solo PACIENTE ve sus consultas y puede cambiar sus horarios.")
	public String paciente() {
		return "paciente";
	}
}
