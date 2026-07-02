package proyect_final.clinica.Model.Dao;
import proyect_final.clinica.Model.Entity.ConsentimientoFoto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ConsentimientoFotoRepository extends JpaRepository<ConsentimientoFoto, Long> {
    List<ConsentimientoFoto> findByConsentimientoIdConsentimiento(Long IdConsentimiento);
    

    
    List<ConsentimientoFoto> findByConsentimiento_IdConsentimiento(Long idConsentimiento);
    
}