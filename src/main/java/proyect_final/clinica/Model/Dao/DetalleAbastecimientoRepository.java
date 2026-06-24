package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.DetalleAbastecimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleAbastecimientoRepository extends JpaRepository<DetalleAbastecimiento, Long> {
    List<DetalleAbastecimiento> findByAbastecimiento_IdAbastecimiento(Long idAbastecimiento);
    List<DetalleAbastecimiento> findByDetallePedido_IdDetallePedidoInsumo(Long idDetallePedido);
}