package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.DetalleEvolucionClinica;
import java.util.List;
import java.util.Optional;

public interface DetalleEvolucionClinicaService {
    
    List<DetalleEvolucionClinica> findAll();
    
    Optional<DetalleEvolucionClinica> obtenerPorId(Long id);
    
    DetalleEvolucionClinica guardar(DetalleEvolucionClinica detalle);
    
    List<DetalleEvolucionClinica> guardarTodos(List<DetalleEvolucionClinica> detalles);
    
    void eliminar(Long id);
    
    List<DetalleEvolucionClinica> findByEvolucionClinicaId(Long idEvolucion);
    
    List<DetalleEvolucionClinica> findByDientePlanId(Long idDientePlan);
    
    List<DetalleEvolucionClinica> findByEvolucionClinicaIdAndEstado(Long idEvolucion, String estDetEvoClin);
    
    long countByEvolucionClinicaId(Long idEvolucion);
}