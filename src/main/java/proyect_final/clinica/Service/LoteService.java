package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Lote;
import java.util.List;
import java.util.Optional;

public interface LoteService {
    List<Lote> findAll();
    Optional<Lote> findById(Long id);
    Lote save(Lote lote);
    void deleteById(Long id);
    List<Lote> findByInsumoId(Long idInsumo);
    List<Lote> findByDetalleActaId(Long idDetalleActa);
}