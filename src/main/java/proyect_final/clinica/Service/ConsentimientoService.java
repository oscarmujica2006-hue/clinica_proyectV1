package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Consentimiento;
import proyect_final.clinica.Model.Entity.ConsentimientoFoto;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface ConsentimientoService {
    
    // ===== MÉTODOS EXISTENTES =====
    List<Consentimiento> obtenerTodos();
    
    Optional<Consentimiento> obtenerPorId(Long id);
    
    Consentimiento guardar(Consentimiento consentimiento);
    
    void eliminar(Long id);
    
    Consentimiento guardarConFotos(Consentimiento consentimiento, MultipartFile[] fotos) throws IOException;
    
    // ===== NUEVOS MÉTODOS PARA LA NUEVA ESTRUCTURA =====
    
    /**
     * Buscar consentimientos por ID del docente
     */
    List<Consentimiento> findByDocenteId(Long idDocente);
    
    /**
     * Buscar consentimiento por ID de DiagnosticoTratamiento
     */
    Optional<Consentimiento> findByDiagnosticoTratamientoId(Long idDiagTrat);
    
    /**
     * Buscar fotos por ID de consentimiento
     */
    List<ConsentimientoFoto> findFotosByConsentimientoId(Long idConsentimiento);
    
    /**
     * Cambiar el estado de un consentimiento
     */
    Consentimiento cambiarEstado(Long idConsentimiento, String estado);
    
    /**
     * Buscar consentimientos por ID del estudiante
     */
    List<Consentimiento> findByEstudianteId(Long idEstudiante);
    
    /**
     * Buscar consentimientos por estado
     */
    List<Consentimiento> findByEstado(String estado);
    
    /**
     * Buscar consentimientos por docente y estado
     */
    List<Consentimiento> findByDocenteIdAndEstado(Long idDocente, String estado);
}