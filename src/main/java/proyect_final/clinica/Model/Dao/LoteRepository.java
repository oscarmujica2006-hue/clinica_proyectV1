package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    List<Lote> findByInsumo_IdInsumo(Long idInsumo);
    List<Lote> findByDetalleActaEntrega_IdDetalleActa(Long idDetalleActa);
}
