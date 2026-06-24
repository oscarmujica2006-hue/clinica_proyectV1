package proyect_final.clinica.Service.Impl;


import proyect_final.clinica.Model.Dao.DetalleActaEntregaRepository;
import proyect_final.clinica.Model.Entity.DetalleActaEntrega;
import proyect_final.clinica.Service.DetalleActaEntregaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DetalleActaEntregaServiceImpl implements DetalleActaEntregaService {

    @Autowired
    private DetalleActaEntregaRepository detalleActaEntregaRepository;

    @Override
    public List<DetalleActaEntrega> findAll() {
        return detalleActaEntregaRepository.findAll();
    }

    @Override
    public Optional<DetalleActaEntrega> findById(Long id) {
        return detalleActaEntregaRepository.findById(id);
    }

    @Override
    public DetalleActaEntrega save(DetalleActaEntrega detalle) {
        return detalleActaEntregaRepository.save(detalle);
    }

    @Override
    public void deleteById(Long id) {
        detalleActaEntregaRepository.deleteById(id);
    }

    @Override
    public List<DetalleActaEntrega> findByActaEntregaId(Long idActaEntrega) {
        return detalleActaEntregaRepository.findByActaEntrega_IdActaEntrega(idActaEntrega);
    }

    @Override
    public List<DetalleActaEntrega> findByDetalleAbastecimientoId(Long idDetalleAbastecimiento) {
        return detalleActaEntregaRepository.findByDetalleAbastecimiento_IdDetalleAbastecimiento(idDetalleAbastecimiento);
    }
}
