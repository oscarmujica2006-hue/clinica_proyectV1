package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.DetalleActaEntrega;
import java.util.List;
import java.util.Optional;

public interface DetalleActaEntregaService {
    List<DetalleActaEntrega> findAll();
    Optional<DetalleActaEntrega> findById(Long id);
    DetalleActaEntrega save(DetalleActaEntrega detalle);
    void deleteById(Long id);
    List<DetalleActaEntrega> findByActaEntregaId(Long idActaEntrega);
    List<DetalleActaEntrega> findByDetalleAbastecimientoId(Long idDetalleAbastecimiento);
}
