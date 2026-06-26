package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.EvolucionClinica;
import proyect_final.clinica.Model.Dto.EvolucionClinicaDTO;
import java.util.List;

public interface EvolucionClinicaService {
    
    List<EvolucionClinica> obtenerTodos();
    
    EvolucionClinica obtenerPorId(Long id);
    
    // ✅ Método para guardar desde DTO
    EvolucionClinica guardarEvolucion(EvolucionClinicaDTO dto);
    
    EvolucionClinica guardar(EvolucionClinica evolucion);
    
    void eliminar(Long id);
    
    void eliminarEvolucion(Long id);
    
    List<EvolucionClinica> obtenerPorDiagnostico(Long idDiagnostico);
    
    List<EvolucionClinica> obtenerPorTipoRegistro(String tipoRegistro);
    
    // ✅ Métodos para el controller
    List<EvolucionClinica> obtenerEvolucionesIniciales(Long idDiagnostico);
    
    List<EvolucionClinica> obtenerSesionesDeEvolucion(Long idEvolucion);
    
    boolean existeEvolucion(Long id);
}