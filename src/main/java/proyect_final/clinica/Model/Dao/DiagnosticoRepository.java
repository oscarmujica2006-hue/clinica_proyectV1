package proyect_final.clinica.Model.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import proyect_final.clinica.Model.Entity.Diagnostico;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Long> {
    Optional<Diagnostico> findByRevision_IdRevision(Long idRevision);
    Optional<Diagnostico> findByConsulta_IdConsulta(Long idConsulta);
        @Query(value = "SELECT * FROM buscar_diagnostico_por_id(:idDiagnostico)", nativeQuery = true)
    Optional<Diagnostico> buscarPorFuncionDiagnostico(@Param("idDiagnostico") Long idDiagnostico);
}