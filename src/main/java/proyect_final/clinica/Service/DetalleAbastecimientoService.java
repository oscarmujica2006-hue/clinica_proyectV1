package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.DetalleAbastecimiento;
import java.util.List;
import java.util.Optional;

public interface DetalleAbastecimientoService {
    List<DetalleAbastecimiento> findAll();
    Optional<DetalleAbastecimiento> findById(Long id);
    DetalleAbastecimiento save(DetalleAbastecimiento detalle);
    void deleteById(Long id);
    List<DetalleAbastecimiento> findByAbastecimientoId(Long idAbastecimiento);
    List<DetalleAbastecimiento> findByDetallePedidoId(Long idDetallePedido);
}