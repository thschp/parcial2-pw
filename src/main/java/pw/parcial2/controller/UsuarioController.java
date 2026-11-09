package pw.parcial2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import pw.parcial2.model.Usuario;
import pw.parcial2.repository.UsuarioRepo;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Cuentas", description = "Cuentas disponibles para las asignaciones")
@ApiResponses({
	@ApiResponse(responseCode = "200", description = "Solicitud exitosa"),
	@ApiResponse(responseCode = "401", description = "Debe iniciar sesión"),
	@ApiResponse(responseCode = "403", description = "El rol no tiene permiso")
})
public class UsuarioController {
	@Autowired
	private UsuarioRepo repo;

	@GetMapping("/medicos")
	@Operation(summary = "Listar cuentas de médicos", description = "Solo ADMIN usa esta lista al registrar un médico.")
	public List<Usuario> medicos() {
		return repo.findByRol("MEDICO");
	}

	@GetMapping("/pacientes")
	@Operation(summary = "Listar cuentas de pacientes", description = "Solo ADMIN usa esta lista para asignar una cita.")
	public List<Usuario> pacientes() {
		return repo.findByRol("PACIENTE");
	}
}
