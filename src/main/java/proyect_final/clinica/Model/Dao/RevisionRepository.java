package proyect_final.clinica.Model.Dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import proyect_final.clinica.Model.Entity.Revision;
import java.util.Optional;
import java.util.List;

public interface RevisionRepository extends JpaRepository<Revision, Long> {
    Optional<Revision> findByConsulta_IdConsulta(Long idConsulta);
    
    @Query("SELECT r FROM Revision r WHERE r.consulta.idConsulta = :idConsulta")
    Optional<Revision> findByIdConsulta(@Param("idConsulta") Long idConsulta);
    
    List<Revision> findByConsulta_Paciente_IdPaciente(Long idPaciente);
}