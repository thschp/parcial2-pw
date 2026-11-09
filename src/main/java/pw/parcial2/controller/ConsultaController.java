package pw.parcial2.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import pw.parcial2.model.Consulta;
import pw.parcial2.model.Usuario;
import pw.parcial2.repository.ConsultaRepo;
import pw.parcial2.repository.MedicoRepo;
import pw.parcial2.repository.UsuarioRepo;

@RestController
@RequestMapping("/api/consultas")
@Tag(name = "Consultas", description = "Administración y consulta de citas médicas")
@ApiResponses({
	@ApiResponse(responseCode = "200", description = "Solicitud exitosa"),
	@ApiResponse(responseCode = "401", description = "Debe iniciar sesión"),
	@ApiResponse(responseCode = "403", description = "El rol no tiene permiso")
})
public class ConsultaController {
	@Autowired
	private ConsultaRepo repo;
	@Autowired
	private MedicoRepo medicos;
	@Autowired
	private UsuarioRepo usuarios;

	@GetMapping
	@Operation(summary = "Listar consultas", description = "Admin ve todas; médico y paciente solo las asignadas a su cuenta.")
	public List<Consulta> lista(Authentication usuario) {
		if (tiene(usuario, "ROLE_ADMIN")) return repo.findAll();
		if (tiene(usuario, "ROLE_MEDICO")) return repo.findByMedicoCuentaUsername(usuario.getName());
		return repo.findByPacienteCuentaUsername(usuario.getName());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Consultar una cita", description = "El usuario autenticado consulta una cita visible para su rol.")
	public ResponseEntity<?> ver(@PathVariable Long id, Authentication usuario) {
		return repo.findById(id).<ResponseEntity<?>>map(c -> visible(c, usuario) ? ResponseEntity.ok(c) : ResponseEntity.status(403).body("- No tiene acceso a esta consulta"))
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	@Operation(summary = "Crear una cita", description = "Solo ADMIN. Ejemplo: {\"paciente\":\"Laura Gómez\",\"motivo\":\"Control general\",\"consultorio\":3,\"horaInicio\":\"2026-11-10T09:00:00\",\"horaFin\":\"2026-11-10T09:30:00\",\"medico\":{\"id\":1},\"pacienteCuenta\":{\"id\":3}}.")
	public ResponseEntity<?> crear(@Valid @RequestBody Consulta datos) {
		if (!horarioValido(datos)) return ResponseEntity.badRequest().body("- La hora de inicio debe ser anterior a la hora de finalización; ejemplo: 09:00 a 09:30");
		if (!relacionesValidas(datos)) return ResponseEntity.badRequest().body("- Seleccione un médico y una cuenta de paciente existentes; ejemplo: médico 1 y paciente 3");
		datos.setMedico(medicos.findById(datos.getMedico().getId()).orElseThrow());
		datos.setPacienteCuenta(usuarios.findById(datos.getPacienteCuenta().getId()).orElseThrow());
		return ResponseEntity.ok(repo.save(datos));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Editar una cita", description = "Solo ADMIN. Recibe los datos completos; ejemplo: {\"paciente\":\"Laura Gómez\",\"motivo\":\"Control general\",\"consultorio\":3,\"horaInicio\":\"2026-11-10T09:00:00\",\"horaFin\":\"2026-11-10T09:30:00\",\"medico\":{\"id\":1},\"pacienteCuenta\":{\"id\":3}}.")
	public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody Consulta datos) {
		if (!horarioValido(datos)) return ResponseEntity.badRequest().body("- La hora de inicio debe ser anterior a la hora de finalización; ejemplo: 09:00 a 09:30");
		if (!relacionesValidas(datos)) return ResponseEntity.badRequest().body("- Seleccione un médico y una cuenta de paciente existentes; ejemplo: médico 1 y paciente 3");
		Consulta actual = repo.findById(id).orElse(null);
		if (actual == null) return ResponseEntity.notFound().build();
		datos.setMedico(medicos.findById(datos.getMedico().getId()).orElseThrow());
		datos.setPacienteCuenta(usuarios.findById(datos.getPacienteCuenta().getId()).orElseThrow());
		actual.setPaciente(datos.getPaciente()); actual.setMotivo(datos.getMotivo()); actual.setConsultorio(datos.getConsultorio());
		actual.setHoraInicio(datos.getHoraInicio()); actual.setHoraFin(datos.getHoraFin());
		actual.setMedico(datos.getMedico()); actual.setPacienteCuenta(datos.getPacienteCuenta());
		return ResponseEntity.ok(repo.save(actual));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar una cita", description = "Solo ADMIN. Elimina la cita indicada por su identificador.")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		if (!repo.existsById(id)) return ResponseEntity.<String>notFound().build();
		repo.deleteById(id);
		return ResponseEntity.ok("Consulta eliminada");
	}

	@PutMapping("/{id}/horario")
	@Operation(summary = "Cambiar horario", description = "Solo el PACIENTE asignado. Cuerpo de ejemplo: {\"horaInicio\":\"2026-11-10T10:00:00\",\"horaFin\":\"2026-11-10T10:30:00\"}.")
	public ResponseEntity<?> horario(@PathVariable Long id, @RequestBody Map<String, String> datos, Authentication usuario) {
		Consulta consulta = repo.findById(id).orElse(null);
		if (consulta == null) return ResponseEntity.notFound().build();
		if (!consulta.getPacienteCuenta().getUsername().equals(usuario.getName())) return ResponseEntity.status(403).body("- Solo puede cambiar el horario de una cita asignada a su cuenta");
		try {
			LocalDateTime inicio = LocalDateTime.parse(datos.get("horaInicio"));
			LocalDateTime fin = LocalDateTime.parse(datos.get("horaFin"));
			if (!inicio.isBefore(fin)) return ResponseEntity.badRequest().body("- La hora de inicio debe ser anterior a la hora de finalización; ejemplo: 10:00 a 10:30");
			consulta.setHoraInicio(inicio); consulta.setHoraFin(fin);
			return ResponseEntity.ok(repo.save(consulta));
		} catch (DateTimeParseException | NullPointerException e) {
			return ResponseEntity.badRequest().body("- Use el formato AAAA-MM-DDTHH:mm:ss; ejemplo: 2026-11-10T10:00:00");
		}
	}

	private boolean visible(Consulta c, Authentication u) {
		return tiene(u, "ROLE_ADMIN") || tiene(u, "ROLE_MEDICO") && c.getMedico().getCuenta().getUsername().equals(u.getName())
			|| tiene(u, "ROLE_PACIENTE") && c.getPacienteCuenta().getUsername().equals(u.getName());
	}

	private boolean tiene(Authentication usuario, String rol) {
		return usuario.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rol));
	}

	private boolean horarioValido(Consulta c) {
		return c.getHoraInicio() != null && c.getHoraFin() != null && c.getHoraInicio().isBefore(c.getHoraFin());
	}

	private boolean relacionesValidas(Consulta c) {
		return c.getMedico() != null && c.getMedico().getId() != null && medicos.existsById(c.getMedico().getId())
			&& c.getPacienteCuenta() != null && c.getPacienteCuenta().getId() != null
			&& usuarios.findById(c.getPacienteCuenta().getId()).map(u -> "PACIENTE".equals(u.getRol())).orElse(false);
	}
}
