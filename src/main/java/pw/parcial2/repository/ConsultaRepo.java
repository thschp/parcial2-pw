package pw.parcial2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pw.parcial2.model.Consulta;

public interface ConsultaRepo extends JpaRepository<Consulta, Long> {
	List<Consulta> findByMedicoCuentaUsername(String username);
	List<Consulta> findByPacienteCuentaUsername(String username);
	boolean existsByMedicoId(Long id);
}
