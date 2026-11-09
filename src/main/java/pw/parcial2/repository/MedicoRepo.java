package pw.parcial2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pw.parcial2.model.Medico;

public interface MedicoRepo extends JpaRepository<Medico, Long> {
	List<Medico> findByCuentaUsername(String username);
	boolean existsByCuentaId(Long id);
}
