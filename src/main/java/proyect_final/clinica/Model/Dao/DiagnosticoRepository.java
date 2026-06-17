package proyect_final.clinica.Model.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import proyect_final.clinica.Model.Entity.Diagnostico;
import java.util.Optional;

public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Long> {
    Optional<Diagnostico> findByRevision_IdRevision(Long idRevision);
    Optional<Diagnostico> findByConsulta_IdConsulta(Long idConsulta);
}