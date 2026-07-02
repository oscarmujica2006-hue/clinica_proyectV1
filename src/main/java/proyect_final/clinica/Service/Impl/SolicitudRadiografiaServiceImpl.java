package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.SolicitudRadiografiaRepository;
import proyect_final.clinica.Model.Dao.DetalleSolicitudRadiografiaRepository;
import proyect_final.clinica.Model.Dao.DiagnosticoTratamientoDienteRepository;
import proyect_final.clinica.Model.Dao.DiagnosticoTratamientoRepository;
import proyect_final.clinica.Model.Entity.SolicitudRadiografia;
import proyect_final.clinica.Model.Entity.DetalleSolicitudRadiografia;
import proyect_final.clinica.Model.Entity.DiagnosticoTratamiento;
import proyect_final.clinica.Model.Entity.DiagnosticoTratamientoDiente;
import proyect_final.clinica.Model.Entity.Radiografia;
import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Service.SolicitudRadiografiaService;
import proyect_final.clinica.Service.DiagnosticoTratamientoService;
import proyect_final.clinica.Service.RadiografiaService;
import proyect_final.clinica.Service.EstudianteService;
import proyect_final.clinica.Service.ReciboService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    
    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    @Lazy
    private ReciboService reciboService;

    // ==================== CRUD BÁSICO ====================
    
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

    // ==================== BÚSQUEDAS ====================
    
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

    // ==================== DETALLES ====================
    
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

    // ==================== ✅ CREAR SOLICITUD CON ESTUDIANTE ====================
    
    @Override
    public SolicitudRadiografia crearSolicitudConDetalles(
            Long idDiagnosticoTratamiento,
            Long idRadiografia,
            String motivo,
            List<Long> idsDientesPlan,
            Long idEstudiante) {
        
        // 1. Obtener el plan de tratamiento
        DiagnosticoTratamiento plan = diagnosticoTratamientoService.obtenerPorId(idDiagnosticoTratamiento)
            .orElseThrow(() -> new RuntimeException("Plan de tratamiento no encontrado"));
        
        // 2. Obtener la radiografía
        Radiografia radiografia = radiografiaService.obtenerPorId(idRadiografia)
            .orElseThrow(() -> new RuntimeException("Radiografía no encontrada"));
        
        // 3. Obtener el estudiante
        Estudiante estudiante = estudianteService.obtenerPorId(idEstudiante)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        // 4. Crear la solicitud
        SolicitudRadiografia solicitud = new SolicitudRadiografia();
        solicitud.setDiagnosticoTratamiento(plan);
        solicitud.setRadiografia(radiografia);
        solicitud.setMotivo(motivo);
        solicitud.setEstado("PENDIENTE");
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstudiante(estudiante);  // ✅ AGREGAR ESTUDIANTE
        
        SolicitudRadiografia solicitudGuardada = solicitudRadiografiaRepository.save(solicitud);
        
        // 5. Crear los detalles (dientes)
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

    // ==================== APROBAR / RECHAZAR ====================
    
    @Override
    public SolicitudRadiografia aprobarSolicitud(Long idSolicitud) {
        SolicitudRadiografia solicitud = solicitudRadiografiaRepository.findById(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if ("APROBADO".equals(solicitud.getEstado())) {
            return solicitud;
        }

        solicitud.setEstado("APROBADO");
        solicitud = solicitudRadiografiaRepository.save(solicitud);

        try {
            Long idConsentimiento = null;
            if (solicitud.getDiagnosticoTratamiento() != null
                    && solicitud.getDiagnosticoTratamiento().getConsentimiento() != null) {
                idConsentimiento = solicitud.getDiagnosticoTratamiento().getConsentimiento().getIdConsentimiento();
            }

            if (solicitud.getRadiografia() != null) {
                reciboService.generarRecibo(
                        "RADIOGRAFIA",
                        solicitud.getIdSolicitudRadiografia(),
                        "Radiografía solicitada",
                        solicitud.getRadiografia().getPrecioRayo(),
                        null,
                        idConsentimiento
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Solicitud aprobada, pero no se pudo registrar en el recibo: " + e.getMessage(), e);
        }

        return solicitud;
    }

    @Override
    public SolicitudRadiografia rechazarSolicitud(Long idSolicitud, String motivoRechazo) {
        SolicitudRadiografia solicitud = solicitudRadiografiaRepository.findById(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        solicitud.setEstado("RECHAZADO");
        solicitud.setMotivo(motivoRechazo);
        return solicitudRadiografiaRepository.save(solicitud);
    }

    // ==================== ✅ NUEVO: MARCAR COMO REALIZADO ====================
    
    @Override
    public SolicitudRadiografia marcarRealizado(Long idSolicitud) {
        SolicitudRadiografia solicitud = solicitudRadiografiaRepository.findById(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        if (!"APROBADO".equals(solicitud.getEstado())) {
            throw new RuntimeException("Solo se puede marcar como realizado una solicitud aprobada");
        }
        
        solicitud.setEstado("REALIZADO");
        return solicitudRadiografiaRepository.save(solicitud);
    }

    // ==================== OBTENER DIENTES DEL DIAGNÓSTICO ====================
    
    @Override
    public List<Map<String, Object>> obtenerDiagnosticoTratamientoConDientes(Long idDiagnosticoTratamiento) {
        try {
            Optional<DiagnosticoTratamiento> opt = diagnosticoTratamientoRepository
                    .findByIdWithDientes(idDiagnosticoTratamiento);
            
            if (opt.isEmpty()) {
                return new ArrayList<>();
            }
            
            DiagnosticoTratamiento diagnostico = opt.get();
            
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