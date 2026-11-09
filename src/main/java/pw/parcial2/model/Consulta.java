package pw.parcial2.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
public class Consulta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "El nombre del paciente es obligatorio; ejemplo: Laura Gómez")
	@Pattern(regexp = "^[\\p{L}][\\p{L} .'-]{1,39}$", message = "El nombre debe tener hasta 40 letras; ejemplo: Laura Gómez")
	private String paciente;

	@NotNull(message = "El motivo es obligatorio; ejemplo: Control general")
	@Pattern(regexp = "^(?=.{1,100}$)(?=.*\\S)[\\p{L}\\p{N} .,;:!?¿¡()'\"/-]+$", message = "El motivo debe tener entre 1 y 100 caracteres; ejemplo: Control general")
	private String motivo;

	@NotNull(message = "El número de consultorio es obligatorio; ejemplo: 3")
	@Min(value = 1, message = "El consultorio debe ser mayor que cero; ejemplo: 3")
	@Max(value = 99, message = "El consultorio no puede superar 99; ejemplo: 3")
	private Integer consultorio;

	@NotNull(message = "La hora de inicio es obligatoria; ejemplo: 2026-11-10T09:00")
	private LocalDateTime horaInicio;

	@NotNull(message = "La hora de finalización es obligatoria; ejemplo: 2026-11-10T09:30")
	private LocalDateTime horaFin;

	@NotNull(message = "Seleccione un médico")
	@ManyToOne
	private Medico medico;

	@NotNull(message = "Seleccione la cuenta del paciente")
	@ManyToOne
	private Usuario pacienteCuenta;
}
