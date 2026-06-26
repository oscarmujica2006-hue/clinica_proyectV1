package proyect_final.clinica.Controller;

import proyect_final.clinica.Service.DetalleEvolucionClinicaService;
import proyect_final.clinica.Service.EvolucionClinicaService;
import proyect_final.clinica.Model.Entity.DetalleEvolucionClinica;
import proyect_final.clinica.Model.Entity.DiagnosticoTratamientoDiente;  // ✅ AGREGAR
import proyect_final.clinica.Service.DiagnosticoTratamientoDienteService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;  // ✅ AGREGAR
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

import proyect_final.clinica.Model.Entity.EvolucionClinica;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/detalle-evolucion")
public class DetalleEvolucionClinicaController {

    @Autowired
    private DetalleEvolucionClinicaService detalleEvolucionService;
    
    @Autowired
    private EvolucionClinicaService evolucionService;
    
    @Autowired
    private DiagnosticoTratamientoDienteService dientePlanService;

    // Registrar procedimientos realizados en una sesión
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarProcedimientos(@RequestBody Map<String, Object> datos) {
        try {
            Long idEvolucion = Long.valueOf(datos.get("idEvolucionClinica").toString());
            
            // Verificar que la evolución existe
            EvolucionClinica evolucion = evolucionService.obtenerPorId(idEvolucion);
            if (evolucion == null) {
                return ResponseEntity.badRequest().body("Evolución clínica no encontrada");
            }
            
            // Obtener la lista de procedimientos realizados
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> procedimientos = (List<Map<String, Object>>) datos.get("procedimientos");
            
            List<DetalleEvolucionClinica> detallesGuardados = new ArrayList<>();
            
            for (Map<String, Object> proc : procedimientos) {
                Long idDientePlan = Long.valueOf(proc.get("idDientePlan").toString());
                String procedimientoRealizado = proc.get("procedimientoRealizado").toString();
                String observaciones = proc.get("observaciones") != null ? proc.get("observaciones").toString() : "";
                String estado = proc.get("estado").toString(); // REALIZADO, PARCIAL, NO_REALIZADO
                
                // Verificar que el diente del plan existe
                Optional<DiagnosticoTratamientoDiente> dientePlanOpt = dientePlanService.obtenerPorId(idDientePlan);
                if (dientePlanOpt.isEmpty()) {
                    continue; // Saltar este diente si no existe
                }
                
                DiagnosticoTratamientoDiente dientePlan = dientePlanOpt.get();
                
                // Crear el detalle
                DetalleEvolucionClinica detalle = new DetalleEvolucionClinica();
                detalle.setEvolucionClinica(evolucion);
                detalle.setDiagnosticoTratamientoDiente(dientePlan);
                detalle.setProcedimientoRealizado(procedimientoRealizado);
                detalle.setObservaciones(observaciones);
                // ✅ CORREGIDO: Usar el nombre correcto del campo
                detalle.setEstDetEvoClin(estado);  // Antes era setEstado()
                detalle.setNumeroSesion(1); // Puedes calcularlo según el orden de las evoluciones
                
                DetalleEvolucionClinica guardado = detalleEvolucionService.guardar(detalle);
                detallesGuardados.add(guardado);
                
                // ✅ ACTUALIZAR EL ESTADO DEL DIENTE EN EL PLAN
                if ("REALIZADO".equals(estado)) {
                    dientePlan.setEstado("REALIZADO");
                    dientePlanService.guardar(dientePlan);
                } else if ("PARCIAL".equals(estado)) {
                    dientePlan.setEstado("EN_PROCESO");
                    dientePlanService.guardar(dientePlan);
                }
            }
            
            // ✅ ACTUALIZAR EL PROGRESO DEL PLAN DE TRATAMIENTO
            Long idDiagTrat = Long.valueOf(datos.get("idDiagTrat").toString());
            long totalDientes = dientePlanService.countByDiagnosticoTratamientoId(idDiagTrat);
            long realizados = dientePlanService.countByDiagnosticoTratamientoIdAndEstado(idDiagTrat, "REALIZADO");
            long pendientes = totalDientes - realizados;
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Procedimientos registrados correctamente",
                "detallesGuardados", detallesGuardados.size(),
                "totalDientes", totalDientes,
                "realizados", realizados,
                "pendientes", pendientes
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al registrar: " + e.getMessage()
            ));
        }
    }
    
    // Obtener el historial de una evolución
    @GetMapping("/evolucion/{idEvolucion}")
    public ResponseEntity<?> obtenerPorEvolucion(@PathVariable Long idEvolucion) {
        try {
            List<DetalleEvolucionClinica> detalles = detalleEvolucionService
                .findByEvolucionClinicaId(idEvolucion);
            
            List<Map<String, Object>> response = detalles.stream()
                .map(d -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idDetalle", d.getIdDetalleEvolucion());
                    map.put("diente", d.getDiagnosticoTratamientoDiente().getDiente());
                    map.put("procedimientoRealizado", d.getProcedimientoRealizado());
                    map.put("observaciones", d.getObservaciones());
                    // ✅ CORREGIDO: Usar el nombre correcto del campo
                    map.put("estado", d.getEstDetEvoClin());  // Antes era getEstado()
                    // ✅ CORREGIDO: Usar el nombre correcto del campo
                    map.put("fechaRegistro", d.getFechRegDetEvo());  // Antes era getFechReg()
                    map.put("numeroSesion", d.getNumeroSesion());
                    return map;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}