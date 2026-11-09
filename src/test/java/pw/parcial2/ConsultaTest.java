package pw.parcial2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import pw.parcial2.model.Consulta;
import pw.parcial2.model.Medico;
import pw.parcial2.model.Usuario;

class ConsultaTest {
	private final Validator validador = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void aceptaConsultaValida() {
		Consulta consulta = consulta("Laura Gómez", "Control general");
		assertTrue(validador.validate(consulta).isEmpty());
	}

	@Test
	void rechazaNombreConFormatoIncorrecto() {
		Consulta consulta = consulta("7Laura", "Control general");
		var errores = validador.validate(consulta);
		assertEquals(1, errores.size());
		assertEquals("El nombre debe tener hasta 40 letras; ejemplo: Laura Gómez", errores.iterator().next().getMessage());
	}

	@Test
	void rechazaMotivoDeMasDeCienCaracteres() {
		Consulta consulta = consulta("Laura Gómez", "a".repeat(101));
		assertEquals(1, validador.validate(consulta).size());
	}

	private Consulta consulta(String paciente, String motivo) {
		Consulta consulta = new Consulta();
		consulta.setPaciente(paciente);
		consulta.setMotivo(motivo);
		consulta.setConsultorio(3);
		consulta.setHoraInicio(LocalDateTime.of(2026, 11, 10, 9, 0));
		consulta.setHoraFin(LocalDateTime.of(2026, 11, 10, 9, 30));
		consulta.setMedico(new Medico());
		consulta.setPacienteCuenta(new Usuario());
		return consulta;
	}
}
