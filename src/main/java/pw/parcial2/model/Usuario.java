package pw.parcial2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "El usuario es obligatorio; ejemplo: paciente")
	@Pattern(regexp = "^[A-Za-z0-9._-]{3,40}$", message = "El usuario debe tener entre 3 y 40 letras o números; ejemplo: paciente1")
	private String username;

	@JsonIgnore
	@NotNull(message = "La clave es obligatoria")
	private String password;

	@NotNull(message = "El rol es obligatorio; ejemplo: PACIENTE")
	@Pattern(regexp = "^(ADMIN|MEDICO|PACIENTE)$", message = "El rol debe ser ADMIN, MEDICO o PACIENTE; ejemplo: PACIENTE")
	private String rol;
}
