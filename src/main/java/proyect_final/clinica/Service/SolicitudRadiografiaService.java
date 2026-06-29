package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.SolicitudRadiografia;
import proyect_final.clinica.Model.Entity.DetalleSolicitudRadiografia;
import proyect_final.clinica.Model.Entity.Radiografia;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SolicitudRadiografiaService {
    
    // ========== SOLICITUD ==========
    List<SolicitudRadiografia> listarTodos();
    
    Optional<SolicitudRadiografia> obtenerPorId(Long id);
    
    SolicitudRadiografia guardar(SolicitudRadiografia solicitud);
    
    void eliminar(Long id);
    
    // ========== MÉTODOS DE BÚSQUEDA ==========
    List<SolicitudRadiografia> findByDiagnosticoTratamientoId(Long idDiagTrat);
    
    List<SolicitudRadiografia> findByEstado(String estado);
    
    List<SolicitudRadiografia> findPendientes();
    
    List<SolicitudRadiografia> findByEstudianteId(Long idEstudiante);
    
    // ========== DETALLE ==========
    DetalleSolicitudRadiografia guardarDetalle(DetalleSolicitudRadiografia detalle);
    
    List<DetalleSolicitudRadiografia> guardarDetalles(List<DetalleSolicitudRadiografia> detalles);
    
    List<DetalleSolicitudRadiografia> findDetallesBySolicitudId(Long idSolicitud);
    
    void eliminarDetalle(Long id);
    
    // ========== MÉTODOS DE NEGOCIO ==========
    SolicitudRadiografia crearSolicitudConDetalles(
        Long idDiagnosticoTratamiento,
        Long idRadiografia,
        String motivo,
        List<Long> idsDientesPlan
    );
    
    SolicitudRadiografia aprobarSolicitud(Long idSolicitud);
    
    SolicitudRadiografia rechazarSolicitud(Long idSolicitud, String motivoRechazo);


    List<Map<String, Object>> obtenerDiagnosticoTratamientoConDientes(Long idDiagnosticoTratamiento);
}