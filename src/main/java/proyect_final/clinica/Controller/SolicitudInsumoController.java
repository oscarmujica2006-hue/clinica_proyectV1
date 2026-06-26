package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.*;
import proyect_final.clinica.Model.Dao.EncargadoInsumoRepository;
import proyect_final.clinica.Model.Dto.*;
import proyect_final.clinica.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/solicitudes-insumo")
public class SolicitudInsumoController {

    @Autowired
    private SolicitudInsumoService solicitudInsumoService;
    
    @Autowired
    private SolDetInsumoService solicitudDetInsumoService;

    @Autowired
    private EncargadoInsumoRepository encargadoInsumoRepository;
    
    @Autowired
    private InsumoService insumoService;

    // ==================== OBTENER SOLICITUDES POR DOCENTE ====================
    @GetMapping("/docente/{idDocente}")
    public ResponseEntity<?> getSolicitudesByDocente(@PathVariable Long idDocente) {
        List<SolicitudInsumo> solicitudes = solicitudInsumoService.findByDocenteId(idDocente);
        
        List<Map<String, Object>> dtos = solicitudes.stream().map(s -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("idSolicitudInsumo", s.getIdSolicitudInsumo());
            dto.put("fechaSolicitud", s.getFechaSolicitud());
            dto.put("estadoSolicitud", s.getEstadoSolicitud());
            
            DiagnosticoTratamiento diagTrat = s.getDiagnosticoTratamiento();
            if (diagTrat != null) {
                // ✅ CORREGIDO: Obtener diagnóstico desde la evolución clínica
                EvolucionClinica evolucion = diagTrat.getEvolucionClinica();
                if (evolucion != null) {
                    Diagnostico diagnostico = evolucion.getDiagnostico();
                    if (diagnostico != null && diagnostico.getRevision() != null && 
                        diagnostico.getRevision().getConsulta() != null && 
                        diagnostico.getRevision().getConsulta().getPaciente() != null) {
                        Paciente paciente = diagnostico.getRevision().getConsulta().getPaciente();
                        if (paciente.getPersona() != null) {
                            dto.put("pacienteNombre", paciente.getPersona().getNombre() + " " + 
                                                     paciente.getPersona().getApellidoPaterno());
                            dto.put("pacienteCi", paciente.getCi());
                        }
                    }
                }
                
                Tratamiento tratamiento = diagTrat.getTratamiento();
                if (tratamiento != null) {
                    dto.put("tratamientoNombre", tratamiento.getNombreTratamiento());
                }
            }
            
            if (s.getDetalles() != null) {
                dto.put("cantidadInsumos", s.getDetalles().size());
            } else {
                dto.put("cantidadInsumos", 0);
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    // ==================== APROBAR SOLICITUD ====================
    @PutMapping("/{idSolicitud}/aprobar")
    public ResponseEntity<?> aprobarSolicitud(@PathVariable Long idSolicitud) {
        SolicitudInsumo solicitud = solicitudInsumoService.obtenerPorId(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        solicitud.setEstadoSolicitud("APROBADO");
        solicitudInsumoService.guardar(solicitud);
        
        if (solicitud.getDetalles() != null) {
            for (SolicitudDetInsumo detalle : solicitud.getDetalles()) {
                detalle.setEstadoSoliDetalle("APROBADO");
                solicitudDetInsumoService.guardar(detalle);
            }
        }
        
        return ResponseEntity.ok(Map.of("mensaje", "Solicitud aprobada correctamente"));
    }
    
    // ==================== RECHAZAR SOLICITUD ====================
    @PutMapping("/{idSolicitud}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(@PathVariable Long idSolicitud, 
                                               @RequestBody Map<String, String> body) {
        SolicitudInsumo solicitud = solicitudInsumoService.obtenerPorId(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        String motivo = body.get("motivo");
        solicitud.setEstadoSolicitud("RECHAZADO");
        solicitudInsumoService.guardar(solicitud);
        
        if (solicitud.getDetalles() != null) {
            for (SolicitudDetInsumo detalle : solicitud.getDetalles()) {
                detalle.setEstadoSoliDetalle("RECHAZADO");
                solicitudDetInsumoService.guardar(detalle);
            }
        }
        
        return ResponseEntity.ok(Map.of("mensaje", "Solicitud rechazada: " + motivo));
    }
    
    // ==================== OBTENER DETALLE COMPLETO ====================
    @GetMapping("/{idSolicitud}/detalle")
    public ResponseEntity<?> getDetalleSolicitud(@PathVariable Long idSolicitud) {
        SolicitudInsumo solicitud = solicitudInsumoService.obtenerPorId(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        Map<String, Object> detalle = new HashMap<>();
        detalle.put("idSolicitudInsumo", solicitud.getIdSolicitudInsumo());
        detalle.put("fechaSolicitud", solicitud.getFechaSolicitud());
        detalle.put("estadoSolicitud", solicitud.getEstadoSolicitud());
        
        DiagnosticoTratamiento diagTrat = solicitud.getDiagnosticoTratamiento();
        if (diagTrat != null) {
            // ✅ CORREGIDO: Obtener diagnóstico desde la evolución clínica
            EvolucionClinica evolucion = diagTrat.getEvolucionClinica();
            if (evolucion != null) {
                Diagnostico diagnostico = evolucion.getDiagnostico();
                if (diagnostico != null && diagnostico.getRevision() != null && 
                    diagnostico.getRevision().getConsulta() != null && 
                    diagnostico.getRevision().getConsulta().getPaciente() != null) {
                    Persona p = diagnostico.getRevision().getConsulta().getPaciente().getPersona();
                    if (p != null) {
                        detalle.put("pacienteNombre", p.getNombre() + " " + p.getApellidoPaterno());
                    }
                }
            }
            
            Tratamiento tratamiento = diagTrat.getTratamiento();
            if (tratamiento != null) {
                detalle.put("tratamientoNombre", tratamiento.getNombreTratamiento());
            }
            
            detalle.put("observaciones", diagTrat.getObservaciones());
        }
        
        List<SolDetInsumoDTO> insumos = new ArrayList<>();
        if (solicitud.getDetalles() != null) {
            for (SolicitudDetInsumo det : solicitud.getDetalles()) {
                SolDetInsumoDTO dto = new SolDetInsumoDTO();
                dto.setIdInsumo(det.getInsumo().getIdInsumo());
                dto.setNombreInsumo(det.getInsumo().getNombreInsumo());
                dto.setCantidadSolicitada(det.getCantidadSolicitada());
                dto.setCantidadEntregada(det.getCantidadEntregada() != null ? det.getCantidadEntregada() : 0);
                dto.setUnidadBase(det.getInsumo().getUnidadBase());
                dto.setConcentracion(det.getInsumo().getConcentracion());
                insumos.add(dto);
            }
        }
        detalle.put("insumos", insumos);
        
        return ResponseEntity.ok(detalle);
    }

    // ==================== OBTENER SOLICITUDES PARA ENCARGADO ====================
    @GetMapping("/encargado/{idEncargado}")
    public ResponseEntity<?> getSolicitudesByEncargado(@PathVariable Long idEncargado) {
        List<SolicitudInsumo> todas = solicitudInsumoService.findByEstados(List.of("APROBADO", "ENTREGADO", "ENTREGADO_PARCIAL"));
        
        List<Map<String, Object>> dtos = todas.stream().map(s -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("idSolicitudInsumo", s.getIdSolicitudInsumo());
            dto.put("fechaSolicitud", s.getFechaSolicitud());
            dto.put("estadoSolicitud", s.getEstadoSolicitud());
            
            DiagnosticoTratamiento diagTrat = s.getDiagnosticoTratamiento();
            if (diagTrat != null) {
                // ✅ CORREGIDO: Obtener diagnóstico desde la evolución clínica
                EvolucionClinica evolucion = diagTrat.getEvolucionClinica();
                if (evolucion != null) {
                    Diagnostico diagnostico = evolucion.getDiagnostico();
                    if (diagnostico != null && diagnostico.getRevision() != null && 
                        diagnostico.getRevision().getConsulta() != null && 
                        diagnostico.getRevision().getConsulta().getPaciente() != null) {
                        Paciente paciente = diagnostico.getRevision().getConsulta().getPaciente();
                        if (paciente != null && paciente.getPersona() != null) {
                            dto.put("pacienteNombre", paciente.getPersona().getNombre() + " " + 
                                                     paciente.getPersona().getApellidoPaterno());
                            dto.put("pacienteCi", paciente.getCi());
                        }
                    }
                }
                
                Tratamiento tratamiento = diagTrat.getTratamiento();
                if (tratamiento != null) {
                    dto.put("tratamientoNombre", tratamiento.getNombreTratamiento());
                }
            }
            
            if (s.getDetalles() != null) {
                dto.put("cantidadInsumos", s.getDetalles().size());
            } else {
                dto.put("cantidadInsumos", 0);
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    // ==================== ENTREGAR INSUMOS ====================
    @SuppressWarnings("unchecked")
    @PutMapping("/{idSolicitud}/entregar")
    public ResponseEntity<?> entregarInsumos(@PathVariable Long idSolicitud,
                                            @RequestBody Map<String, Object> body) {
        try {
            SolicitudInsumo solicitud = solicitudInsumoService.obtenerPorId(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
            
            Long idEncargado = Long.valueOf(body.get("idEncargado").toString());
            List<Map<String, Object>> insumosEntregados = (List<Map<String, Object>>) body.get("insumos");
            
            EncargadoInsumo encargado = encargadoInsumoRepository.findById(idEncargado)
                .orElseThrow(() -> new RuntimeException("Encargado no encontrado"));
            
            if (!solicitud.getEstadoSolicitud().equals("APROBADO") && 
                !solicitud.getEstadoSolicitud().equals("ENTREGADO_PARCIAL")) {
                return ResponseEntity.badRequest().body(Map.of("error", 
                    "Solo se pueden entregar insumos de solicitudes aprobadas"));
            }
            
            // Verificar stock
            Map<Long, Integer> stockDisponible = new HashMap<>();
            for (Map<String, Object> item : insumosEntregados) {
                Long idInsumo = Long.valueOf(item.get("idInsumo").toString());
                Integer cantidadEntregada = (Integer) item.get("cantidadEntregada");
                
                if (cantidadEntregada == null || cantidadEntregada <= 0) {
                    continue;
                }
                
                Insumo insumo = insumoService.obtenerPorId(idInsumo)
                    .orElseThrow(() -> new RuntimeException("Insumo no encontrado: " + idInsumo));
                
                Integer yaEntregado = 0;
                for (SolicitudDetInsumo detalle : solicitud.getDetalles()) {
                    if (detalle.getInsumo().getIdInsumo().equals(idInsumo) && detalle.getCantidadEntregada() != null) {
                        yaEntregado = detalle.getCantidadEntregada();
                        break;
                    }
                }
                
                int cantidadSolicitada = 0;
                for (SolicitudDetInsumo detalle : solicitud.getDetalles()) {
                    if (detalle.getInsumo().getIdInsumo().equals(idInsumo)) {
                        cantidadSolicitada = detalle.getCantidadSolicitada();
                        break;
                    }
                }
                
                int nuevaCantidadTotal = yaEntregado + cantidadEntregada;
                if (nuevaCantidadTotal > cantidadSolicitada) {
                    return ResponseEntity.badRequest().body(Map.of("error", 
                        "No se puede entregar más de " + cantidadSolicitada + 
                        " unidades de " + insumo.getNombreInsumo() +
                        ". Ya se entregaron " + yaEntregado + " unidades"));
                }
                
                if (insumo.getStockTotal() < cantidadEntregada) {
                    return ResponseEntity.badRequest().body(Map.of("error", 
                        "Stock insuficiente para " + insumo.getNombreInsumo() + 
                        ". Stock disponible: " + insumo.getStockTotal() + 
                        ", Intentas entregar: " + cantidadEntregada));
                }
                
                stockDisponible.put(idInsumo, insumo.getStockTotal());
            }
            
            // Realizar entrega
            boolean algunaEntrega = false;
            boolean todosEntregados = true;
            List<Map<String, Object>> insumosActualizados = new ArrayList<>();
            
            for (Map<String, Object> item : insumosEntregados) {
                Long idInsumo = Long.valueOf(item.get("idInsumo").toString());
                Integer cantidadEntregada = (Integer) item.get("cantidadEntregada");
                
                if (cantidadEntregada == null || cantidadEntregada <= 0) {
                    continue;
                }
                
                algunaEntrega = true;
                
                Insumo insumo = insumoService.obtenerPorId(idInsumo)
                    .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));
                
                int stockActual = insumo.getStockTotal();
                int nuevoStock = stockActual - cantidadEntregada;
                insumo.setStockTotal(nuevoStock);
                insumo.setUltimaActualizacion(LocalDate.now());
                insumoService.guardar(insumo);
                
                for (SolicitudDetInsumo detalle : solicitud.getDetalles()) {
                    if (detalle.getInsumo().getIdInsumo().equals(idInsumo)) {
                        Integer cantidadActual = detalle.getCantidadEntregada() != null ? 
                            detalle.getCantidadEntregada() : 0;
                        Integer nuevaCantidad = cantidadActual + cantidadEntregada;
                        
                        detalle.setCantidadEntregada(nuevaCantidad);
                        detalle.setResponsableInsumo(encargado);
                        
                        if (nuevaCantidad.equals(detalle.getCantidadSolicitada())) {
                            detalle.setEstadoSoliDetalle("ENTREGADO_COMPLETO");
                        } else if (nuevaCantidad > 0) {
                            detalle.setEstadoSoliDetalle("ENTREGADO_PARCIAL");
                        }
                        
                        solicitudDetInsumoService.guardar(detalle);
                        
                        if (nuevaCantidad < detalle.getCantidadSolicitada()) {
                            todosEntregados = false;
                        }
                        
                        Map<String, Object> infoInsumo = new HashMap<>();
                        infoInsumo.put("nombre", insumo.getNombreInsumo());
                        infoInsumo.put("entregado", cantidadEntregada);
                        infoInsumo.put("stockRestante", nuevoStock);
                        infoInsumo.put("totalEntregado", nuevaCantidad);
                        infoInsumo.put("totalSolicitado", detalle.getCantidadSolicitada());
                        insumosActualizados.add(infoInsumo);
                        
                        break;
                    }
                }
            }
            
            if (!algunaEntrega) {
                return ResponseEntity.badRequest().body(Map.of("error", 
                    "Debe entregar al menos un insumo"));
            }
            
            if (todosEntregados) {
                solicitud.setEstadoSolicitud("ENTREGADO");
            } else {
                solicitud.setEstadoSolicitud("ENTREGADO_PARCIAL");
            }
            solicitudInsumoService.guardar(solicitud);
            
            String nombreEncargado = encargado.getUsuario().getPersona().getNombre();
            String mensaje = todosEntregados ? 
                "✅ Todos los insumos entregados correctamente. Stock actualizado." : 
                "⚠️ Entrega parcial registrada. Stock actualizado. Pendiente: " + calcularPendientes(solicitud);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", mensaje,
                "entregadoPor", nombreEncargado,
                "estadoFinal", solicitud.getEstadoSolicitud(),
                "insumosActualizados", insumosActualizados
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== VER STOCK DISPONIBLE ====================
    @GetMapping("/insumos/stock")
    public ResponseEntity<?> verStockDisponible() {
        List<Insumo> insumos = insumoService.obtenerTodos();
        
        List<Map<String, Object>> stockInfo = insumos.stream().map(insumo -> {
            Map<String, Object> info = new HashMap<>();
            info.put("idInsumo", insumo.getIdInsumo());
            info.put("nombreInsumo", insumo.getNombreInsumo());
            info.put("stockTotal", insumo.getStockTotal());
            info.put("unidadBase", insumo.getUnidadBase());
            info.put("concentracion", insumo.getConcentracion());
            info.put("ultimaActualizacion", insumo.getUltimaActualizacion());
            return info;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(stockInfo);
    }
    
    // ==================== HISTORIAL DE ENTREGAS ====================
    @GetMapping("/{idSolicitud}/entregas")
    public ResponseEntity<?> getHistorialEntregas(@PathVariable Long idSolicitud) {
        SolicitudInsumo solicitud = solicitudInsumoService.obtenerPorId(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        List<Map<String, Object>> historial = new ArrayList<>();
        for (SolicitudDetInsumo detalle : solicitud.getDetalles()) {
            Map<String, Object> entrega = new HashMap<>();
            entrega.put("insumo", detalle.getInsumo().getNombreInsumo());
            entrega.put("solicitado", detalle.getCantidadSolicitada());
            entrega.put("entregado", detalle.getCantidadEntregada() != null ? detalle.getCantidadEntregada() : 0);
            entrega.put("pendiente", detalle.getCantidadSolicitada() - (detalle.getCantidadEntregada() != null ? detalle.getCantidadEntregada() : 0));
            entrega.put("estado", detalle.getEstadoSoliDetalle());
            if (detalle.getResponsableInsumo() != null && detalle.getResponsableInsumo().getUsuario() != null) {
                entrega.put("entregadoPor", detalle.getResponsableInsumo().getUsuario().getPersona().getNombre());
            }
            historial.add(entrega);
        }
        
        return ResponseEntity.ok(historial);
    }

    // ==================== MÉTODO AUXILIAR ====================
    private String calcularPendientes(SolicitudInsumo solicitud) {
        List<String> pendientes = new ArrayList<>();
        for (SolicitudDetInsumo detalle : solicitud.getDetalles()) {
            Integer entregado = detalle.getCantidadEntregada() != null ? detalle.getCantidadEntregada() : 0;
            Integer pendiente = detalle.getCantidadSolicitada() - entregado;
            if (pendiente > 0) {
                pendientes.add(detalle.getInsumo().getNombreInsumo() + ": " + pendiente);
            }
        }
        return String.join(", ", pendientes);
    }
}