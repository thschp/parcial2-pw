package pw.parcial2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
public class Medico {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "El nombre del médico es obligatorio; ejemplo: Ana Pérez")
	@Pattern(regexp = "^[\\p{L}][\\p{L} .'-]{1,59}$", message = "El nombre debe tener entre 2 y 60 letras; ejemplo: Ana Pérez")
	private String nombre;

	@NotNull(message = "La especialidad es obligatoria; ejemplo: Pediatría")
	@Pattern(regexp = "^[\\p{L}][\\p{L} .'-]{1,59}$", message = "La especialidad debe tener entre 2 y 60 letras; ejemplo: Pediatría")
	private String especialidad;

	@ManyToOne(optional = false)
	private Usuario cuenta;
}
