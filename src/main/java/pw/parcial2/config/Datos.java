package pw.parcial2.config;

import java.time.LocalDateTime;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import pw.parcial2.model.Consulta;
import pw.parcial2.model.Medico;
import pw.parcial2.model.Usuario;
import pw.parcial2.repository.ConsultaRepo;
import pw.parcial2.repository.MedicoRepo;
import pw.parcial2.repository.UsuarioRepo;

@Component
public class Datos implements CommandLineRunner {
	@Autowired
	private UsuarioRepo usuarioRepo;
	@Autowired
	private MedicoRepo medicoRepo;
	@Autowired
	private ConsultaRepo consultaRepo;
	@Autowired
	private PasswordEncoder codificador;

	@Override
	public void run(String... args) {
		crearUsuario("admin", "ADMIN", "admin123");
		crearUsuario("medico", "MEDICO", "medico123");
		crearUsuario("paciente", "PACIENTE", "paciente123");
		if (medicoRepo.count() == 0) {
			Medico medico = new Medico();
			medico.setNombre("Gregorio Casas");
			medico.setEspecialidad("Medicina general");
			medico.setCuenta(usuarioRepo.findByUsername("medico").orElseThrow());
			medicoRepo.save(medico);
		}
		normalizarCuentasMedicas();
		if (consultaRepo.count() == 0) {
			Consulta consulta = new Consulta();
			consulta.setPaciente("Laura Gómez");
			consulta.setMotivo("Control general");
			consulta.setConsultorio(3);
			consulta.setHoraInicio(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0));
			consulta.setHoraFin(consulta.getHoraInicio().plusMinutes(30));
			consulta.setMedico(medicoRepo.findAll().getFirst());
			consulta.setPacienteCuenta(usuarioRepo.findByUsername("paciente").orElseThrow());
			consultaRepo.save(consulta);
		}
	}

	private void crearUsuario(String username, String rol, String clave) {
		Usuario usuario = usuarioRepo.findByUsername(username).orElseGet(Usuario::new);
		usuario.setUsername(username);
		usuario.setRol(rol);
		if (usuario.getPassword() == null || !codificador.matches(clave, usuario.getPassword())) usuario.setPassword(codificador.encode(clave));
		usuarioRepo.save(usuario);
	}

	private void normalizarCuentasMedicas() {
		List<Medico> medicos = medicoRepo.findAll().stream().sorted(Comparator.comparing(Medico::getId)).toList();
		Set<Long> cuentas = new HashSet<>();
		for (Medico medico : medicos) {
			Usuario cuenta = medico.getCuenta();
			if (cuenta != null && "MEDICO".equals(cuenta.getRol()) && cuentas.add(cuenta.getId())) continue;
			cuenta = crearCuentaMedica(medico.getNombre());
			medico.setCuenta(cuenta);
			medicoRepo.save(medico);
			cuentas.add(cuenta.getId());
		}
	}

	private Usuario crearCuentaMedica(String nombre) {
		String base = Normalizer.normalize(nombre, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT)
			.replaceAll("[^a-z0-9]+", ".").replaceAll("^\\.|\\.$", "");
		if (base.length() < 3) base = "medico" + base;
		base = base.substring(0, Math.min(35, base.length())).replaceAll("\\.$", "");
		String username = base;
		int sufijo = 2;
		while (usuarioRepo.findByUsername(username).isPresent()) {
			String numero = String.valueOf(sufijo++);
			username = base.substring(0, Math.min(base.length(), 40 - numero.length())) + numero;
		}
		Usuario usuario = new Usuario();
		usuario.setUsername(username);
		usuario.setPassword(codificador.encode("medico123"));
		usuario.setRol("MEDICO");
		return usuarioRepo.save(usuario);
	}
}
