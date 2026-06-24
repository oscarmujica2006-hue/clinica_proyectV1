package proyect_final.clinica.Service;


import proyect_final.clinica.Model.Entity.ActaEntrega;
import java.util.List;
import java.util.Optional;

public interface ActaEntregaService {
    List<ActaEntrega> findAll();
    Optional<ActaEntrega> findById(Long id);
    ActaEntrega save(ActaEntrega acta);
    void deleteById(Long id);
    List<ActaEntrega> findByAbastecimientoId(Long idAbastecimiento);
}