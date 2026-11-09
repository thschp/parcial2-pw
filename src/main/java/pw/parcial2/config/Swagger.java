package pw.parcial2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class Swagger {
	@Bean
	OpenAPI documentacion() {
		return new OpenAPI().info(new Info().title("Consultas Médicas - Documentacion de servicios")
			.description("Inicie sesión en /login antes de usar los servicios. Ejemplo: POST /api/consultas con nombre, motivo, número, horario, médico y cuenta del paciente.")
			.version("1.0"));
	}
}
