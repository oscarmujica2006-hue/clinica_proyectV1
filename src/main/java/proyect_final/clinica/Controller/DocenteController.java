package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.*;
import proyect_final.clinica.Model.Dto.DocenteDTO;
import proyect_final.clinica.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/docentes")
public class DocenteController {

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private DiagnosticoTratamientoDienteService dientePlanService;
    
    @Autowired
    private DiagnosticoTratamientoService diagnosticoTratamientoService;
    
    @Autowired
    private CupoService cupoService;
    
    @Autowired
    private DetalleEvolucionClinicaService detalleEvolucionService;
    
    @Autowired
    private DiagnosticoService diagnosticoService;

    @Autowired
    private ReciboService reciboService;

    // ==================== CRUD BÁSICO ====================
    
    @GetMapping
    public ResponseEntity<List<DocenteDTO>> listarTodos() {
        List<DocenteDTO> docentes = docenteService.obtenerTodosDTO();
        return ResponseEntity.ok(docentes);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<DocenteDTO>> listarActivos() {
        List<DocenteDTO> docentes = docenteService.obtenerActivosDTO();
        return ResponseEntity.ok(docentes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocenteDTO> obtenerPorId(@PathVariable Long id) {
        return docenteService.obtenerDTOPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody DocenteDTO docenteDTO) {
        try {
            if (docenteDTO.getCodigoDocente() != null && 
                docenteService.existePorCodigoDocente(docenteDTO.getCodigoDocente())) {
                
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ya existe un docente con el código: " + docenteDTO.getCodigoDocente());
                return ResponseEntity.badRequest().body(error);
            }
            
            Docente docente = docenteService.convertirAEntidad(docenteDTO);
            Docente guardado = docenteService.guardar(docente);
            DocenteDTO resultado = docenteService.convertirADTO(guardado);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear docente: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id, 
            @RequestBody DocenteDTO docenteDTO) {
        
        try {
            if (!docenteService.obtenerPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            if (docenteDTO.getCodigoDocente() != null) {
                Optional<Docente> docenteExistente = docenteService.obtenerPorCodigoDocente(docenteDTO.getCodigoDocente());
                if (docenteExistente.isPresent() && !docenteExistente.get().getIdDocente().equals(id)) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "El código ya está en uso por otro docente");
                    return ResponseEntity.badRequest().body(error);
                }
            }
            
            docenteDTO.setIdDocente(id);
            Docente docente = docenteService.convertirAEntidad(docenteDTO);
            Docente actualizado = docenteService.guardar(docente);
            DocenteDTO resultado = docenteService.convertirADTO(actualizado);
            
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (!docenteService.obtenerPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            docenteService.eliminar(id);
            
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Docente eliminado (desactivado) exitosamente");
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    // ==================== SELECTORES Y ESTADÍSTICAS ====================
    
    @GetMapping("/selectores")
    public ResponseEntity<List<Map<String, Object>>> obtenerParaSelectores() {
        List<DocenteDTO> docentes = docenteService.obtenerActivosDTO();
        List<Map<String, Object>> simplificado = docentes.stream()
            .map(d -> {
                Map<String, Object> mapa = new HashMap<>();
                mapa.put("id", d.getIdDocente());
                mapa.put("nombre", d.getNombreCompleto());
                mapa.put("especialidad", d.getEspecialidad() != null ? d.getEspecialidad() : "");
                mapa.put("codigo", d.getCodigoDocente());
                return mapa;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(simplificado);
    }
    
    @GetMapping("/especialidades")
    public ResponseEntity<List<String>> listarEspecialidades() {
        List<String> especialidades = docenteService.obtenerTodosDTO().stream()
                .map(DocenteDTO::getEspecialidad)
                .filter(e -> e != null && !e.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(especialidades);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticas() {
        List<DocenteDTO> todos = docenteService.obtenerTodosDTO();
        long activos = todos.stream().filter(DocenteDTO::getEstado).count();
        
        Map<String, Long> estadisticas = new HashMap<>();
        estadisticas.put("total", (long) todos.size());
        estadisticas.put("activos", activos);
        estadisticas.put("inactivos", todos.size() - activos);
        
        return ResponseEntity.ok(estadisticas);
    }

    // ==================== PROCEDIMIENTOS PENDIENTES ====================
    
    @GetMapping("/procedimientos-pendientes")
    public ResponseEntity<?> obtenerProcedimientosPendientes(@RequestParam Long docenteId) {
        try {
            Docente docente = docenteService.obtenerPorId(docenteId)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
            
            Clinica clinica = docente.getClinica();
            if (clinica == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "El docente no tiene una clínica asignada"
                ));
            }
            
            List<Map<String, Object>> procedimientosPendientes = new ArrayList<>();
            List<DiagnosticoTratamiento> todosPlanes = diagnosticoTratamientoService.findAll();
            
            for (DiagnosticoTratamiento plan : todosPlanes) {
                List<DiagnosticoTratamientoDiente> dientes = dientePlanService
                    .findByDiagnosticoTratamientoId(plan.getIdDiagTrat());
                
                for (DiagnosticoTratamientoDiente diente : dientes) {
                    if ("PENDIENTE_APROBACION".equals(diente.getEstado())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("idDientePlan", diente.getIdDiagnosticoTratamientoDiente());
                        map.put("diente", diente.getDiente());
                        map.put("estado", diente.getEstado());
                        map.put("idPlan", plan.getIdDiagTrat());
                        map.put("tratamientoNombre", plan.getTratamiento().getNombreTratamiento());
                        map.put("precioTratamiento", plan.getTratamiento().getPrecioTratamiento());
                        
                        // Obtener paciente
                        EvolucionClinica evolucion = plan.getEvolucionClinica();
                        if (evolucion != null) {
                            Diagnostico diagnostico = evolucion.getDiagnostico();
                            if (diagnostico != null && diagnostico.getRevision() != null) {
                                var consulta = diagnostico.getRevision().getConsulta();
                                if (consulta != null && consulta.getPaciente() != null) {
                                    var paciente = consulta.getPaciente();
                                    map.put("pacienteNombre", paciente.getPersona().getNombre() + " " + 
                                                           paciente.getPersona().getApellidoPaterno());
                                    map.put("pacienteCi", paciente.getCi());
                                }
                            }
                        }
                        
                        // Obtener el detalle de la evolución
                        List<DetalleEvolucionClinica> detalles = detalleEvolucionService
                            .findByDientePlanId(diente.getIdDiagnosticoTratamientoDiente());
                        
                        if (!detalles.isEmpty()) {
                            DetalleEvolucionClinica ultimoDetalle = detalles.get(detalles.size() - 1);
                            map.put("procedimientoRealizado", ultimoDetalle.getProcedimientoRealizado());
                            map.put("observaciones", ultimoDetalle.getObservaciones());
                            map.put("fechaRegistro", ultimoDetalle.getFechRegDetEvo());
                        }
                        
                        int cuposDisponibles = obtenerCuposDisponibles(plan.getTratamiento().getIdTratamiento());
                        map.put("cuposDisponibles", cuposDisponibles);
                        
                        procedimientosPendientes.add(map);
                    }
                }
            }
            
            long totalPendientes = procedimientosPendientes.size();
            long totalAprobados = contarAprobados();
            long totalRechazados = contarRechazados();
            int cuposRestantes = obtenerCuposTotalesDisponibles();
            
            return ResponseEntity.ok(Map.of(
                "procedimientos", procedimientosPendientes,
                "totalPendientes", totalPendientes,
                "totalAprobados", totalAprobados,
                "totalRechazados", totalRechazados,
                "cuposRestantes", cuposRestantes,
                "clinica", clinica.getNombreClinica()
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al obtener procedimientos: " + e.getMessage()
            ));
        }
    }

    // ==================== APROBAR PROCEDIMIENTO (TRIGGER HACE TODO) ====================
    
    @PutMapping("/procedimientos/{idDientePlan}/aprobar")
    public ResponseEntity<?> aprobarProcedimiento(
            @PathVariable Long idDientePlan,
            @RequestParam Long docenteId) {
        try {
            // 1. Obtener el diente
            DiagnosticoTratamientoDiente dientePlan = dientePlanService.obtenerPorId(idDientePlan)
                .orElseThrow(() -> new RuntimeException("Diente no encontrado"));
            
            // 2. Validar estado
            if (!"PENDIENTE_APROBACION".equals(dientePlan.getEstado())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "El diente no está pendiente de aprobación"
                ));
            }
            
            // 3. Obtener el plan
            DiagnosticoTratamiento plan = dientePlan.getDiagnosticoTratamiento();
            Long idTratamiento = plan.getTratamiento().getIdTratamiento();
            
            // 4. Validar cupos
            Cupo cupo = cupoService.findByTratamientoId(idTratamiento);
            if (cupo == null || cupo.getCuposDisponibles() <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No hay cupos disponibles"
                ));
            }
            
            // 5. Cambiar estado - EL TRIGGER DESCUENTA EL CUPO Y CREA/ACTUALIZA RECIBO
            dientePlan.setEstado("APROBADO");
            dientePlanService.guardar(dientePlan);
            
            // 6. Actualizar progreso
            List<DiagnosticoTratamientoDiente> todosDientes = dientePlanService
                .findByDiagnosticoTratamientoId(plan.getIdDiagTrat());
            
            long total = todosDientes.size();
            long aprobados = todosDientes.stream()
                .filter(d -> "APROBADO".equals(d.getEstado()))
                .count();
            
            plan.setCantidadRealizada((int) aprobados);
            plan.setCantidadPendiente((int) (total - aprobados));
            
            if (aprobados == total) {
                plan.setEstado("COMPLETADO");
            } else {
                plan.setEstado("EN_PROCESO");
            }
            
            diagnosticoTratamientoService.guardar(plan);
            
            // 7. Obtener cupos actualizados (después del trigger)
            Cupo cupoActualizado = cupoService.findByTratamientoId(idTratamiento);
            int cuposRestantes = cupoActualizado != null ? cupoActualizado.getCuposDisponibles() : 0;
            
            // 8. OBTENER EL RECIBO GENERADO POR EL TRIGGER (blindado contra consentimiento nulo)
            Long idConsentimiento = plan.getConsentimiento() != null
                ? plan.getConsentimiento().getIdConsentimiento()
                : null;
            Recibo reciboGenerado = null;
            if (idConsentimiento != null) {
                reciboGenerado = reciboService.findByConsentimientoId(idConsentimiento);
            } else if (plan.getEvolucionClinica() != null) {
                reciboGenerado = reciboService.findByEvolucionClinicaId(
                    plan.getEvolucionClinica().getIdEvolucionClinica());
            }

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "✅ Procedimiento aprobado - Trigger descontó cupo y actualizó recibo");
            respuesta.put("idDientePlan", idDientePlan);
            respuesta.put("cuposRestantes", cuposRestantes);
            respuesta.put("progreso", aprobados + "/" + total);
            respuesta.put("idRecibo", reciboGenerado != null ? reciboGenerado.getIdRecibo() : null);
            respuesta.put("montoTotal", reciboGenerado != null ? reciboGenerado.getMontoTotal() : 0);
            respuesta.put("estadoRecibo", reciboGenerado != null ? reciboGenerado.getEstadoPago() : "SIN_RECIBO");

            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error: " + e.getMessage()
            ));
        }
    }
    
    // ==================== RECHAZAR PROCEDIMIENTO ====================
    
    @PutMapping("/procedimientos/{idDientePlan}/rechazar")
    public ResponseEntity<?> rechazarProcedimiento(
            @PathVariable Long idDientePlan,
            @RequestBody Map<String, String> datos) {
        try {
            String motivo = datos.get("motivo");
            if (motivo == null || motivo.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Debe proporcionar un motivo para el rechazo"
                ));
            }
            
            DiagnosticoTratamientoDiente dientePlan = dientePlanService.obtenerPorId(idDientePlan)
                .orElseThrow(() -> new RuntimeException("Diente del plan no encontrado"));
            
            if (!"PENDIENTE_APROBACION".equals(dientePlan.getEstado())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "El diente no está en estado pendiente de aprobación"
                ));
            }
            
            dientePlan.setEstado("REALIZADO");
            dientePlanService.guardar(dientePlan);
            
            List<DetalleEvolucionClinica> detalles = detalleEvolucionService
                .findByDientePlanId(idDientePlan);
            
            if (!detalles.isEmpty()) {
                DetalleEvolucionClinica ultimo = detalles.get(detalles.size() - 1);
                String obsActual = ultimo.getObservaciones() != null ? ultimo.getObservaciones() : "";
                ultimo.setObservaciones(obsActual + " | RECHAZADO POR DOCENTE: " + motivo);
                detalleEvolucionService.guardar(ultimo);
            }
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "❌ Procedimiento rechazado. El estudiante deberá corregirlo.",
                "motivo", motivo,
                "nuevoEstado", "REALIZADO"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al rechazar: " + e.getMessage()
            ));
        }
    }

    // ==================== CONSULTAR RECIBO DESDE DOCENTE ====================
    
    @GetMapping("/recibo/{idDiagnosticoTratamiento}")
    public ResponseEntity<?> obtenerReciboPorDiagnostico(@PathVariable Long idDiagnosticoTratamiento) {
        try {
            // Obtener el diagnóstico
            DiagnosticoTratamiento plan = diagnosticoTratamientoService
                .obtenerPorId(idDiagnosticoTratamiento)
                .orElseThrow(() -> new RuntimeException("Diagnóstico no encontrado"));
            
            // Obtener el recibo (blindado contra consentimiento nulo)
            Long idConsentimiento = plan.getConsentimiento() != null
                ? plan.getConsentimiento().getIdConsentimiento()
                : null;
            Recibo recibo = null;
            if (idConsentimiento != null) {
                recibo = reciboService.findByConsentimientoId(idConsentimiento);
            } else if (plan.getEvolucionClinica() != null) {
                recibo = reciboService.findByEvolucionClinicaId(
                    plan.getEvolucionClinica().getIdEvolucionClinica());
            }
            
            if (recibo == null) {
                return ResponseEntity.ok(Map.of(
                    "mensaje", "No existe recibo para este diagnóstico",
                    "idDiagnostico", idDiagnosticoTratamiento
                ));
            }
            
            // Construir respuesta completa
            Map<String, Object> response = new HashMap<>();
            response.put("idRecibo", recibo.getIdRecibo());
            response.put("estadoPago", recibo.getEstadoPago());
            response.put("subtotalTratamientos", recibo.getSubtotalTratamientos());
            response.put("subtotalRadiografias", recibo.getSubtotalRadiografias());
            response.put("montoTotal", recibo.getMontoTotal());
            response.put("montoPagado", recibo.getMontoPagado());
            response.put("saldoPendiente", recibo.getSaldoPendiente());
            response.put("fechaRegistro", recibo.getFechRegRec());
            
            // Detalles
            List<Map<String, Object>> detalles = new ArrayList<>();
            if (recibo.getDetalles() != null) {
                for (DetalleRecibo detalle : recibo.getDetalles()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("idDetalle", detalle.getIdDetalleRecibo());
                    item.put("tipo", detalle.getTipoItem());
                    item.put("cantidad", detalle.getCantidad());
                    item.put("precioUnitario", detalle.getPrecioUnitario());
                    item.put("subtotal", detalle.getSubtotal());
                    
                    if ("TRATAMIENTO".equals(detalle.getTipoItem()) && 
                        detalle.getDiagnosticoTratamientoDiente() != null) {
                        var diente = detalle.getDiagnosticoTratamientoDiente();
                        item.put("diente", diente.getDiente());
                        item.put("tratamiento", diente.getDiagnosticoTratamiento()
                            .getTratamiento().getNombreTratamiento());
                        item.put("estado", diente.getEstado());
                    } else if ("RADIOGRAFIA".equals(detalle.getTipoItem()) && 
                               detalle.getSolicitudRadiografia() != null) {
                        var solicitud = detalle.getSolicitudRadiografia();
                        item.put("nombreRayo", solicitud.getRadiografia().getNombreRayo());
                        item.put("motivo", solicitud.getMotivo());
                        item.put("estado", solicitud.getEstado());
                    }
                    detalles.add(item);
                }
            }
            response.put("detalles", detalles);
            
            // Pagos
            List<Map<String, Object>> pagos = new ArrayList<>();
            if (recibo.getPagos() != null) {
                for (PagoRecibo pago : recibo.getPagos()) {
                    Map<String, Object> pagoMap = new HashMap<>();
                    pagoMap.put("idPago", pago.getIdPagoRecibo());
                    pagoMap.put("monto", pago.getMonto());
                    pagoMap.put("metodoPago", pago.getMetodoPago());
                    pagoMap.put("fechaPago", pago.getFechaPago());
                    pagoMap.put("observaciones", pago.getObservaciones());
                    pagos.add(pagoMap);
                }
            }
            response.put("pagos", pagos);
            response.put("totalItems", detalles.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error: " + e.getMessage()
            ));
        }
    }

    // ==================== ESTADÍSTICAS DOCENTE ====================
    
    @GetMapping("/{docenteId}/estadisticas")
    public ResponseEntity<?> obtenerEstadisticasDocente(@PathVariable Long docenteId) {
        try {
            Docente docente = docenteService.obtenerPorId(docenteId)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
            
            long pendientes = 0;
            long aprobados = 0;
            long rechazados = 0;
            
            List<DiagnosticoTratamiento> todosPlanes = diagnosticoTratamientoService.findAll();
            for (DiagnosticoTratamiento plan : todosPlanes) {
                List<DiagnosticoTratamientoDiente> dientes = dientePlanService
                    .findByDiagnosticoTratamientoId(plan.getIdDiagTrat());
                
                for (DiagnosticoTratamientoDiente diente : dientes) {
                    String estado = diente.getEstado();
                    if ("PENDIENTE_APROBACION".equals(estado)) {
                        pendientes++;
                    } else if ("APROBADO".equals(estado)) {
                        aprobados++;
                    } else if ("RECHAZADO".equals(estado)) {
                        rechazados++;
                    }
                }
            }
            
            int cuposRestantes = obtenerCuposTotalesDisponibles();
            
            return ResponseEntity.ok(Map.of(
                "docente", docente.getUsuario().getPersona().getNombre(),
                "pendientes", pendientes,
                "aprobados", aprobados,
                "rechazados", rechazados,
                "cuposRestantes", cuposRestantes
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error: " + e.getMessage()
            ));
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private int obtenerCuposDisponibles(Long idTratamiento) {
        Cupo cupo = cupoService.findByTratamientoId(idTratamiento);
        return cupo != null ? cupo.getCuposDisponibles() : 0;
    }

    private int obtenerCuposTotalesDisponibles() {
        List<Cupo> todos = cupoService.listarTodos();
        return todos.stream().mapToInt(Cupo::getCuposDisponibles).sum();
    }

    private long contarAprobados() {
        long count = 0;
        List<DiagnosticoTratamiento> todosPlanes = diagnosticoTratamientoService.findAll();
        for (DiagnosticoTratamiento plan : todosPlanes) {
            List<DiagnosticoTratamientoDiente> dientes = dientePlanService
                .findByDiagnosticoTratamientoId(plan.getIdDiagTrat());
            count += dientes.stream().filter(d -> "APROBADO".equals(d.getEstado())).count();
        }
        return count;
    }

    private long contarRechazados() {
        long count = 0;
        List<DiagnosticoTratamiento> todosPlanes = diagnosticoTratamientoService.findAll();
        for (DiagnosticoTratamiento plan : todosPlanes) {
            List<DiagnosticoTratamientoDiente> dientes = dientePlanService
                .findByDiagnosticoTratamientoId(plan.getIdDiagTrat());
            count += dientes.stream().filter(d -> "RECHAZADO".equals(d.getEstado())).count();
        }
        return count;
    }
}