package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.SolicitudRadiografia;
import proyect_final.clinica.Model.Dto.SolicitudRadiografiaDTO;
import proyect_final.clinica.Model.Entity.DetalleSolicitudRadiografia;
import proyect_final.clinica.Model.Entity.Radiografia;
import proyect_final.clinica.Service.SolicitudRadiografiaService;
import proyect_final.clinica.Service.RadiografiaService;
import proyect_final.clinica.Service.ReciboService;  // ✅ AGREGAR IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // ✅ INYECTAR ReciboService
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

    @PostMapping("/crear")
    public ResponseEntity<?> crearSolicitud(@RequestBody SolicitudRadiografiaDTO dto) {
        try {
            SolicitudRadiografia solicitud = solicitudRadiografiaService.crearSolicitudConDetalles(
                dto.getIdDiagnosticoTratamiento(),
                dto.getIdRadiografia(),
                dto.getMotivo(),
                dto.getIdsDientesPlan()
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

    // ==================== ✅ APROBAR SOLICITUD (CON RECIBO) ====================
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarSolicitud(@PathVariable Long id) {
        try {
            // 1. Aprobar la solicitud
            SolicitudRadiografia solicitud = solicitudRadiografiaService.aprobarSolicitud(id);
            
            // ✅ 2. GENERAR RECIBO POR RADIOGRAFÍA
            Double precioRadiografia = solicitud.getRadiografia().getPrecioRayo();
            
            Long idEvolucionClinica = solicitud.getDiagnosticoTratamiento()
                .getEvolucionClinica().getIdEvolucionClinica();
            Long idConsentimiento = solicitud.getDiagnosticoTratamiento()
                .getConsentimiento().getIdConsentimiento();
            
            String concepto = "Radiografía: " + solicitud.getRadiografia().getNombreRayo();
            
            reciboService.generarRecibo(
                "RADIOGRAFIA",
                solicitud.getIdSolicitudRadiografia(),
                concepto,
                precioRadiografia != null ? precioRadiografia : 0.0,
                idEvolucionClinica,
                idConsentimiento
            );
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "✅ Solicitud aprobada correctamente y registrada en el recibo",
                "idSolicitud", solicitud.getIdSolicitudRadiografia(),
                "estado", solicitud.getEstado(),
                "precio", precioRadiografia != null ? precioRadiografia : 0.0
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
            
            if (dientes == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(dientes);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al obtener dientes: " + e.getMessage()
            ));
        }
    }
}