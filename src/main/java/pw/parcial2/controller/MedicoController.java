package pw.parcial2.controller;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import pw.parcial2.model.Medico;
import pw.parcial2.model.Usuario;
import pw.parcial2.repository.ConsultaRepo;
import pw.parcial2.repository.MedicoRepo;
import pw.parcial2.repository.UsuarioRepo;

@RestController
@RequestMapping("/api/medicos")
@Tag(name = "Médicos", description = "Catálogo de médicos para asignar citas")
@ApiResponses({
	@ApiResponse(responseCode = "200", description = "Solicitud exitosa"),
	@ApiResponse(responseCode = "401", description = "Debe iniciar sesión"),
	@ApiResponse(responseCode = "403", description = "El rol no tiene permiso")
})
public class MedicoController {
	@Autowired
	private MedicoRepo repo;
	@Autowired
	private ConsultaRepo consultas;
	@Autowired
	private UsuarioRepo usuarios;
	@Autowired
	private PasswordEncoder codificador;

	@GetMapping
	@Operation(summary = "Listar médicos", description = "Cualquier usuario autenticado puede consultar el catálogo.")
	public List<Medico> lista() {
		return repo.findAll();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Ver médico", description = "Cualquier usuario autenticado consulta un médico por id.")
	public ResponseEntity<Medico> ver(@PathVariable Long id) {
		return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	@Operation(summary = "Crear médico", description = "Solo ADMIN. Crea su usuario con clave medico123. Ejemplo: {\"nombre\":\"Gregorio Casas\",\"especialidad\":\"Pediatría\"}.")
	@Transactional
	public ResponseEntity<?> crear(@Valid @RequestBody Medico datos) {
		datos.setCuenta(crearCuenta(datos.getNombre()));
		return ResponseEntity.ok(repo.save(datos));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Editar médico", description = "Solo ADMIN. Conserva el usuario propio del médico. Ejemplo: {\"nombre\":\"Gregorio Casas\",\"especialidad\":\"Pediatría\"}.")
	public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody Medico datos) {
		Medico actual = repo.findById(id).orElse(null);
		if (actual == null) return ResponseEntity.notFound().build();
		actual.setNombre(datos.getNombre()); actual.setEspecialidad(datos.getEspecialidad());
		return ResponseEntity.ok(repo.save(actual));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar médico", description = "Solo ADMIN. Elimina un médico si no tiene citas asociadas.")
	@Transactional
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		if (!repo.existsById(id)) return ResponseEntity.<String>notFound().build();
		if (consultas.existsByMedicoId(id)) return ResponseEntity.status(409).body("No se puede eliminar porque tiene consultas asignadas");
		Medico medico = repo.findById(id).orElseThrow();
		Long cuentaId = medico.getCuenta().getId();
		repo.delete(medico);
		repo.flush();
		if (!repo.existsByCuentaId(cuentaId)) usuarios.deleteById(cuentaId);
		return ResponseEntity.ok("Médico eliminado");
	}

	private Usuario crearCuenta(String nombre) {
		String base = Normalizer.normalize(nombre, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT)
			.replaceAll("[^a-z0-9]+", ".").replaceAll("^\\.|\\.$", "");
		if (base.length() < 3) base = "medico" + base;
		base = base.substring(0, Math.min(35, base.length())).replaceAll("\\.$", "");
		String username = base;
		int sufijo = 2;
		while (usuarios.findByUsername(username).isPresent()) {
			String numero = String.valueOf(sufijo++);
			username = base.substring(0, Math.min(base.length(), 40 - numero.length())) + numero;
		}
		Usuario usuario = new Usuario();
		usuario.setUsername(username);
		usuario.setPassword(codificador.encode("medico123"));
		usuario.setRol("MEDICO");
		return usuarios.save(usuario);
	}
}
