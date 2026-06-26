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

    // ==================== ENDPOINTS EXISTENTES ====================
    
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

    @GetMapping("/buscar")
    public ResponseEntity<List<DocenteDTO>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) Integer codigo) {
        
        List<Docente> docentes;
        
        if (codigo != null) {
            docentes = docenteService.obtenerPorCodigoDocente(codigo)
                    .map(List::of)
                    .orElse(List.of());
        } else if (nombre != null && !nombre.isEmpty()) {
            docentes = docenteService.buscarPorNombre(nombre);
        } else if (especialidad != null && !especialidad.isEmpty()) {
            docentes = docenteService.buscarPorEspecialidad(especialidad);
        } else {
            docentes = docenteService.obtenerTodos();
        }
        
        List<DocenteDTO> resultados = docentes.stream()
                .map(docenteService::convertirADTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(resultados);
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

    // ==================== NUEVOS ENDPOINTS PARA VALIDACIÓN ====================

    /**
     * OBTENER PROCEDIMIENTOS PENDIENTES DE APROBACIÓN
     * GET /api/docentes/procedimientos-pendientes?docenteId=1
     */
    @GetMapping("/procedimientos-pendientes")
    public ResponseEntity<?> obtenerProcedimientosPendientes(@RequestParam Long docenteId) {
        try {
            // Obtener el docente para saber su clínica
            Docente docente = docenteService.obtenerPorId(docenteId)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
            
            // Obtener la clínica del docente
            Clinica clinica = docente.getClinica();
            if (clinica == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "El docente no tiene una clínica asignada"
                ));
            }
            
            // Buscar todos los dientes en estado "PENDIENTE_APROBACION"
            // que pertenezcan a la clínica del docente
            List<Map<String, Object>> procedimientosPendientes = new ArrayList<>();
            List<DiagnosticoTratamiento> todosPlanes = diagnosticoTratamientoService.findAll();
            
            for (DiagnosticoTratamiento plan : todosPlanes) {
                // Verificar que el plan pertenece a la clínica del docente
                // Asumiendo que el tratamiento tiene una relación con la clínica
                // o que el diagnóstico está asociado a una consulta con clínica
                
                List<DiagnosticoTratamientoDiente> dientes = dientePlanService
                    .findByDiagnosticoTratamientoId(plan.getIdDiagTrat());
                
                for (DiagnosticoTratamientoDiente diente : dientes) {
                    if ("PENDIENTE_APROBACION".equals(diente.getEstado())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("idDientePlan", diente.getIdDiagnosticoTratamientoDiente());
                        map.put("diente", diente.getDiente());
                        map.put("estado", diente.getEstado());
                        
                        // Datos del plan
                        map.put("idPlan", plan.getIdDiagTrat());
                        map.put("tratamientoNombre", plan.getTratamiento().getNombreTratamiento());
                        
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
                        
                        // Obtener el detalle de la evolución (procedimiento realizado)
                        List<DetalleEvolucionClinica> detalles = detalleEvolucionService
                            .findByDientePlanId(diente.getIdDiagnosticoTratamientoDiente());
                        
                        if (!detalles.isEmpty()) {
                            DetalleEvolucionClinica ultimoDetalle = detalles.get(detalles.size() - 1);
                            map.put("procedimientoRealizado", ultimoDetalle.getProcedimientoRealizado());
                            map.put("observaciones", ultimoDetalle.getObservaciones());
                            map.put("fechaRegistro", ultimoDetalle.getFechRegDetEvo());
                        }
                        
                        // Obtener cupos disponibles para este tratamiento
                        int cuposDisponibles = obtenerCuposDisponibles(plan.getTratamiento().getIdTratamiento());
                        map.put("cuposDisponibles", cuposDisponibles);
                        
                        procedimientosPendientes.add(map);
                    }
                }
            }
            
            // Calcular estadísticas
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

    /**
     * APROBAR PROCEDIMIENTO - Descuenta del cupo
     * PUT /api/docentes/procedimientos/{idDientePlan}/aprobar?docenteId=1
     */
    @PutMapping("/procedimientos/{idDientePlan}/aprobar")
    public ResponseEntity<?> aprobarProcedimiento(
            @PathVariable Long idDientePlan,
            @RequestParam Long docenteId) {
        try {
            // 1. Obtener el diente del plan
            DiagnosticoTratamientoDiente dientePlan = dientePlanService.obtenerPorId(idDientePlan)
                .orElseThrow(() -> new RuntimeException("Diente del plan no encontrado"));
            
            // 2. Verificar que esté en estado PENDIENTE_APROBACION
            if (!"PENDIENTE_APROBACION".equals(dientePlan.getEstado())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "El diente no está en estado pendiente de aprobación. Estado actual: " + dientePlan.getEstado()
                ));
            }
            
            // 3. Obtener el plan de tratamiento
            DiagnosticoTratamiento plan = dientePlan.getDiagnosticoTratamiento();
            Long idTratamiento = plan.getTratamiento().getIdTratamiento();
            
            // 4. DESCONTAR DEL CUPO
            Cupo cupo = cupoService.findByTratamientoId(idTratamiento);
            
            if (cupo == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No hay cupo disponible para este tratamiento"
                ));
            }
            
            if (cupo.getCuposDisponibles() <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No hay cupos disponibles para este tratamiento"
                ));
            }
            
            // Descontar el cupo
            int nuevoCupo = cupo.getCuposDisponibles() - 1;
            cupo.setCuposDisponibles(nuevoCupo);
            cupoService.guardar(cupo);
            
            // 5. Cambiar estado del diente a APROBADO
            dientePlan.setEstado("APROBADO");
            dientePlanService.guardar(dientePlan);
            
            // 6. Actualizar progreso del plan
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
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "✅ Procedimiento aprobado correctamente",
                "idDientePlan", idDientePlan,
                "nuevoEstado", "APROBADO",
                "cuposRestantes", nuevoCupo,
                "progreso", aprobados + "/" + total
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al aprobar: " + e.getMessage()
            ));
        }
    }

    /**
     * RECHAZAR PROCEDIMIENTO
     * PUT /api/docentes/procedimientos/{idDientePlan}/rechazar
     */
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
            
            // Volver a estado REALIZADO para que el estudiante pueda corregir
            dientePlan.setEstado("REALIZADO");
            dientePlanService.guardar(dientePlan);
            
            // Registrar observaciones del rechazo en el detalle de evolución
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

    /**
     * OBTENER ESTADÍSTICAS DEL DOCENTE
     * GET /api/docentes/{docenteId}/estadisticas
     */
    @GetMapping("/{docenteId}/estadisticas")
    public ResponseEntity<?> obtenerEstadisticasDocente(@PathVariable Long docenteId) {
        try {
            Docente docente = docenteService.obtenerPorId(docenteId)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
            
            // Contar procedimientos por estado
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
            
            // Obtener cupos totales disponibles
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