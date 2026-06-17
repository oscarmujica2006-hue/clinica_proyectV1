package proyect_final.clinica.Model.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import proyect_final.clinica.Model.Entity.DetalleRevision;
import java.util.List;

public interface DetalleRevisionRepository extends JpaRepository<DetalleRevision, Long> {
    List<DetalleRevision> findByRevision_IdRevision(Long idRevision);
    List<DetalleRevision> findByDiente_NumeroDiente(Integer numeroDiente);
}