package pw.parcial2.controller;

import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class Errores {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> validacion(MethodArgumentNotValidException e) {
		String texto = e.getBindingResult().getFieldErrors().stream().map(error -> "- " + error.getDefaultMessage()).distinct().collect(Collectors.joining("\n"));
		return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(texto);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> formato(HttpMessageNotReadableException e) {
		return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("- Revise el formato de los datos; use fecha y hora como 2026-11-10T09:00");
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<String> sinRecurso(NoResourceFoundException e) {
		if (e.getResourcePath().startsWith("api/consultas")) return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("- Seleccione una consulta con un identificador válido antes de guardar");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("- No se encontró el recurso solicitado");
	}
}
