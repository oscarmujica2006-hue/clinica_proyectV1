package proyect_final.clinica.Service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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

    @Autowired
    private ObjectMapper objectMapper;

    // =============================================
    // 🔥 MÉTODOS PARA LLAMAR FUNCIONES POSTGRESQL
    // =============================================
    
    public int contarSolicitudesHoy(Long idEstudiante) {
        return solicitudRadiografiaRepository.contarSolicitudesHoy(idEstudiante);
    }
    
    public Map<String, Object> puedeSolicitar(Long idEstudiante) {
        try {
            String jsonResult = solicitudRadiografiaRepository.puedeSolicitar(idEstudiante);
            return objectMapper.readValue(jsonResult, Map.class);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("puedeSolicitar", false);
            error.put("motivo", "ERROR");
            error.put("mensaje", "Error al validar: " + e.getMessage());
            return error;
        }
    }
    
    @Scheduled(cron = "0 0 * * * *")
    public void verificarPagosVencidos() {
        try {
            String resultado = solicitudRadiografiaRepository.verificarPagosVencidos();
            System.out.println("✅ Verificación de pagos vencidos: " + resultado);
        } catch (Exception e) {
            System.err.println("❌ Error al verificar pagos vencidos: " + e.getMessage());
        }
    }

    // =============================================
    // CREAR SOLICITUD CON VALIDACIÓN
    // =============================================
    
    @Override
    public SolicitudRadiografia crearSolicitudConDetalles(
            Long idDiagnosticoTratamiento,
            Long idRadiografia,
            String motivo,
            List<Long> idsDientesPlan,
            Long idEstudiante) {
        
        Map<String, Object> validacion = puedeSolicitar(idEstudiante);
        
        if (!(Boolean) validacion.get("puedeSolicitar")) {
            String motivoError = (String) validacion.get("mensaje");
            throw new RuntimeException("❌ No puede solicitar: " + motivoError);
        }
        
        DiagnosticoTratamiento plan = diagnosticoTratamientoService.obtenerPorId(idDiagnosticoTratamiento)
            .orElseThrow(() -> new RuntimeException("Plan de tratamiento no encontrado"));
        
        Radiografia radiografia = radiografiaService.obtenerPorId(idRadiografia)
            .orElseThrow(() -> new RuntimeException("Radiografía no encontrada"));
        
        Estudiante estudiante = estudianteService.obtenerPorId(idEstudiante)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        SolicitudRadiografia solicitud = new SolicitudRadiografia();
        solicitud.setDiagnosticoTratamiento(plan);
        solicitud.setRadiografia(radiografia);
        solicitud.setMotivo(motivo);
        solicitud.setEstado("PENDIENTE");
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstudiante(estudiante);
        solicitud.setPagado(false);
        solicitud.setIncluidaEnRecibo(false);
        
        SolicitudRadiografia solicitudGuardada = solicitudRadiografiaRepository.save(solicitud);
        
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

    // =============================================
    // 🔥 NUEVO: REALIZAR SOLICITUD (Aprobar + Marcar Realizado)
    // =============================================
    
    @Override
    @Transactional
    public SolicitudRadiografia realizarSolicitud(Long idSolicitud) {
        SolicitudRadiografia solicitud = solicitudRadiografiaRepository.findById(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new RuntimeException("Solo se puede realizar solicitudes en estado PENDIENTE");
        }

        // 🔥 PASO 1: Cambiar a APROBADO - El TRIGGER crea el recibo y actualiza id_recibo
        solicitud.setEstado("APROBADO");
        solicitud = solicitudRadiografiaRepository.save(solicitud);

        // 🔥 PASO 2: Cambiar a REALIZADO - La radiografía ya fue realizada
        solicitud.setEstado("REALIZADO");
        solicitud.setFechModSolRad(LocalDateTime.now());
        
        return solicitudRadiografiaRepository.save(solicitud);
    }

    // =============================================
    // APROBAR SOLICITUD (el trigger hace el resto)
    // =============================================
    
    @Override
    public SolicitudRadiografia aprobarSolicitud(Long idSolicitud) {
        SolicitudRadiografia solicitud = solicitudRadiografiaRepository.findById(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if ("APROBADO".equals(solicitud.getEstado())) {
            return solicitud;
        }

        solicitud.setEstado("APROBADO");
        solicitud = solicitudRadiografiaRepository.save(solicitud);

        return solicitud;
    }

    // =============================================
    // RECHAZAR SOLICITUD
    // =============================================
    
    @Override
    public SolicitudRadiografia rechazarSolicitud(Long idSolicitud, String motivoRechazo) {
        SolicitudRadiografia solicitud = solicitudRadiografiaRepository.findById(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        solicitud.setEstado("RECHAZADA");
        solicitud.setMotivoRechazo(motivoRechazo);
        return solicitudRadiografiaRepository.save(solicitud);
    }

    // =============================================
    // CANCELAR SOLICITUD
    // =============================================
    
    @Override
    @Transactional
    public SolicitudRadiografia cancelarSolicitud(Long idSolicitud) {
        SolicitudRadiografia solicitud = solicitudRadiografiaRepository.findById(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        if (!"PENDIENTE".equals(solicitud.getEstado()) && !"APROBADO".equals(solicitud.getEstado())) {
            throw new RuntimeException("No se puede cancelar una solicitud en estado: " + solicitud.getEstado());
        }
        
        solicitud.setEstado("CANCELADO");
        solicitud.setMotivoRechazo("Cancelado por el usuario");
        solicitud.setFechModSolRad(LocalDateTime.now());
        
        if (Boolean.TRUE.equals(solicitud.getIncluidaEnRecibo()) && solicitud.getIdRecibo() != null) {
            System.out.println("📌 Solicitud #" + idSolicitud + " cancelada. Recibo ID: " + solicitud.getIdRecibo());
        }
        
        return solicitudRadiografiaRepository.save(solicitud);
    }

    // =============================================
    // MARCAR COMO REALIZADO
    // =============================================
    
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

    // =============================================
    // MÉTODOS EXISTENTES
    // =============================================
    
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