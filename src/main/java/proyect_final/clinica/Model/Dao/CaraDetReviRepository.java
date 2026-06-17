package proyect_final.clinica.Model.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import proyect_final.clinica.Model.Entity.CaraDetRevi;
import java.util.List;

public interface CaraDetReviRepository extends JpaRepository<CaraDetRevi, Long> {
    List<CaraDetRevi> findByDetalleRevision_IdDetalleRev(Long idDetalleRev);
    void deleteByDetalleRevision_IdDetalleRev(Long idDetalleRev);
}