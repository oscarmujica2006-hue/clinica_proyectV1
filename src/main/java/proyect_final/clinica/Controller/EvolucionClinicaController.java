package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Dto.EvolucionClinicaDTO;
import proyect_final.clinica.Model.Entity.EvolucionClinica;
import proyect_final.clinica.Service.EvolucionClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evolucion-clinica")
@CrossOrigin(origins = "*")
public class EvolucionClinicaController {

    @Autowired
    private EvolucionClinicaService evolucionService;

    // ===== GUARDAR EVOLUCIÓN =====
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarEvolucion(@RequestBody EvolucionClinicaDTO dto) {
        try {
            EvolucionClinica evolucion = evolucionService.guardarEvolucion(dto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("idEvolucionClinica", evolucion.getIdEvolucionClinica());
            response.put("mensaje", "Evolución clínica guardada exitosamente");
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error al guardar la evolución: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ===== OBTENER POR ID =====
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEvolucion(@PathVariable Long id) {
        try {
            EvolucionClinica evolucion = evolucionService.obtenerPorId(id);
            return ResponseEntity.ok(evolucion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ===== OBTENER EVOLUCIONES POR DIAGNÓSTICO =====
    @GetMapping("/diagnostico/{idDiagnostico}")
    public ResponseEntity<?> obtenerEvolucionesPorDiagnostico(@PathVariable Long idDiagnostico) {
        try {
            List<EvolucionClinica> evoluciones = evolucionService.obtenerPorDiagnostico(idDiagnostico);
            return ResponseEntity.ok(evoluciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ===== OBTENER EVOLUCIÓN INICIAL POR DIAGNÓSTICO =====
    @GetMapping("/diagnostico/{idDiagnostico}/inicial")
    public ResponseEntity<?> obtenerEvolucionInicial(@PathVariable Long idDiagnostico) {
        try {
            List<EvolucionClinica> evoluciones = evolucionService.obtenerEvolucionesIniciales(idDiagnostico);
            if (evoluciones.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "No hay evolución inicial para este diagnóstico"));
            }
            return ResponseEntity.ok(evoluciones.get(0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ===== OBTENER SESIONES DE UNA EVOLUCIÓN =====
    @GetMapping("/{idEvolucion}/sesiones")
    public ResponseEntity<?> obtenerSesiones(@PathVariable Long idEvolucion) {
        try {
            List<EvolucionClinica> sesiones = evolucionService.obtenerSesionesDeEvolucion(idEvolucion);
            return ResponseEntity.ok(sesiones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }


}