package proyect_final.clinica.Model.Dao;
import proyect_final.clinica.Model.Entity.OdontogramaFoto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OdontogramaFotoRepository extends JpaRepository<OdontogramaFoto, Long> {
    List<OdontogramaFoto> findByConsultaIdConsulta(Long IdConsulta);
}