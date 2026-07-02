package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.SolicitudRadiografia;
import proyect_final.clinica.Model.Dto.SolicitudRadiografiaDTO;
import proyect_final.clinica.Model.Entity.DetalleSolicitudRadiografia;
import proyect_final.clinica.Model.Entity.Radiografia;
import proyect_final.clinica.Model.Entity.Recibo;
import proyect_final.clinica.Service.SolicitudRadiografiaService;
import proyect_final.clinica.Service.RadiografiaService;
import proyect_final.clinica.Service.ReciboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/solicitudes-radiografia")
@CrossOrigin(origins = "*")
public class SolicitudRadiografiaController {

    @Autowired
    private SolicitudRadiografiaService solicitudRadiografiaService;
    
    @Autowired
    private RadiografiaService radiografiaService;

    @Autowired
    private ReciboService reciboService;

    // ==================== OBTENER RADIOGRAFÍAS DISPONIBLES ====================
    @GetMapping("/radiografias-disponibles")
    public ResponseEntity<?> obtenerRadiografiasDisponibles() {
        try {
            List<Radiografia> radiografias = radiografiaService.listarTodos();
            
            List<Map<String, Object>> response = radiografias.stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idRadiografia", r.getIdRadiografia());
                    map.put("nombreRayo", r.getNombreRayo());
                    map.put("precioRayo", r.getPrecioRayo());
                    return map;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al obtener radiografías: " + e.getMessage()
            ));
        }
    }

    // ==================== CREAR SOLICITUD ====================
    @PostMapping("/crear")
    public ResponseEntity<?> crearSolicitud(@RequestBody SolicitudRadiografiaDTO dto) {
        try {
            SolicitudRadiografia solicitud = solicitudRadiografiaService.crearSolicitudConDetalles(
                dto.getIdDiagnosticoTratamiento(),
                dto.getIdRadiografia(),
                dto.getMotivo(),
                dto.getIdsDientesPlan(),
                dto.getIdEstudiante()  // ✅ AGREGAR ID DE ESTUDIANTE
            );
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Solicitud creada correctamente",
                "idSolicitud", solicitud.getIdSolicitudRadiografia(),
                "estado", solicitud.getEstado()
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al crear solicitud: " + e.getMessage()
            ));
        }
    }
    
    // ==================== OBTENER SOLICITUDES POR ESTADO ====================
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPorEstado(@PathVariable String estado) {
        try {
            List<SolicitudRadiografia> solicitudes = solicitudRadiografiaService.findByEstado(estado);
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error: " + e.getMessage()
            ));
        }
    }

    // ==================== ✅ NUEVO: OBTENER SOLICITUDES PENDIENTES PARA ENCARGADA ====================
    @GetMapping("/pendientes/encargada")
    public ResponseEntity<?> obtenerPendientesParaEncargada() {
        try {
            List<SolicitudRadiografia> solicitudes = solicitudRadiografiaService.findPendientes();
            
            // Enriquecer con datos adicionales
            List<Map<String, Object>> response = solicitudes.stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idSolicitudRadiografia", s.getIdSolicitudRadiografia());
                    map.put("estado", s.getEstado());
                    map.put("motivo", s.getMotivo());
                    map.put("fechaSolicitud", s.getFechaSolicitud());
                    
                    // Datos de radiografía
                    if (s.getRadiografia() != null) {
                        map.put("radiografia", Map.of(
                            "idRadiografia", s.getRadiografia().getIdRadiografia(),
                            "nombreRayo", s.getRadiografia().getNombreRayo(),
                            "precioRayo", s.getRadiografia().getPrecioRayo()
                        ));
                    }
                    
                    // Datos del diagnóstico
                    if (s.getDiagnosticoTratamiento() != null) {
                        map.put("idDiagnosticoTratamiento", s.getDiagnosticoTratamiento().getIdDiagTrat());
                    }
                    
                    // Obtener detalles (dientes)
                    List<DetalleSolicitudRadiografia> detalles = solicitudRadiografiaService
                        .findDetallesBySolicitudId(s.getIdSolicitudRadiografia());
                    List<Integer> dientes = detalles.stream()
                        .map(d -> d.getDiagTratDiente().getDiente())
                        .collect(Collectors.toList());
                    map.put("dientes", dientes);
                    
                    return map;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al obtener solicitudes pendientes: " + e.getMessage()
            ));
        }
    }

    // ==================== OBTENER SOLICITUDES POR ESTUDIANTE ====================
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<?> obtenerPorEstudiante(@PathVariable Long idEstudiante) {
        try {
            List<SolicitudRadiografia> solicitudes = solicitudRadiografiaService.findByEstudianteId(idEstudiante);
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error: " + e.getMessage()
            ));
        }
    }

    // ==================== OBTENER SOLICITUD POR ID ====================
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return solicitudRadiografiaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error: " + e.getMessage()
            ));
        }
    }

    // ==================== OBTENER DETALLES DE SOLICITUD ====================
    @GetMapping("/{id}/detalles")
    public ResponseEntity<?> obtenerDetalles(@PathVariable Long id) {
        try {
            List<DetalleSolicitudRadiografia> detalles = solicitudRadiografiaService.findDetallesBySolicitudId(id);
            
            List<Map<String, Object>> response = detalles.stream()
                .map(d -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idDetalle", d.getIdDetSoliRadiografia());
                    if (d.getDiagTratDiente() != null) {
                        map.put("diente", d.getDiagTratDiente().getDiente());
                        map.put("dienteNumero", d.getDiagTratDiente().getDiente());
                        map.put("idDientePlan", d.getDiagTratDiente().getIdDiagnosticoTratamientoDiente());
                    }
                    return map;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error: " + e.getMessage()
            ));
        }
    }

    // ==================== APROBAR SOLICITUD ====================
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarSolicitud(@PathVariable Long id) {
        try {
            SolicitudRadiografia solicitud = solicitudRadiografiaService.aprobarSolicitud(id);
            
            // Obtener el recibo generado por el trigger
            Long idConsentimiento = solicitud.getDiagnosticoTratamiento()
                .getConsentimiento().getIdConsentimiento();
            
            Recibo reciboGenerado = reciboService.findByConsentimientoId(idConsentimiento);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "✅ Solicitud aprobada - Trigger agregó al recibo",
                "idSolicitud", solicitud.getIdSolicitudRadiografia(),
                "estado", solicitud.getEstado(),
                "precio", solicitud.getRadiografia().getPrecioRayo(),
                "idRecibo", reciboGenerado != null ? reciboGenerado.getIdRecibo() : null,
                "montoTotal", reciboGenerado != null ? reciboGenerado.getMontoTotal() : 0
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al aprobar: " + e.getMessage()
            ));
        }
    }

    // ==================== RECHAZAR SOLICITUD ====================
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String motivo = payload.get("motivo");
            if (motivo == null || motivo.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Debe proporcionar un motivo para el rechazo"
                ));
            }
            
            SolicitudRadiografia solicitud = solicitudRadiografiaService.rechazarSolicitud(id, motivo);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Solicitud rechazada correctamente",
                "idSolicitud", solicitud.getIdSolicitudRadiografia(),
                "estado", solicitud.getEstado()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al rechazar: " + e.getMessage()
            ));
        }
    }

    // ==================== OBTENER DIENTES DEL DIAGNÓSTICO ====================
    @GetMapping("/diagnostico-tratamiento/{id}/dientes")
    public ResponseEntity<?> obtenerDientesDelDiagnostico(@PathVariable Long id) {
        try {
            List<Map<String, Object>> dientes = solicitudRadiografiaService.obtenerDiagnosticoTratamientoConDientes(id);
            
            if (dientes == null || dientes.isEmpty()) {
                return ResponseEntity.ok(List.of());
            }
            
            return ResponseEntity.ok(dientes);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al obtener dientes: " + e.getMessage()
            ));
        }
    }

    // ==================== ✅ NUEVO: MARCAR COMO REALIZADO ====================
    @PutMapping("/{id}/realizado")
    public ResponseEntity<?> marcarRealizado(@PathVariable Long id) {
        try {
            SolicitudRadiografia solicitud = solicitudRadiografiaService.marcarRealizado(id);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Radiografía marcada como realizada",
                "idSolicitud", solicitud.getIdSolicitudRadiografia(),
                "estado", solicitud.getEstado()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error: " + e.getMessage()
            ));
        }
    }
}