package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.DiagnosticoTratamiento;
import proyect_final.clinica.Model.Entity.Tratamiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosticoTratamientoRepository extends JpaRepository<DiagnosticoTratamiento, Long> {
    
    // Solo este método si lo necesitas para consultas
    List<DiagnosticoTratamiento> findByEvolucionClinica_IdEvolucionClinica(Long idEvolucionClinica);
    
    // Si necesitas buscar por tratamiento
    List<DiagnosticoTratamiento> findByTratamiento_IdTratamiento(Long idTratamiento);

    // Buscar planes por tratamiento y estado
    List<DiagnosticoTratamiento> findByTratamientoAndEstado(Tratamiento tratamiento, String estado);


    List<DiagnosticoTratamiento> findByTratamiento(Tratamiento tratamiento);
    @Query("SELECT DISTINCT d FROM DiagnosticoTratamiento d " +
           "LEFT JOIN FETCH d.dientes dt " +
           "WHERE d.idDiagTrat = :idDiagTrat")
    Optional<DiagnosticoTratamiento> findByIdWithDientes(@Param("idDiagTrat") Long idDiagTrat);
}