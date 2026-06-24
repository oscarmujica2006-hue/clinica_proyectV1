package proyect_final.clinica.Service.Impl;


import proyect_final.clinica.Model.Dao.DetalleAbastecimientoRepository;
import proyect_final.clinica.Model.Entity.DetalleAbastecimiento;
import proyect_final.clinica.Service.DetalleAbastecimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DetalleAbastecimientoServiceImpl implements DetalleAbastecimientoService {

    @Autowired
    private DetalleAbastecimientoRepository detalleAbastecimientoRepository;

    @Override
    public List<DetalleAbastecimiento> findAll() {
        return detalleAbastecimientoRepository.findAll();
    }

    @Override
    public Optional<DetalleAbastecimiento> findById(Long id) {
        return detalleAbastecimientoRepository.findById(id);
    }

    @Override
    public DetalleAbastecimiento save(DetalleAbastecimiento detalle) {
        return detalleAbastecimientoRepository.save(detalle);
    }

    @Override
    public void deleteById(Long id) {
        detalleAbastecimientoRepository.deleteById(id);
    }

    @Override
    public List<DetalleAbastecimiento> findByAbastecimientoId(Long idAbastecimiento) {
        return detalleAbastecimientoRepository.findByAbastecimiento_IdAbastecimiento(idAbastecimiento);
    }

    @Override
    public List<DetalleAbastecimiento> findByDetallePedidoId(Long idDetallePedido) {
        return detalleAbastecimientoRepository.findByDetallePedido_IdDetallePedidoInsumo(idDetallePedido);
    }
}
