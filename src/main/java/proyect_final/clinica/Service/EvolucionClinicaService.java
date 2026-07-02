package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.EvolucionClinica;
import proyect_final.clinica.Model.Dto.EvolucionClinicaDTO;
import java.util.List;

public interface EvolucionClinicaService {
    
    
    EvolucionClinica obtenerPorId(Long id);
    
   
    EvolucionClinica guardarEvolucion(EvolucionClinicaDTO dto);
    
    EvolucionClinica guardar(EvolucionClinica evolucion);
    
    List<EvolucionClinica> obtenerPorDiagnostico(Long idDiagnostico);
    
 
    // ✅ Métodos para el controller
    List<EvolucionClinica> obtenerEvolucionesIniciales(Long idDiagnostico);
    
    List<EvolucionClinica> obtenerSesionesDeEvolucion(Long idEvolucion);
    

}