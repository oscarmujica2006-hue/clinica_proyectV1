package proyect_final.clinica.Service;
import proyect_final.clinica.Model.Entity.Abastecimiento;
import java.util.List;
import java.util.Optional;

public interface AbastecimientoService {
    List<Abastecimiento> findAll();
    Optional<Abastecimiento> findById(Long id);
    Abastecimiento save(Abastecimiento abastecimiento);
    void deleteById(Long id);
    List<Abastecimiento> findByDirectorId(Long idDirector);
    List<Abastecimiento> findByEstado(String estado);
    List<Abastecimiento> findByPedidoId(Long idPedido);
    Optional<Abastecimiento> findByPedidoIdAndDirectorId(Long idPedido, Long idDirector);
}