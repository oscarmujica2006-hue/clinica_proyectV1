package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.SolicitudRadiografiaRepository;
import proyect_final.clinica.Model.Dao.DetalleSolicitudRadiografiaRepository;
import proyect_final.clinica.Model.Dao.DiagnosticoTratamientoDienteRepository;
import proyect_final.clinica.Model.Dao.DiagnosticoTratamientoRepository;
import proyect_final.clinica.Model.Dao.DiagnosticoTratamientoRepository;
import proyect_final.clinica.Model.Entity.SolicitudRadiografia;
import proyect_final.clinica.Model.Entity.DetalleSolicitudRadiografia;
import proyect_final.clinica.Model.Entity.DiagnosticoTratamiento;
import proyect_final.clinica.Model.Entity.DiagnosticoTratamientoDiente;
import proyect_final.clinica.Model.Entity.Radiografia;
import proyect_final.clinica.Service.SolicitudRadiografiaService;
import proyect_final.clinica.Service.DiagnosticoTratamientoService;
import proyect_final.clinica.Service.RadiografiaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class SolicitudRadiografiaServiceImpl implements SolicitudRadiografiaService {

    @Autowired
    private SolicitudRadiografiaRepository solicitudRadiografiaRepository;
    
    @Autowired
    private DetalleSolicitudRadiografiaRepository detalleSolicitudRadiografiaRepository;
    
    @Autowired
    private DiagnosticoTratamientoService diagnosticoTratamientoService;
    
    @Autowired
    private DiagnosticoTratamientoDienteRepository diagnosticoTratamientoDienteRepository;
    
    @Autowired
    private RadiografiaService radiografiaService;


    @Autowired
    private DiagnosticoTratamientoRepository diagnosticoTratamientoRepository;
      

    // ==================== SOLICITUD ====================
    
    @Override
    public List<SolicitudRadiografia> listarTodos() {
        return solicitudRadiografiaRepository.findAll();
    }

    @Override
    public Optional<SolicitudRadiografia> obtenerPorId(Long id) {
        return solicitudRadiografiaRepository.findById(id);
    }

    @Override
    public SolicitudRadiografia guardar(SolicitudRadiografia solicitud) {
        return solicitudRadiografiaRepository.save(solicitud);
    }

    @Override
    public void eliminar(Long id) {
        solicitudRadiografiaRepository.deleteById(id);
    }

    // ==================== MÉTODOS DE BÚSQUEDA ====================
    
    @Override
    public List<SolicitudRadiografia> findByDiagnosticoTratamientoId(Long idDiagTrat) {
        return solicitudRadiografiaRepository.findByDiagnosticoTratamiento_IdDiagTrat(idDiagTrat);
    }

    @Override
    public List<SolicitudRadiografia> findByEstado(String estado) {
        return solicitudRadiografiaRepository.findByEstado(estado);
    }

    @Override
    public List<SolicitudRadiografia> findPendientes() {
        return solicitudRadiografiaRepository.findSolicitudesPendientes();
    }

    @Override
    public List<SolicitudRadiografia> findByEstudianteId(Long idEstudiante) {
        return solicitudRadiografiaRepository.findByEstudianteId(idEstudiante);
    }

    // ==================== DETALLE ====================
    
    @Override
    public DetalleSolicitudRadiografia guardarDetalle(DetalleSolicitudRadiografia detalle) {
        return detalleSolicitudRadiografiaRepository.save(detalle);
    }

    @Override
    public List<DetalleSolicitudRadiografia> guardarDetalles(List<DetalleSolicitudRadiografia> detalles) {
        return detalleSolicitudRadiografiaRepository.saveAll(detalles);
    }

    @Override
    public List<DetalleSolicitudRadiografia> findDetallesBySolicitudId(Long idSolicitud) {
        return detalleSolicitudRadiografiaRepository.findBySolicitudRadiografia_IdSolicitudRadiografia(idSolicitud);
    }

    @Override
    public void eliminarDetalle(Long id) {
        detalleSolicitudRadiografiaRepository.deleteById(id);
    }

    // ==================== MÉTODOS DE NEGOCIO ====================
    
    @Override
    public SolicitudRadiografia crearSolicitudConDetalles(
            Long idDiagnosticoTratamiento,
            Long idRadiografia,
            String motivo,
            List<Long> idsDientesPlan) {
        
        // 1. Obtener el plan de tratamiento
        DiagnosticoTratamiento plan = diagnosticoTratamientoService.obtenerPorId(idDiagnosticoTratamiento)
            .orElseThrow(() -> new RuntimeException("Plan de tratamiento no encontrado"));
        
        // 2. Obtener la radiografía
        Radiografia radiografia = radiografiaService.obtenerPorId(idRadiografia)
            .orElseThrow(() -> new RuntimeException("Radiografía no encontrada"));
        
        // 3. Crear la solicitud
        SolicitudRadiografia solicitud = new SolicitudRadiografia();
        solicitud.setDiagnosticoTratamiento(plan);
        solicitud.setRadiografia(radiografia);
        solicitud.setMotivo(motivo);
        solicitud.setEstado("PENDIENTE");
        solicitud.setFechaSolicitud(LocalDateTime.now());
        
        SolicitudRadiografia solicitudGuardada = solicitudRadiografiaRepository.save(solicitud);
        
        // 4. Crear los detalles (dientes)
        if (idsDientesPlan != null && !idsDientesPlan.isEmpty()) {
            List<DetalleSolicitudRadiografia> detalles = new ArrayList<>();
            
            for (Long idDientePlan : idsDientesPlan) {
                DiagnosticoTratamientoDiente dientePlan = diagnosticoTratamientoDienteRepository
                    .findById(idDientePlan)
                    .orElseThrow(() -> new RuntimeException("Diente del plan no encontrado: " + idDientePlan));
                
                DetalleSolicitudRadiografia detalle = new DetalleSolicitudRadiografia();
                detalle.setSolicitudRadiografia(solicitudGuardada);
                detalle.setDiagTratDiente(dientePlan);
                detalles.add(detalle);
            }
            
            detalleSolicitudRadiografiaRepository.saveAll(detalles);
        }
        
        return solicitudGuardada;
    }

    @Override
    public SolicitudRadiografia aprobarSolicitud(Long idSolicitud) {
        SolicitudRadiografia solicitud = solicitudRadiografiaRepository.findById(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        solicitud.setEstado("APROBADO");
        return solicitudRadiografiaRepository.save(solicitud);
    }

    @Override
    public SolicitudRadiografia rechazarSolicitud(Long idSolicitud, String motivoRechazo) {
        SolicitudRadiografia solicitud = solicitudRadiografiaRepository.findById(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        solicitud.setEstado("RECHAZADO");
        solicitud.setMotivo(motivoRechazo);
        return solicitudRadiografiaRepository.save(solicitud);
    }

    


@Override
public List<Map<String, Object>> obtenerDiagnosticoTratamientoConDientes(Long idDiagnosticoTratamiento) {
    try {
        // Buscar el diagnóstico con sus dientes
        Optional<DiagnosticoTratamiento> opt = diagnosticoTratamientoRepository
                .findByIdWithDientes(idDiagnosticoTratamiento);
        
        if (opt.isEmpty()) {
            return null;
        }
        
        DiagnosticoTratamiento diagnostico = opt.get();
        
        // Solo devolver los dientes
        List<Map<String, Object>> dientesList = new ArrayList<>();
        if (diagnostico.getDientes() != null && !diagnostico.getDientes().isEmpty()) {
            for (DiagnosticoTratamientoDiente diente : diagnostico.getDientes()) {
                Map<String, Object> dienteMap = new HashMap<>();
                dienteMap.put("idDiagnosticoTratamientoDiente", diente.getIdDiagnosticoTratamientoDiente());
                dienteMap.put("diente", diente.getDiente());
                dienteMap.put("estado", diente.getEstado());
                dienteMap.put("observaciones", diente.getObservaciones());
                dientesList.add(dienteMap);
            }
        }
        
        return dientesList;
        
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Error al obtener dientes del diagnóstico: " + e.getMessage());
    }
}
}