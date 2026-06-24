package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.AbastecimientoRepository;
import proyect_final.clinica.Model.Entity.Abastecimiento;
import proyect_final.clinica.Service.AbastecimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AbastecimientoServiceImpl implements AbastecimientoService {

    @Autowired
    private AbastecimientoRepository abastecimientoRepository;

    @Override
    public List<Abastecimiento> findAll() {
        return abastecimientoRepository.findAll();
    }

    @Override
    public Optional<Abastecimiento> findById(Long id) {
        return abastecimientoRepository.findById(id);
    }

    @Override
    public Abastecimiento save(Abastecimiento abastecimiento) {
        return abastecimientoRepository.save(abastecimiento);
    }

    @Override
    public void deleteById(Long id) {
        abastecimientoRepository.deleteById(id);
    }

    @Override
    public List<Abastecimiento> findByDirectorId(Long idDirector) {
        // CORREGIDO: usar director en lugar de directora
        return abastecimientoRepository.findByDirector_IdUsuarioClinica(idDirector);
    }

    @Override
    public List<Abastecimiento> findByEstado(String estado) {
        return abastecimientoRepository.findByEstadoAbastecimiento(estado);
    }

    @Override
    public List<Abastecimiento> findByPedidoId(Long idPedido) {
        return abastecimientoRepository.findByPedido_IdPedido(idPedido);
    }

    @Override
    public Optional<Abastecimiento> findByPedidoIdAndDirectorId(Long idPedido, Long idDirector) {
        List<Abastecimiento> abastecimientos = abastecimientoRepository.findByPedido_IdPedido(idPedido);
        return abastecimientos.stream()
            // CORREGIDO: usar getDirector() en lugar de getDirectora()
            .filter(a -> a.getDirector() != null && a.getDirector().getIdUsuarioClinica().equals(idDirector))
            .findFirst();
    }
}