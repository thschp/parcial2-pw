package pw.parcial2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pw.parcial2.model.Usuario;

public interface UsuarioRepo extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByUsername(String username);
	List<Usuario> findByRol(String rol);
}
