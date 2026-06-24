package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.DetalleActaEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleActaEntregaRepository extends JpaRepository<DetalleActaEntrega, Long> {
    List<DetalleActaEntrega> findByActaEntrega_IdActaEntrega(Long idActaEntrega);
    List<DetalleActaEntrega> findByDetalleAbastecimiento_IdDetalleAbastecimiento(Long idDetalleAbastecimiento);
}
