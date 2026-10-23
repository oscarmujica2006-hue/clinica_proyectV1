package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.SolicitudRadiografia;
import proyect_final.clinica.Model.Entity.DetalleSolicitudRadiografia;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SolicitudRadiografiaService {
    
    // CRUD
    List<SolicitudRadiografia> listarTodos();
    Optional<SolicitudRadiografia> obtenerPorId(Long id);
    SolicitudRadiografia guardar(SolicitudRadiografia solicitud);
    void eliminar(Long id);
    
    // Búsquedas
    List<SolicitudRadiografia> findByDiagnosticoTratamientoId(Long idDiagTrat);
    List<SolicitudRadiografia> findByEstado(String estado);
    List<SolicitudRadiografia> findPendientes();
    List<SolicitudRadiografia> findByEstudianteId(Long idEstudiante);
    
    // Detalles
    DetalleSolicitudRadiografia guardarDetalle(DetalleSolicitudRadiografia detalle);
    List<DetalleSolicitudRadiografia> guardarDetalles(List<DetalleSolicitudRadiografia> detalles);
    List<DetalleSolicitudRadiografia> findDetallesBySolicitudId(Long idSolicitud);
    void eliminarDetalle(Long id);
    
    // Negocio
    SolicitudRadiografia crearSolicitudConDetalles(
        Long idDiagnosticoTratamiento,
        Long idRadiografia,
        String motivo,
        List<Long> idsDientesPlan,
        Long idEstudiante  // ✅ AGREGAR PARÁMETRO
    );
        // Agregar a la interface
    SolicitudRadiografia cancelarSolicitud(Long idSolicitud);
    SolicitudRadiografia aprobarSolicitud(Long idSolicitud);
    SolicitudRadiografia rechazarSolicitud(Long idSolicitud, String motivoRechazo);
    SolicitudRadiografia marcarRealizado(Long idSolicitud);  
    SolicitudRadiografia realizarSolicitud(Long idSolicitud);
    
    List<Map<String, Object>> obtenerDiagnosticoTratamientoConDientes(Long idDiagnosticoTratamiento);
}