package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.*;
import proyect_final.clinica.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/seguimiento")
public class SeguimientoController {

    @Autowired
    private DiagnosticoService diagnosticoService;
    
    @Autowired
    private EvolucionClinicaService evolucionService;
    
    @Autowired
    private DiagnosticoTratamientoService diagnosticoTratamientoService;
    
    @Autowired
    private DiagnosticoTratamientoDienteService diagnosticoTratamientoDienteService;
    
    @Autowired
    private DetalleEvolucionClinicaService detalleEvolucionService;

    /**
     * Sirve la página HTML de seguimiento
     */
    @GetMapping
    public String mostrarSeguimiento(@RequestParam(required = false) Long id, Model model) {
        model.addAttribute("idPlanTratamiento", id);
        return "seguimiento";
    }

    /**
     * OBTENER DATOS DEL PLAN DE TRATAMIENTO
     * GET /seguimiento/api/plan/{idPlan}
     */
    @GetMapping("/api/plan/{idPlan}")
    @ResponseBody
    public ResponseEntity<?> obtenerPlanTratamiento(@PathVariable Long idPlan) {
        try {
            Optional<DiagnosticoTratamiento> planOpt = diagnosticoTratamientoService.obtenerPorId(idPlan);
            if (planOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            DiagnosticoTratamiento plan = planOpt.get();
            EvolucionClinica evolucion = plan.getEvolucionClinica();
            Diagnostico diagnostico = evolucion.getDiagnostico();
            
            Map<String, Object> response = new HashMap<>();
            response.put("idPlanTratamiento", plan.getIdDiagTrat());
            response.put("idEvolucionInicial", evolucion.getIdEvolucionClinica());
            response.put("idDiagnostico", diagnostico.getIdDiagnostico());
            response.put("tratamientoNombre", plan.getTratamiento().getNombreTratamiento());
            response.put("estadoPlan", plan.getEstado());
            response.put("cantidadPlaneada", plan.getCantidadPlaneada());
            response.put("cantidadRealizada", plan.getCantidadRealizada());
            response.put("cantidadPendiente", plan.getCantidadPendiente());
            
            // Datos del paciente
            if (diagnostico.getRevision() != null && 
                diagnostico.getRevision().getConsulta() != null) {
                
                var consulta = diagnostico.getRevision().getConsulta();
                var paciente = consulta.getPaciente();
                
                response.put("idPaciente", paciente.getIdPaciente());
                response.put("ci", paciente.getCi());
                
                if (paciente.getPersona() != null) {
                    var persona = paciente.getPersona();
                    response.put("nombrePaciente", 
                        persona.getNombre() + " " + persona.getApellidoPaterno() + " " + persona.getApellidoMaterno());
                    response.put("edad", persona.getEdad());
                }
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al obtener plan: " + e.getMessage()
            ));
        }
    }

    /**
     * OBTENER DIENTES PENDIENTES DEL PLAN
     * GET /seguimiento/api/plan/{idPlan}/dientes-pendientes
     */
    @GetMapping("/api/plan/{idPlan}/dientes-pendientes")
    @ResponseBody
    public ResponseEntity<?> obtenerDientesPendientes(@PathVariable Long idPlan) {
        try {
            List<DiagnosticoTratamientoDiente> dientes = diagnosticoTratamientoDienteService
                .findByDiagnosticoTratamientoId(idPlan);
            
            List<Map<String, Object>> pendientes = dientes.stream()
                .filter(d -> !"REALIZADO".equals(d.getEstado()))
                .map(d -> {
                    Map<String, Object> dienteMap = new HashMap<>();
                    dienteMap.put("idDientePlan", d.getIdDiagnosticoTratamientoDiente());
                    dienteMap.put("diente", d.getDiente());
                    dienteMap.put("estado", d.getEstado());
                    return dienteMap;
                })
                .collect(Collectors.toList());
            
            long total = dientes.size();
            long realizados = dientes.stream()
                .filter(d -> "REALIZADO".equals(d.getEstado()))
                .count();
            
            return ResponseEntity.ok(Map.of(
                "dientes", pendientes,
                "total", total,
                "realizados", realizados,
                "pendientes", pendientes.size(),
                "idPlan", idPlan
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error: " + e.getMessage()
            ));
        }
    }

    /**
     * REGISTRAR EVOLUCIÓN Y PROCEDIMIENTOS
     * POST /seguimiento/api/evolucion/registrar
     */
    @PostMapping("/api/evolucion/registrar")
    @ResponseBody
    public ResponseEntity<?> registrarEvolucion(@RequestBody Map<String, Object> datos) {
        try {
            Long idPlan = Long.valueOf(datos.get("idPlan").toString());
            
            // 1. Obtener el plan de tratamiento
            DiagnosticoTratamiento plan = diagnosticoTratamientoService.obtenerPorId(idPlan)
                .orElseThrow(() -> new RuntimeException("Plan de tratamiento no encontrado"));
            
            EvolucionClinica evolucionInicial = plan.getEvolucionClinica();
            Diagnostico diagnostico = evolucionInicial.getDiagnostico();
            
            // 2. Crear la evolución clínica (CONTROL)
            EvolucionClinica evolucion = new EvolucionClinica();
            evolucion.setDiagnostico(diagnostico);
            evolucion.setTipoRegistro("CONTROL");
            evolucion.setSubjetivo(datos.get("subjetivo").toString());
            evolucion.setObjetivo(datos.get("objetivo").toString());
            evolucion.setAnalisis(datos.get("analisis").toString());
            evolucion.setPlanAccion(datos.get("planAccion").toString());
            evolucion.setFechaHora(LocalDateTime.now());
            
            // Signos vitales
            if (datos.containsKey("presionArterial")) {
                evolucion.setPresionArterial(datos.get("presionArterial").toString());
            }
            if (datos.containsKey("frecuenciaCardiaca") && datos.get("frecuenciaCardiaca") != null) {
                evolucion.setFrecuenciaCardiaca(Integer.valueOf(datos.get("frecuenciaCardiaca").toString()));
            }
            if (datos.containsKey("frecuenciaRespiratoria") && datos.get("frecuenciaRespiratoria") != null) {
                evolucion.setFrecuenciaRespiratoria(Integer.valueOf(datos.get("frecuenciaRespiratoria").toString()));
            }
            if (datos.containsKey("temperatura") && datos.get("temperatura") != null) {
                evolucion.setTemperatura(Double.valueOf(datos.get("temperatura").toString()));
            }
            if (datos.containsKey("peso") && datos.get("peso") != null) {
                evolucion.setPeso(Double.valueOf(datos.get("peso").toString()));
            }
            
            EvolucionClinica evolucionGuardada = evolucionService.guardar(evolucion);
            
            // 3. Registrar los procedimientos realizados
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> procedimientos = (List<Map<String, Object>>) datos.get("procedimientos");
            
            List<DetalleEvolucionClinica> detallesGuardados = new ArrayList<>();
            
            if (procedimientos != null && !procedimientos.isEmpty()) {
                for (Map<String, Object> proc : procedimientos) {
                    Long idDientePlan = Long.valueOf(proc.get("idDientePlan").toString());
                    String procedimientoRealizado = proc.get("procedimientoRealizado").toString();
                    String observaciones = proc.get("observaciones") != null ? proc.get("observaciones").toString() : "";
                    String estado = proc.get("estado").toString();
                    
                    // Obtener el diente del plan
                    DiagnosticoTratamientoDiente dientePlan = diagnosticoTratamientoDienteService
                        .obtenerPorId(idDientePlan)
                        .orElseThrow(() -> new RuntimeException("Diente del plan no encontrado"));
                    
                    // Crear el detalle
                    DetalleEvolucionClinica detalle = new DetalleEvolucionClinica();
                    detalle.setEvolucionClinica(evolucionGuardada);
                    detalle.setDiagnosticoTratamientoDiente(dientePlan);
                    detalle.setProcedimientoRealizado(procedimientoRealizado);
                    detalle.setObservaciones(observaciones);
                    detalle.setEstDetEvoClin(estado);
                    detalle.setNumeroSesion(calcularNumeroSesion(evolucionGuardada, dientePlan));
                    
                    DetalleEvolucionClinica guardado = detalleEvolucionService.guardar(detalle);
                    detallesGuardados.add(guardado);
                    
                    // Actualizar estado del diente en el plan
                    if ("REALIZADO".equals(estado)) {
                        dientePlan.setEstado("PENDIENTE_APROBACION");
                        diagnosticoTratamientoDienteService.guardar(dientePlan);
                    } else if ("PARCIAL".equals(estado)) {
                        dientePlan.setEstado("EN_PROCESO");
                        diagnosticoTratamientoDienteService.guardar(dientePlan);
                    }
                }
            }
            
            // 4. Calcular progreso del plan
            List<DiagnosticoTratamientoDiente> todosDientes = diagnosticoTratamientoDienteService
                .findByDiagnosticoTratamientoId(idPlan);
            
            long totalDientes = todosDientes.size();
            long realizados = todosDientes.stream()
                .filter(d -> "REALIZADO".equals(d.getEstado()))
                .count();
            long pendientes = totalDientes - realizados;
            
            // Actualizar el plan
            plan.setCantidadRealizada((int) realizados);
            plan.setCantidadPendiente((int) pendientes);
            
            if (pendientes == 0) {
                plan.setEstado("COMPLETADO");
            } else {
                plan.setEstado("EN_PROCESO");
            }
            
            diagnosticoTratamientoService.guardar(plan);
            
            Map<String, Object> response = new HashMap<>();
            response.put("idEvolucionClinica", evolucionGuardada.getIdEvolucionClinica());
            response.put("mensaje", "Evolución registrada correctamente");
            response.put("detallesGuardados", detallesGuardados.size());
            response.put("totalDientes", totalDientes);
            response.put("realizados", realizados);
            response.put("pendientes", pendientes);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al registrar: " + e.getMessage()
            ));
        }
    }
    
    private Integer calcularNumeroSesion(EvolucionClinica evolucion, DiagnosticoTratamientoDiente diente) {
        // Contar cuántas evoluciones anteriores tiene este diente
        List<DetalleEvolucionClinica> detallesAnteriores = detalleEvolucionService
            .findByDientePlanId(diente.getIdDiagnosticoTratamientoDiente());
        
        // Las evoluciones anteriores + 1 = número de sesión actual
        return detallesAnteriores.size() + 1;
    }
}