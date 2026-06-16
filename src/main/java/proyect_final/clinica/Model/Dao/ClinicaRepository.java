package proyect_final.clinica.Model.Dao;
import proyect_final.clinica.Model.Entity.Clinica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClinicaRepository extends JpaRepository<Clinica, Long> {
    
    List<Clinica> findByNombreClinicaContainingIgnoreCase(String nombre);
    
    List<Clinica> findByTurno_NombreTurno(String nombreTurno);

    List<Clinica> findByRote_IdRote(Long idRote);
}