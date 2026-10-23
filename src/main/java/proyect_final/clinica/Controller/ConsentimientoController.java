package proyect_final.clinica.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import proyect_final.clinica.Model.Entity.*;
import proyect_final.clinica.Model.Dto.*;
import proyect_final.clinica.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/consentimientos")
public class ConsentimientoController {

    @Autowired
    private ConsentimientoService consentimientoService;

    @Autowired
    private DiagnosticoService diagnosticoService;

    @Autowired
    private DiagnosticoTratamientoService diagnosticoTratamientoService;

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private DiagnosticoTratamientoDienteService diagnosticoTratamientoDienteService;

    @Autowired
    private SolicitudInsumoService solicitudInsumoService;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private InscripcionMateriaService inscripcionMateriaService;

    @Autowired
    private TratamientoInsumoService tratamientoInsumoService;

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private EvolucionClinicaService evolucionService;

    // ================================================================
    // ============ NUEVOS ENDPOINTS PARA EL FRONTEND ============
    // ================================================================

    /**
     * OBTENER CONSENTIMIENTOS DE UN DOCENTE
     * GET /api/consentimientos/docente/{idDocente}
     */
    @GetMapping("/api/consentimientos/docente/{idDocente}")
    @ResponseBody
    public ResponseEntity<?> getConsentimientosByDocente(@PathVariable Long idDocente) {
        try {
            List<Consentimiento> consentimientos = consentimientoService.findByDocenteId(idDocente);
            
            List<Map<String, Object>> resultado = new ArrayList<>();
            
            for (Consentimiento c : consentimientos) {
                Map<String, Object> item = new HashMap<>();
                item.put("idConsentimiento", c.getIdConsentimiento());
                item.put("estado", c.getEstado());
                item.put("decision", c.getDecision());
                item.put("fecha", c.getFecha());
                
                DiagnosticoTratamiento diagTrat = c.getDiagnosticoTratamiento();
                if (diagTrat != null) {
                    item.put("idDiagnosticoTratamiento", diagTrat.getIdDiagTrat());
                    
                    Tratamiento tratamiento = diagTrat.getTratamiento();
                    if (tratamiento != null) {
                        item.put("tratamientoNombre", tratamiento.getNombreTratamiento());
                        item.put("idTratamiento", tratamiento.getIdTratamiento());
                    }
                    
                    EvolucionClinica evolucion = diagTrat.getEvolucionClinica();
                    if (evolucion != null) {
                        Diagnostico diagnostico = evolucion.getDiagnostico();
                        if (diagnostico != null && diagnostico.getRevision() != null) {
                            var revision = diagnostico.getRevision();
                            if (revision.getConsulta() != null) {
                                var consulta = revision.getConsulta();
                                if (consulta.getPaciente() != null) {
                                    Paciente paciente = consulta.getPaciente();
                                    item.put("idPaciente", paciente.getIdPaciente());
                                    item.put("ciPaciente", paciente.getCi());
                                    
                                    Persona persona = paciente.getPersona();
                                    if (persona != null) {
                                        String nombreCompleto = persona.getNombre() + " " + 
                                            persona.getApellidoPaterno() + " " + 
                                            (persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : "");
                                        item.put("pacienteNombre", nombreCompleto.trim());
                                    }
                                }
                            }
                        }
                    }
                }
                
                Estudiante estudiante = c.getEstudiante();
                if (estudiante != null) {
                    item.put("idEstudiante", estudiante.getIdEstudiante());
                    if (estudiante.getUsuario() != null && estudiante.getUsuario().getPersona() != null) {
                        Persona personaEst = estudiante.getUsuario().getPersona();
                        String nombreEst = personaEst.getNombre() + " " + personaEst.getApellidoPaterno();
                        item.put("estudianteNombre", nombreEst);
                    }
                }
                
                resultado.add(item);
            }
            
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body("Error al obtener consentimientos: " + e.getMessage());
        }
    }

    /**
     * CAMBIAR ESTADO DE CONSENTIMIENTO
     * PUT /api/consentimientos/{id}/estado?estado=APROBADO
     */
    @PutMapping("/api/consentimientos/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoConsentimiento(
            @PathVariable Long id,
            @RequestParam String estado) {
        try {
            if (!estado.equals("APROBADO") && !estado.equals("RECHAZADO")) {
                return ResponseEntity.badRequest().body("Estado inválido. Debe ser APROBADO o RECHAZADO");
            }
            
            Optional<Consentimiento> optional = consentimientoService.obtenerPorId(id);
            if (optional.isEmpty()) {
                return ResponseEntity.badRequest().body("Consentimiento no encontrado");
            }
            
            Consentimiento consentimiento = optional.get();
            consentimiento.setEstado(estado);
            consentimiento.setFechModCon(LocalDateTime.now());
            
            if (estado.equals("APROBADO")) {
                DiagnosticoTratamiento diagTrat = consentimiento.getDiagnosticoTratamiento();
                if (diagTrat != null) {
                    generarSolicitud(diagTrat, consentimiento.getDocente());
                }
            }
            
            consentimientoService.guardar(consentimiento);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Consentimiento " + estado.toLowerCase() + " correctamente",
                "idConsentimiento", id,
                "estado", estado
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body("Error al cambiar estado: " + e.getMessage());
        }
    }

    /**
     * OBTENER FOTOS DE UN CONSENTIMIENTO
     * GET /api/consentimiento-fotos/consentimiento/{idConsentimiento}
     */
    @GetMapping("/api/consentimiento-fotos/consentimiento/{idConsentimiento}")
    @ResponseBody
    public ResponseEntity<?> getFotosByConsentimiento(@PathVariable Long idConsentimiento) {
        try {
            List<ConsentimientoFoto> fotos = consentimientoService.findFotosByConsentimientoId(idConsentimiento);
            
            List<Map<String, Object>> resultado = fotos.stream()
                .map(f -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("idConsentimientoFoto", f.getId_consentimientoFoto());
                    item.put("rutaArchivoConsentimiento", f.getRutaArchivoConsentimiento());
                    item.put("nombreConsentimientoOriginal", f.getNombreConsentimientoOriginal());
                    item.put("tipoContenidoConsentimiento", f.getTipoContenidoConsentimiento());
                    item.put("tamanoConsentimiento", f.getTamanoConsentimiento());
                    return item;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body("Error al obtener fotos: " + e.getMessage());
        }
    }

    // ================================================================
    // ============ ENDPOINTS PARA PROCEDIMIENTOS ============
    // ================================================================

    /**
     * OBTENER PROCEDIMIENTOS PENDIENTES DEL DOCENTE
     * GET /api/docentes/{docenteId}/procedimientos-pendientes
     */
    @GetMapping("/api/docentes/{docenteId}/procedimientos-pendientes")
    @ResponseBody
    public ResponseEntity<?> getProcedimientosPendientes(@PathVariable Long docenteId) {
        try {
            List<DiagnosticoTratamientoDiente> todosDientes = new ArrayList<>();
            todosDientes.addAll(diagnosticoTratamientoDienteService.findPendientesByDocenteId(docenteId));
            todosDientes.addAll(diagnosticoTratamientoDienteService.findCompletadosByDocenteId(docenteId));
            todosDientes.addAll(diagnosticoTratamientoDienteService.findRechazadosByDocenteId(docenteId));
            
            List<Map<String, Object>> procedimientos = new ArrayList<>();
            int totalPendientes = 0;
            int totalAprobados = 0;
            int totalRechazados = 0;
            int cuposRestantes = 0;
            
            for (DiagnosticoTratamientoDiente diente : todosDientes) {
                DiagnosticoTratamiento diagTrat = diente.getDiagnosticoTratamiento();
                
                Consentimiento consentimiento = diagTrat.getConsentimiento();
                if (consentimiento == null || !consentimiento.getEstado().equals("APROBADO")) {
                    continue;
                }
                
                Map<String, Object> item = new HashMap<>();
                // ✅ CORREGIDO: usar el ID correcto de tu entidad
                item.put("idDientePlan", diente.getIdDiagnosticoTratamientoDiente());
                item.put("idDiagnosticoTratamiento", diagTrat.getIdDiagTrat());
                item.put("diente", diente.getDiente());
                item.put("estado", diente.getEstado());
                item.put("observaciones", diente.getObservaciones());
                // ✅ CORREGIDO: usar el getter correcto
                item.put("fechaRegistro", diente.getFechRegDiaTraDie());
                
                Tratamiento tratamiento = diagTrat.getTratamiento();
                if (tratamiento != null) {
                    item.put("tratamientoNombre", tratamiento.getNombreTratamiento());
                    item.put("idTratamiento", tratamiento.getIdTratamiento());
                    item.put("procedimientoRealizado", tratamiento.getNombreTratamiento());
                }
                
                Integer cupos = diagTrat.getCantidadPendiente();
                item.put("cuposDisponibles", cupos != null ? cupos : 0);
                
                EvolucionClinica evolucion = diagTrat.getEvolucionClinica();
                if (evolucion != null) {
                    Diagnostico diagnostico = evolucion.getDiagnostico();
                    if (diagnostico != null && diagnostico.getRevision() != null) {
                        var revision = diagnostico.getRevision();
                        if (revision.getConsulta() != null) {
                            var consulta = revision.getConsulta();

                            if (consulta.getPaciente() != null) {
                                Paciente paciente = consulta.getPaciente();
                                item.put("pacienteCi", paciente.getCi());

                                Persona persona = paciente.getPersona();
                                if (persona != null) {
                                    String nombre = persona.getNombre() + " " + persona.getApellidoPaterno();
                                    item.put("pacienteNombre", nombre);
                                }
                            }

                            Estudiante est = consulta.getEstudiante();
                            if (est != null) {
                                item.put("idEstudiante", est.getIdEstudiante());
                                item.put("ruEstudiante", est.getCodigoEstudiante());
                                if (est.getUsuario() != null && est.getUsuario().getPersona() != null) {
                                    Persona personaEst = est.getUsuario().getPersona();
                                    item.put("nombreEstudiante",
                                        personaEst.getNombre() + " " + personaEst.getApellidoPaterno());
                                }
                            }
                        }
                    }
                }
                
                switch (diente.getEstado()) {
                    case "PENDIENTE_APROBACION":
                        totalPendientes++;
                        cuposRestantes += (cupos != null ? cupos : 0);
                        break;
                    case "COMPLETADO":
                        totalAprobados++;
                        break;
                    case "RECHAZADO":
                        totalRechazados++;
                        break;
                }
                
                procedimientos.add(item);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("procedimientos", procedimientos);
            response.put("totalPendientes", totalPendientes);
            response.put("totalAprobados", totalAprobados);
            response.put("totalRechazados", totalRechazados);
            response.put("cuposRestantes", cuposRestantes);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body("Error al obtener procedimientos: " + e.getMessage());
        }
    }

    /**
     * APROBAR PROCEDIMIENTO (descontar cupo)
     * PUT /api/procedimientos/{idDientePlan}/aprobar
     */
    @PutMapping("/api/procedimientos/{idDientePlan}/aprobar")
    @ResponseBody
    public ResponseEntity<?> aprobarProcedimiento(
            @PathVariable Long idDientePlan,
            @RequestBody Map<String, Object> request) {
        try {
            Long docenteId = ((Number) request.get("docenteId")).longValue();
            
            // ✅ CORREGIDO: usar obtenerPorId
            Optional<DiagnosticoTratamientoDiente> optional = diagnosticoTratamientoDienteService
                .obtenerPorId(idDientePlan);
            if (optional.isEmpty()) {
                return ResponseEntity.badRequest().body("Diente plan no encontrado");
            }
            
            DiagnosticoTratamientoDiente dientePlan = optional.get();
            
            if (!dientePlan.getEstado().equals("PENDIENTE_APROBACION")) {
                return ResponseEntity.badRequest().body("Este procedimiento ya fue procesado");
            }
            
            DiagnosticoTratamiento diagTrat = dientePlan.getDiagnosticoTratamiento();
            if (diagTrat == null) {
                return ResponseEntity.badRequest().body("Diagnóstico de tratamiento no encontrado");
            }
            
            Integer cantidadPendiente = diagTrat.getCantidadPendiente();
            if (cantidadPendiente == null || cantidadPendiente <= 0) {
                return ResponseEntity.badRequest().body("No hay cupos disponibles para este tratamiento");
            }
            
            // Descontar cupo
            diagTrat.setCantidadPendiente(cantidadPendiente - 1);
            
            // Incrementar cantidad realizada
            Integer cantidadRealizada = diagTrat.getCantidadRealizada();
            diagTrat.setCantidadRealizada((cantidadRealizada != null ? cantidadRealizada : 0) + 1);
            
            // Marcar el diente como completado
            dientePlan.setEstado("COMPLETADO");
            // ✅ CORREGIDO: usar setFechModDiaTraDie
            dientePlan.setFechModDiaTraDie(LocalDateTime.now());
            
            if (docenteId != null) {
                // ✅ CORREGIDO: usar setUsuModDiaTraDie
                dientePlan.setUsuModDiaTraDie(docenteId.intValue());
                diagTrat.setUsuModDiaTra(docenteId.intValue());
            }
            
            diagnosticoTratamientoDienteService.guardar(dientePlan);
            diagnosticoTratamientoService.guardar(diagTrat);
            
            if (diagTrat.getCantidadPendiente() <= 0) {
                diagTrat.setEstado("COMPLETADO");
                diagnosticoTratamientoService.guardar(diagTrat);
            }
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Procedimiento aprobado correctamente",
                "idDientePlan", idDientePlan,
                "cuposRestantes", diagTrat.getCantidadPendiente()
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body("Error al aprobar procedimiento: " + e.getMessage());
        }
    }

    /**
     * RECHAZAR PROCEDIMIENTO
     * PUT /api/procedimientos/{idDientePlan}/rechazar
     */
    @PutMapping("/api/procedimientos/{idDientePlan}/rechazar")
    @ResponseBody
    public ResponseEntity<?> rechazarProcedimiento(
            @PathVariable Long idDientePlan,
            @RequestBody Map<String, Object> request) {
        try {
            String motivo = (String) request.get("motivo");
            Long docenteId = ((Number) request.get("docenteId")).longValue();
            
            if (motivo == null || motivo.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Debe proporcionar un motivo de rechazo");
            }
            
            // ✅ CORREGIDO: usar obtenerPorId
            Optional<DiagnosticoTratamientoDiente> optional = diagnosticoTratamientoDienteService
                .obtenerPorId(idDientePlan);
            if (optional.isEmpty()) {
                return ResponseEntity.badRequest().body("Diente plan no encontrado");
            }
            
            DiagnosticoTratamientoDiente dientePlan = optional.get();
            
            if (!dientePlan.getEstado().equals("PENDIENTE_APROBACION")) {
                return ResponseEntity.badRequest().body("Este procedimiento ya fue procesado");
            }
            
            dientePlan.setEstado("RECHAZADO");
            dientePlan.setObservaciones(motivo);
            // ✅ CORREGIDO: usar setFechModDiaTraDie
            dientePlan.setFechModDiaTraDie(LocalDateTime.now());
            
            if (docenteId != null) {
                // ✅ CORREGIDO: usar setUsuModDiaTraDie
                dientePlan.setUsuModDiaTraDie(docenteId.intValue());
            }
            
            diagnosticoTratamientoDienteService.guardar(dientePlan);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Procedimiento rechazado correctamente",
                "idDientePlan", idDientePlan,
                "motivo", motivo
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body("Error al rechazar procedimiento: " + e.getMessage());
        }
    }

    // ================================================================
    // ============ MÉTODO GENERAR SOLICITUD ============
    // ================================================================

    private void generarSolicitud(DiagnosticoTratamiento diagnosticoTratamiento, Docente docente) {
        try {
            Tratamiento tratamiento = diagnosticoTratamiento.getTratamiento();
            if (tratamiento == null) return;
            
            List<TratamientoInsumo> tratamientoInsumos = tratamientoInsumoService
                .findByTratamientoId(tratamiento.getIdTratamiento());
            
            if (tratamientoInsumos.isEmpty()) return;
            
            SolicitudInsumo solicitud = new SolicitudInsumo();
            solicitud.setDiagnosticoTratamiento(diagnosticoTratamiento);
            solicitud.setFechaSolicitud(LocalDate.now());
            solicitud.setEstadoSolicitud("PENDIENTE_DOCENTE");
            
            SolicitudInsumo solicitudGuardada = solicitudInsumoService.guardar(solicitud);
            
            for (TratamientoInsumo ti : tratamientoInsumos) {
                SolicitudDetInsumo detalle = new SolicitudDetInsumo();
                detalle.setSolicitudInsumo(solicitudGuardada);
                detalle.setInsumo(ti.getInsumo());
                detalle.setCantidadSolicitada(ti.getCantidadRequerida());
                detalle.setCantidadEntregada(0);
                detalle.setEstadoSoliDetalle("PENDIENTE_DOCENTE");
                detalle.setDocente(docente);
                solicitudInsumoService.guardarDetalle(detalle);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================================================================
    // ============ MÉTODOS EXISTENTES ============
    // ================================================================

    @GetMapping("/buscar-diagnostico")
    @ResponseBody
    public ResponseEntity<?> buscarDiagnostico(@RequestParam String criterio) {
        try {
            Long idDiagnostico = Long.parseLong(criterio.trim());
            
            Map<String, Object> resultado = diagnosticoService
                .buscarDiagnosticoFormateado(idDiagnostico);
            
            return ResponseEntity.ok(List.of(resultado));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("El ID debe ser un número válido");
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al buscar diagnóstico: " + e.getMessage());
        }
    }

    @GetMapping("/buscar-por-evolucion")
    @ResponseBody
    public ResponseEntity<?> buscarPorEvolucion(@RequestParam Long idEvolucion) {
        try {
            EvolucionClinica evolucion = evolucionService.obtenerPorId(idEvolucion);
            
            if (evolucion == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            Diagnostico diagnostico = evolucion.getDiagnostico();
            if (diagnostico == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("idEvolucionClinica", evolucion.getIdEvolucionClinica());
            resultado.put("idDiagnostico", diagnostico.getIdDiagnostico());
            resultado.put("tipoRegistro", evolucion.getTipoRegistro());
            resultado.put("fechaHora", evolucion.getFechaHora());
            resultado.put("subjetivo", evolucion.getSubjetivo());
            resultado.put("objetivo", evolucion.getObjetivo());
            resultado.put("analisis", evolucion.getAnalisis());
            resultado.put("planAccion", evolucion.getPlanAccion());
            resultado.put("descripcionDiagnostico", diagnostico.getDescripcion());
            
            if (diagnostico.getRevision() != null && 
                diagnostico.getRevision().getConsulta() != null &&
                diagnostico.getRevision().getConsulta().getPaciente() != null) {
                
                var consulta = diagnostico.getRevision().getConsulta();
                var paciente = consulta.getPaciente();
                var persona = paciente.getPersona();
                
                resultado.put("nombrePaciente", 
                    persona.getNombre() + " " + persona.getApellidoPaterno() + " " + persona.getApellidoMaterno());
                resultado.put("edad", persona.getEdad());
                resultado.put("idPaciente", paciente.getIdPaciente());
                resultado.put("ci", paciente.getCi());
                resultado.put("historialClinico", paciente.getHistorialClinico());
            }
            
            List<DiagnosticoTratamiento> diagTrats = diagnosticoTratamientoService
                .findByEvolucionClinicaId(evolucion.getIdEvolucionClinica());
            
            List<Map<String, Object>> tratamientos = diagTrats.stream()
                .map(dt -> {
                    Map<String, Object> t = new HashMap<>();
                    t.put("idDiagTrat", dt.getIdDiagTrat());
                    t.put("nombreTratamiento", dt.getTratamiento().getNombreTratamiento());
                    t.put("observaciones", dt.getObservaciones());
                    return t;
                })
                .collect(Collectors.toList());
            
            resultado.put("diagnosticoTratamientos", tratamientos);
            
            return ResponseEntity.ok(List.of(resultado));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al buscar evolución: " + e.getMessage());
        }
    }
    
    @GetMapping("/insumos-por-diag-trat/{idDiagTrat}")
    @ResponseBody
    public ResponseEntity<?> obtenerInsumosPorDiagnosticoTratamiento(@PathVariable Long idDiagTrat) {
        try {
            DiagnosticoTratamiento diagTrat = diagnosticoTratamientoService
                .obtenerPorId(idDiagTrat)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
            
            List<TratamientoInsumo> tratamientoInsumos = tratamientoInsumoService
                .findByTratamientoId(diagTrat.getTratamiento().getIdTratamiento());
            
            if (tratamientoInsumos.isEmpty()) {
                return ResponseEntity.ok(Map.of("mensaje", "Sin insumos asociados", "insumos", new ArrayList<>()));
            }
            
            List<Map<String, Object>> insumos = tratamientoInsumos.stream()
                .map(ti -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idInsumo", ti.getInsumo().getIdInsumo());
                    map.put("nombreInsumo", ti.getInsumo().getNombreInsumo());
                    map.put("cantidadRequerida", ti.getCantidadRequerida());
                    return map;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "insumos", insumos,
                "idDiagTrat", idDiagTrat,
                "nombreTratamiento", diagTrat.getTratamiento().getNombreTratamiento()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar-diag-trat/{idDiagTrat}")
    @ResponseBody
    public ResponseEntity<?> actualizarDiagnosticoTratamiento(
            @PathVariable Long idDiagTrat,
            @RequestBody Map<String, String> datos) {
        try {
            DiagnosticoTratamiento diagTrat = diagnosticoTratamientoService
                .obtenerPorId(idDiagTrat)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
            
            if (datos.containsKey("observaciones") && datos.get("observaciones") != null) {
                diagTrat.setObservaciones(datos.get("observaciones").trim());
            }
            
            diagnosticoTratamientoService.guardar(diagTrat);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Actualizado correctamente",
                "idDiagTrat", diagTrat.getIdDiagTrat()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/crear-diagnostico-tratamiento")
    @ResponseBody
    public ResponseEntity<?> crearDiagnosticoTratamiento(@RequestBody Map<String, Object> datos) {
        try {
            Long idEvolucionClinica = Long.valueOf(datos.get("idEvolucionClinica").toString());
            Long idTratamiento = Long.valueOf(datos.get("idTratamiento").toString());
            
            EvolucionClinica evolucion = evolucionService.obtenerPorId(idEvolucionClinica);
            if (evolucion == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Evolución clínica no encontrada para el ID: " + idEvolucionClinica
                ));
            }
            
            Tratamiento tratamiento = tratamientoService.obtenerPorId(idTratamiento)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado"));
            
            List<Integer> dientesList = new ArrayList<>();
            if (datos.containsKey("dientes") && datos.get("dientes") != null) {
                Object dientesObj = datos.get("dientes");
                if (dientesObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> dientesRaw = (List<Object>) dientesObj;
                    for (Object d : dientesRaw) {
                        if (d instanceof Integer) {
                            dientesList.add((Integer) d);
                        } else if (d instanceof String) {
                            try {
                                dientesList.add(Integer.parseInt((String) d));
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                } else if (dientesObj instanceof String) {
                    String dientesStr = (String) dientesObj;
                    if (!dientesStr.trim().isEmpty()) {
                        String[] partes = dientesStr.split(",");
                        for (String p : partes) {
                            try {
                                dientesList.add(Integer.parseInt(p.trim()));
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
            
            if (dientesList.isEmpty() && datos.containsKey("dienteAfectado")) {
                String dienteAfectado = datos.get("dienteAfectado").toString();
                if (!dienteAfectado.trim().isEmpty()) {
                    String[] partes = dienteAfectado.split(",");
                    for (String p : partes) {
                        try {
                            dientesList.add(Integer.parseInt(p.trim()));
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
            
            int cantidadPlaneada = dientesList.size();
            
            DiagnosticoTratamiento diagTrat = new DiagnosticoTratamiento();
            diagTrat.setEvolucionClinica(evolucion);
            diagTrat.setTratamiento(tratamiento);
            diagTrat.setObservaciones(
                datos.get("observaciones") != null ? datos.get("observaciones").toString() : ""
            );
            diagTrat.setEstado("PENDIENTE");
            diagTrat.setCantidadPlaneada(cantidadPlaneada);
            diagTrat.setCantidadRealizada(0);
            diagTrat.setCantidadPendiente(cantidadPlaneada);
            
            DiagnosticoTratamiento guardado = diagnosticoTratamientoService.guardar(diagTrat);
            
            if (!dientesList.isEmpty()) {
                List<DiagnosticoTratamientoDiente> dientesGuardados = new ArrayList<>();
                for (Integer numeroDiente : dientesList) {
                    DiagnosticoTratamientoDiente diente = new DiagnosticoTratamientoDiente();
                    diente.setDiagnosticoTratamiento(guardado);
                    diente.setDiente(numeroDiente);
                    diente.setEstado("PENDIENTE");
                    diente.setObservaciones("Diente programado para tratamiento");
                    dientesGuardados.add(diente);
                }
                diagnosticoTratamientoDienteService.guardarTodos(dientesGuardados);
            }
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Creado correctamente",
                "idDiagTrat", guardado.getIdDiagTrat(),
                "idEvolucionClinica", guardado.getEvolucionClinica().getIdEvolucionClinica(),
                "cantidadPlaneada", cantidadPlaneada,
                "dientes", dientesList
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al crear: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping(value = "/crear-desde-diagnostico", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> crearConsentimientoDesdeDiagnostico(
            @RequestParam("datos") String datosJson,
            @RequestParam(value = "fotos", required = false) MultipartFile[] fotos) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ConsentimientoDTO dto = mapper.readValue(datosJson, ConsentimientoDTO.class);
            
            if (dto.getIdDiagnosticoTratamiento() == null || 
                dto.getIdDocente() == null || 
                dto.getIdEstudiante() == null || 
                dto.getIdMateria() == null) {
                return ResponseEntity.badRequest().body("Faltan datos requeridos");
            }
            
            if (!dto.getDecision().equals("aceptar") && !dto.getDecision().equals("rechazar")) {
                return ResponseEntity.badRequest().body("Decisión inválida");
            }
            
            DiagnosticoTratamiento diagnosticoTratamiento = diagnosticoTratamientoService
                .obtenerPorId(dto.getIdDiagnosticoTratamiento())
                .orElseThrow(() -> new RuntimeException("DiagnosticoTratamiento no encontrado"));
            
            Docente docente = docenteService.obtenerPorId(dto.getIdDocente())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
            
            Estudiante estudiante = estudianteService.obtenerPorId(dto.getIdEstudiante())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
            
            inscripcionMateriaService
                .obtenerInscripcionActivaPorEstudianteYMateria(dto.getIdEstudiante(), dto.getIdMateria())
                .orElseThrow(() -> new RuntimeException("Estudiante no inscrito en esta materia"));
            
            Consentimiento consentimiento = new Consentimiento();
            consentimiento.setDiagnosticoTratamiento(diagnosticoTratamiento);
            consentimiento.setDocente(docente);
            consentimiento.setEstudiante(estudiante);
            consentimiento.setDecision(dto.getDecision());
            consentimiento.setFecha(LocalDateTime.now());
            consentimiento.setEstado("PENDIENTE");
            
            Consentimiento guardado = consentimientoService.guardarConFotos(consentimiento, fotos);
            
            if ("aceptar".equals(dto.getDecision())) {
                generarSolicitud(diagnosticoTratamiento, docente);
            }
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Consentimiento guardado",
                "idConsentimiento", guardado.getIdConsentimiento()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/obtener-docentes")
    @ResponseBody
    public ResponseEntity<?> obtenerDocentes() {
        try {
            List<Docente> docentes = docenteService.obtenerTodosActivos();
            
            List<DocenteDTO> docentesDTO = docentes.stream()
                .map(d -> {
                    String nombre = "Sin nombre";
                    if (d.getUsuario() != null && d.getUsuario().getPersona() != null) {
                        Persona p = d.getUsuario().getPersona();
                        nombre = p.getNombre() + " " + p.getApellidoPaterno();
                    }
                    return new DocenteDTO(d.getIdDocente(), nombre, d.getEspecialidad(), d.getEstado());
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(docentesDTO);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error: " + e.getMessage());
        }
    }
}