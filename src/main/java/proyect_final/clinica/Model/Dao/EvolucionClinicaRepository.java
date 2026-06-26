package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.EvolucionClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvolucionClinicaRepository extends JpaRepository<EvolucionClinica, Long> {
 
    // Solo agrega métodos personalizados:
    List<EvolucionClinica> findByDiagnosticoIdDiagnostico(Long idDiagnostico);
    
    List<EvolucionClinica> findByTipoRegistro(String tipoRegistro);
    
    List<EvolucionClinica> findByDiagnosticoIdDiagnosticoAndEvolucionPadreIsNull(Long idDiagnostico);
    
    List<EvolucionClinica> findByEvolucionPadreIdEvolucionClinica(Long idEvolucionPadre);
}