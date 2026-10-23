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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                dto.getIdEstudiante()
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
            
            List<Map<String, Object>> response = solicitudes.stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idSolicitudRadiografia", s.getIdSolicitudRadiografia());
                    map.put("estado", s.getEstado());
                    map.put("motivo", s.getMotivo());
                    map.put("fechaSolicitud", s.getFechaSolicitud());
                    map.put("idRecibo", s.getIdRecibo());
                    map.put("pagado", s.getPagado());
                    map.put("calidad", s.getCalidad());
                    map.put("observacionesCalidad", s.getObservacionesCalidad());
                    
                    if (s.getRadiografia() != null) {
                        map.put("radiografia", Map.of(
                            "idRadiografia", s.getRadiografia().getIdRadiografia(),
                            "nombreRayo", s.getRadiografia().getNombreRayo(),
                            "precioRayo", s.getRadiografia().getPrecioRayo()
                        ));
                    }
                    
                    if (s.getDiagnosticoTratamiento() != null) {
                        map.put("idDiagnosticoTratamiento", s.getDiagnosticoTratamiento().getIdDiagTrat());
                    }
                    
                    // 🔥 Obtener nombres del paciente y estudiante
                    try {
                        if (s.getDiagnosticoTratamiento() != null) {
                            var plan = s.getDiagnosticoTratamiento();
                            if (plan.getEvolucionClinica() != null) {
                                var ec = plan.getEvolucionClinica();
                                if (ec.getDiagnostico() != null) {
                                    var d = ec.getDiagnostico();
                                    if (d.getRevision() != null) {
                                        var r = d.getRevision();
                                        if (r.getConsulta() != null) {
                                            var c = r.getConsulta();
                                            
                                            // Paciente
                                            if (c.getPaciente() != null && c.getPaciente().getPersona() != null) {
                                                var persona = c.getPaciente().getPersona();
                                                String nombrePaciente = persona.getNombre() + " " + 
                                                                      persona.getApellidoPaterno() + " " + 
                                                                      (persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : "");
                                                map.put("pacienteNombre", nombrePaciente.trim());
                                            } else {
                                                map.put("pacienteNombre", "N/A");
                                            }
                                            
                                            // Estudiante
                                            if (c.getEstudiante() != null && 
                                                c.getEstudiante().getUsuario() != null && 
                                                c.getEstudiante().getUsuario().getPersona() != null) {
                                                var persona = c.getEstudiante().getUsuario().getPersona();
                                                String nombreEstudiante = persona.getNombre() + " " + 
                                                                         persona.getApellidoPaterno();
                                                map.put("estudianteNombre", nombreEstudiante);
                                            } else {
                                                map.put("estudianteNombre", "N/A");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        map.put("pacienteNombre", "N/A");
                        map.put("estudianteNombre", "N/A");
                    }
                    
                    return map;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error: " + e.getMessage()
            ));
        }
    }

    // ==================== OBTENER SOLICITUDES PENDIENTES PARA ENCARGADA ====================
    @GetMapping("/pendientes/encargada")
    public ResponseEntity<?> obtenerPendientesParaEncargada() {
        try {
            List<SolicitudRadiografia> solicitudes = solicitudRadiografiaService.findPendientes();
            
            List<Map<String, Object>> response = solicitudes.stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idSolicitudRadiografia", s.getIdSolicitudRadiografia());
                    map.put("estado", s.getEstado());
                    map.put("motivo", s.getMotivo());
                    map.put("fechaSolicitud", s.getFechaSolicitud());
                    
                    if (s.getRadiografia() != null) {
                        map.put("radiografia", Map.of(
                            "idRadiografia", s.getRadiografia().getIdRadiografia(),
                            "nombreRayo", s.getRadiografia().getNombreRayo(),
                            "precioRayo", s.getRadiografia().getPrecioRayo()
                        ));
                    }
                    
                    if (s.getDiagnosticoTratamiento() != null) {
                        map.put("idDiagnosticoTratamiento", s.getDiagnosticoTratamiento().getIdDiagTrat());
                    }
                    
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
            Optional<SolicitudRadiografia> opt = solicitudRadiografiaService.obtenerPorId(id);
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            SolicitudRadiografia s = opt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("idSolicitudRadiografia", s.getIdSolicitudRadiografia());
            response.put("estado", s.getEstado());
            response.put("motivo", s.getMotivo());
            response.put("fechaSolicitud", s.getFechaSolicitud());
            response.put("idRecibo", s.getIdRecibo());
            response.put("pagado", s.getPagado());
            response.put("calidad", s.getCalidad());
            response.put("observacionesCalidad", s.getObservacionesCalidad());
            response.put("fechRegSolRad", s.getFechRegSolRad());
            response.put("fechModSolRad", s.getFechModSolRad());
            
            if (s.getRadiografia() != null) {
                Map<String, Object> radio = new HashMap<>();
                radio.put("idRadiografia", s.getRadiografia().getIdRadiografia());
                radio.put("nombreRayo", s.getRadiografia().getNombreRayo());
                radio.put("precioRayo", s.getRadiografia().getPrecioRayo());
                response.put("radiografia", radio);
            }
            
            if (s.getDiagnosticoTratamiento() != null) {
                response.put("idDiagnosticoTratamiento", s.getDiagnosticoTratamiento().getIdDiagTrat());
            }
            
            return ResponseEntity.ok(response);
            
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

    // ==================== 🔥 REALIZAR SOLICITUD (Aprobar + Marcar Realizado) ====================
    @PutMapping("/{id}/realizar")
    public ResponseEntity<?> realizarSolicitud(@PathVariable Long id) {
        try {
            SolicitudRadiografia solicitud = solicitudRadiografiaService.realizarSolicitud(id);
            
            Recibo reciboGenerado = null;
            if (solicitud.getIdRecibo() != null) {
                reciboGenerado = reciboService.obtenerPorId(solicitud.getIdRecibo()).orElse(null);
            }
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "✅ Radiografía realizada correctamente",
                "idSolicitud", solicitud.getIdSolicitudRadiografia(),
                "estado", solicitud.getEstado(),
                "idRecibo", solicitud.getIdRecibo(),
                "precio", solicitud.getRadiografia() != null ? solicitud.getRadiografia().getPrecioRayo() : 0,
                "montoTotal", reciboGenerado != null ? reciboGenerado.getMontoTotal() : 0
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al realizar: " + e.getMessage()
            ));
        }
    }

    // ==================== APROBAR SOLICITUD (Deprecado - usar realizar) ====================
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarSolicitud(@PathVariable Long id) {
        try {
            SolicitudRadiografia solicitud = solicitudRadiografiaService.aprobarSolicitud(id);
            
            Recibo reciboGenerado = null;
            if (solicitud.getIdRecibo() != null) {
                reciboGenerado = reciboService.obtenerPorId(solicitud.getIdRecibo()).orElse(null);
            }
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "✅ Solicitud aprobada - Trigger agregó al recibo",
                "idSolicitud", solicitud.getIdSolicitudRadiografia(),
                "estado", solicitud.getEstado(),
                "idRecibo", solicitud.getIdRecibo(),
                "precio", solicitud.getRadiografia() != null ? solicitud.getRadiografia().getPrecioRayo() : 0,
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

    // ==================== MARCAR COMO REALIZADO (Deprecado - usar realizar) ====================
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

    // ==================== CALIFICAR CALIDAD ====================
    @PutMapping("/{id}/calidad")
    public ResponseEntity<?> calificarCalidad(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String calidad = payload.get("calidad");
            String observaciones = payload.get("observaciones");
            
            SolicitudRadiografia solicitud = solicitudRadiografiaService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
            
            if (!"REALIZADO".equals(solicitud.getEstado())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Solo se puede calificar radiografías realizadas"
                ));
            }
            
            solicitud.setCalidad(calidad);
            solicitud.setObservacionesCalidad(observaciones);
            solicitud = solicitudRadiografiaService.guardar(solicitud);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "✅ Calidad registrada correctamente",
                "idSolicitud", solicitud.getIdSolicitudRadiografia(),
                "calidad", solicitud.getCalidad(),
                "observaciones", solicitud.getObservacionesCalidad()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al calificar: " + e.getMessage()
            ));
        }
    }

    // ==================== CANCELAR SOLICITUD ====================
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarSolicitud(@PathVariable Long id) {
        try {
            SolicitudRadiografia solicitud = solicitudRadiografiaService.cancelarSolicitud(id);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "✅ Solicitud cancelada correctamente",
                "idSolicitud", solicitud.getIdSolicitudRadiografia(),
                "estado", solicitud.getEstado()
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al cancelar: " + e.getMessage()
            ));
        }
    }

    // ==================== 🔥 NUEVO: OBTENER RADIOGRAFÍAS REALIZADAS (para cajero) ====================
    @GetMapping("/realizadas/pagos")
    public ResponseEntity<?> obtenerRealizadasParaPagos() {
        try {
            List<SolicitudRadiografia> solicitudes = solicitudRadiografiaService.findByEstado("REALIZADO");
            
            List<Map<String, Object>> response = new ArrayList<>();
            
            for (SolicitudRadiografia s : solicitudes) {
                Map<String, Object> map = new HashMap<>();
                map.put("idSolicitudRadiografia", s.getIdSolicitudRadiografia());
                map.put("estado", s.getEstado());
                map.put("motivo", s.getMotivo());
                map.put("fechaSolicitud", s.getFechaSolicitud());
                map.put("idRecibo", s.getIdRecibo());
                map.put("pagado", s.getPagado());
                map.put("calidad", s.getCalidad());
                map.put("observacionesCalidad", s.getObservacionesCalidad());
                map.put("fechRegSolRad", s.getFechRegSolRad());
                map.put("fechModSolRad", s.getFechModSolRad());
                
                // Radiografía
                if (s.getRadiografia() != null) {
                    Map<String, Object> radio = new HashMap<>();
                    radio.put("idRadiografia", s.getRadiografia().getIdRadiografia());
                    radio.put("nombreRayo", s.getRadiografia().getNombreRayo());
                    radio.put("precioRayo", s.getRadiografia().getPrecioRayo());
                    map.put("radiografia", radio);
                }
                
                // 🔥 Obtener nombre del paciente y estudiante
                try {
                    if (s.getDiagnosticoTratamiento() != null) {
                        var plan = s.getDiagnosticoTratamiento();
                        if (plan.getEvolucionClinica() != null) {
                            var ec = plan.getEvolucionClinica();
                            if (ec.getDiagnostico() != null) {
                                var d = ec.getDiagnostico();
                                if (d.getRevision() != null) {
                                    var r = d.getRevision();
                                    if (r.getConsulta() != null) {
                                        var c = r.getConsulta();
                                        
                                        // Paciente
                                        if (c.getPaciente() != null && c.getPaciente().getPersona() != null) {
                                            var persona = c.getPaciente().getPersona();
                                            String nombrePaciente = persona.getNombre() + " " + 
                                                                  persona.getApellidoPaterno() + " " + 
                                                                  (persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : "");
                                            map.put("pacienteNombre", nombrePaciente.trim());
                                        } else {
                                            map.put("pacienteNombre", "N/A");
                                        }
                                        
                                        // Estudiante
                                        if (c.getEstudiante() != null && 
                                            c.getEstudiante().getUsuario() != null && 
                                            c.getEstudiante().getUsuario().getPersona() != null) {
                                            var persona = c.getEstudiante().getUsuario().getPersona();
                                            String nombreEstudiante = persona.getNombre() + " " + 
                                                                     persona.getApellidoPaterno();
                                            map.put("estudianteNombre", nombreEstudiante);
                                        } else {
                                            map.put("estudianteNombre", "N/A");
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    map.put("pacienteNombre", "N/A");
                    map.put("estudianteNombre", "N/A");
                }
                
                response.add(map);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al obtener radiografías: " + e.getMessage()
            ));
        }
    }
}