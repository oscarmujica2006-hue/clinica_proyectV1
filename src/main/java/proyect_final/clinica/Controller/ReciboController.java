package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Dto.ReciboDTO;
import proyect_final.clinica.Model.Dto.DetalleReciboDTO;
import proyect_final.clinica.Model.Dto.PagoReciboDTO;
import proyect_final.clinica.Model.Entity.Recibo;
import proyect_final.clinica.Model.Entity.PagoRecibo;
import proyect_final.clinica.Service.ReciboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recibos")
@CrossOrigin(origins = "*")
public class ReciboController {

    @Autowired
    private ReciboService reciboService;

    // ==================== CONVERTIR ENTIDAD A DTO ====================
    private ReciboDTO convertirAReciboDTO(Recibo recibo) {
        if (recibo == null) return null;
        
        ReciboDTO dto = new ReciboDTO();
        dto.setIdRecibo(recibo.getIdRecibo());
        dto.setFechaPago(recibo.getFechaPago());
        dto.setEstadoPago(recibo.getEstadoPago());
        dto.setSubtotalTratamientos(recibo.getSubtotalTratamientos());
        dto.setSubtotalRadiografias(recibo.getSubtotalRadiografias());
        dto.setMontoTotal(recibo.getMontoTotal());
        dto.setMontoPagado(recibo.getMontoPagado());
        dto.setSaldoPendiente(recibo.getSaldoPendiente());
        
        // ✅ OBTENER DATOS DEL PACIENTE Y ESTUDIANTE
        String pacienteNombre = "N/A";
        String ciPaciente = "N/A";
        String estudianteNombre = "N/A";
        
        try {
            if (recibo.getConsentimiento() != null && 
                recibo.getConsentimiento().getDiagnosticoTratamiento() != null) {
                
                var plan = recibo.getConsentimiento().getDiagnosticoTratamiento();
                
                // Obtener paciente
                if (plan.getEvolucionClinica() != null && 
                    plan.getEvolucionClinica().getDiagnostico() != null) {
                    var diagnostico = plan.getEvolucionClinica().getDiagnostico();
                    if (diagnostico.getRevision() != null && 
                        diagnostico.getRevision().getConsulta() != null &&
                        diagnostico.getRevision().getConsulta().getPaciente() != null) {
                        var paciente = diagnostico.getRevision().getConsulta().getPaciente();
                        if (paciente.getPersona() != null) {
                            pacienteNombre = paciente.getPersona().getNombre() + " " + 
                                           paciente.getPersona().getApellidoPaterno();
                        }
                        // ✅ CORREGIDO: Convertir Integer a String
                        ciPaciente = paciente.getCi() != null ? String.valueOf(paciente.getCi()) : "N/A";
                    }
                }
                
                // ✅ OBTENER NOMBRE DEL ESTUDIANTE
                if (plan.getEvolucionClinica() != null && 
                    plan.getEvolucionClinica().getDiagnostico() != null) {
                    var diagnostico = plan.getEvolucionClinica().getDiagnostico();
                    if (diagnostico.getRevision() != null && 
                        diagnostico.getRevision().getConsulta() != null &&
                        diagnostico.getRevision().getConsulta().getEstudiante() != null) {
                        var estudiante = diagnostico.getRevision().getConsulta().getEstudiante();
                        if (estudiante.getUsuario() != null && 
                            estudiante.getUsuario().getPersona() != null) {
                            var persona = estudiante.getUsuario().getPersona();
                            estudianteNombre = persona.getNombre() + " " + 
                                             persona.getApellidoPaterno();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener datos: " + e.getMessage());
        }
        
        dto.setPacienteNombre(pacienteNombre);
        dto.setCiPaciente(ciPaciente);
        dto.setEstudianteNombre(estudianteNombre);
        
        // ✅ CONVERTIR DETALLES CON TRATAMIENTO Y DIENTE
        if (recibo.getDetalles() != null) {
            List<DetalleReciboDTO> detallesDTO = recibo.getDetalles().stream()
                .map(d -> {
                    DetalleReciboDTO detDTO = new DetalleReciboDTO();
                    detDTO.setIdDetalleRecibo(d.getIdDetalleRecibo());
                    detDTO.setTipoItem(d.getTipoItem());
                    detDTO.setCantidad(d.getCantidad());
                    detDTO.setPrecioUnitario(d.getPrecioUnitario());
                    detDTO.setSubtotal(d.getSubtotal());
                    
                    // ✅ OBTENER NOMBRE DEL TRATAMIENTO Y DIENTE
                    String tratamientoNombre = "N/A";
                    Integer diente = null;
                    
                    try {
                        if (d.getDiagnosticoTratamientoDiente() != null) {
                            var dientePlan = d.getDiagnosticoTratamientoDiente();
                            diente = dientePlan.getDiente();
                            
                            if (dientePlan.getDiagnosticoTratamiento() != null &&
                                dientePlan.getDiagnosticoTratamiento().getTratamiento() != null) {
                                tratamientoNombre = dientePlan.getDiagnosticoTratamiento()
                                    .getTratamiento().getNombreTratamiento();
                            }
                        } else if (d.getSolicitudRadiografia() != null) {
                            var radiografia = d.getSolicitudRadiografia().getRadiografia();
                            if (radiografia != null) {
                                tratamientoNombre = "Radiografía: " + radiografia.getNombreRayo();
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error al obtener tratamiento/diente: " + e.getMessage());
                    }
                    
                    detDTO.setTratamientoNombre(tratamientoNombre);
                    detDTO.setDiente(diente);
                    
                    return detDTO;
                })
                .collect(Collectors.toList());
            dto.setDetalles(detallesDTO);
        }
        
        // ✅ CONVERTIR PAGOS
        if (recibo.getPagos() != null) {
            List<PagoReciboDTO> pagosDTO = recibo.getPagos().stream()
                .map(p -> {
                    PagoReciboDTO pagoDTO = new PagoReciboDTO();
                    pagoDTO.setIdPagoRecibo(p.getIdPagoRecibo());
                    pagoDTO.setMonto(p.getMonto());
                    pagoDTO.setMetodoPago(p.getMetodoPago());
                    pagoDTO.setFechaPago(p.getFechaPago());
                    pagoDTO.setObservaciones(p.getObservaciones());
                    return pagoDTO;
                })
                .collect(Collectors.toList());
            dto.setPagos(pagosDTO);
        }
        
        return dto;
    }

    // ==================== LISTAR TODOS LOS RECIBOS (DTO) ====================
    @GetMapping
    public ResponseEntity<List<ReciboDTO>> listarTodos() {
        List<Recibo> recibos = reciboService.listarTodos();
        List<ReciboDTO> dtos = recibos.stream()
            .map(this::convertirAReciboDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ==================== LISTAR TODOS (ALIAS) ====================
    @GetMapping("/todos")
    public ResponseEntity<List<ReciboDTO>> listarTodosAlias() {
        return listarTodos();
    }

    // ==================== OBTENER RECIBO POR ID (DTO) ====================
    @GetMapping("/{id}")
    public ResponseEntity<ReciboDTO> obtenerPorId(@PathVariable Long id) {
        return reciboService.obtenerPorId(id)
            .map(this::convertirAReciboDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ==================== OBTENER RECIBO COMPLETO (DTO) ====================
    @GetMapping("/{id}/completo")
    public ResponseEntity<ReciboDTO> obtenerReciboCompleto(@PathVariable Long id) {
        return reciboService.obtenerPorId(id)
            .map(this::convertirAReciboDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ==================== OBTENER RECIBOS POR ESTADO ====================
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReciboDTO>> obtenerPorEstado(@PathVariable String estado) {
        List<Recibo> recibos = reciboService.findByEstado(estado);
        List<ReciboDTO> dtos = recibos.stream()
            .map(this::convertirAReciboDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ==================== OBTENER RECIBOS POR CONSENTIMIENTO ====================
    @GetMapping("/consentimiento/{idConsentimiento}")
    public ResponseEntity<List<ReciboDTO>> obtenerPorConsentimiento(@PathVariable Long idConsentimiento) {
        List<Recibo> recibos = reciboService.findByConsentimientoId(idConsentimiento);
        List<ReciboDTO> dtos = recibos.stream()
            .map(this::convertirAReciboDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ==================== OBTENER RECIBOS POR PACIENTE ====================
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<ReciboDTO>> obtenerPorPaciente(@PathVariable Long idPaciente) {
        List<Recibo> recibos = reciboService.findByPacienteId(idPaciente);
        List<ReciboDTO> dtos = recibos.stream()
            .map(this::convertirAReciboDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ==================== OBTENER RECIBOS CON SALDO PENDIENTE ====================
    @GetMapping("/saldo-pendiente")
    public ResponseEntity<List<ReciboDTO>> obtenerRecibosConSaldoPendiente() {
        List<Recibo> recibos = reciboService.findRecibosConSaldoPendiente();
        List<ReciboDTO> dtos = recibos.stream()
            .map(this::convertirAReciboDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ==================== OBTENER RECIBOS POR RANGO DE FECHAS ====================
    @GetMapping("/fechas")
    public ResponseEntity<List<ReciboDTO>> obtenerPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<Recibo> recibos = reciboService.findRecibosByFechaBetween(inicio, fin);
        List<ReciboDTO> dtos = recibos.stream()
            .map(this::convertirAReciboDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ==================== REGISTRAR PAGO ====================
    @PostMapping("/{id}/pagar")
    public ResponseEntity<?> registrarPago(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            Double monto = Double.valueOf(payload.get("monto").toString());
            String metodoPago = payload.get("metodoPago").toString();
            String observaciones = payload.get("observaciones") != null ? 
                payload.get("observaciones").toString() : "";
            
            Recibo recibo = reciboService.registrarPago(id, monto, metodoPago, observaciones);
            ReciboDTO dto = convertirAReciboDTO(recibo);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "✅ Pago registrado correctamente",
                "idRecibo", dto.getIdRecibo(),
                "montoPagado", dto.getMontoPagado(),
                "saldoPendiente", dto.getSaldoPendiente(),
                "estadoPago", dto.getEstadoPago()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al registrar pago: " + e.getMessage()
            ));
        }
    }

    // ==================== ACTUALIZAR ESTADO DE PAGO ====================
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoPago(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        try {
            String nuevoEstado = payload.get("estadoPago");
            if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Debe proporcionar un estado"
                ));
            }
            
            Recibo recibo = reciboService.actualizarEstadoPago(id, nuevoEstado);
            ReciboDTO dto = convertirAReciboDTO(recibo);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Estado actualizado correctamente",
                "idRecibo", dto.getIdRecibo(),
                "estadoPago", dto.getEstadoPago()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error: " + e.getMessage()
            ));
        }
    }

    // ==================== ESTADÍSTICAS DE PAGOS ====================
    @GetMapping("/estadisticas/pagos")
    public ResponseEntity<?> obtenerEstadisticasPagos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        try {
            Double totalPagos = reciboService.sumPagosByFechaBetween(inicio, fin);
            
            return ResponseEntity.ok(Map.of(
                "totalPagos", totalPagos,
                "fechaInicio", inicio,
                "fechaFin", fin
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error: " + e.getMessage()
            ));
        }
    }
}