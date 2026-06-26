package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.Diagnostico;
import proyect_final.clinica.Model.Dao.DiagnosticoRepository;
import proyect_final.clinica.Service.DiagnosticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnostico")
@CrossOrigin(origins = "*")
public class DiagnosticoController {

    @Autowired
    private DiagnosticoService diagnosticoService;
    
    @Autowired
    private DiagnosticoRepository diagnosticoRepository;

    // ===== OBTENER DIAGNÓSTICO POR ID CON DATOS DEL PACIENTE =====
    @GetMapping("/{idDiagnostico}")
    public ResponseEntity<?> obtenerDiagnosticoCompleto(@PathVariable Long idDiagnostico) {
        try {
            Diagnostico diagnostico = diagnosticoRepository.findById(idDiagnostico)
                    .orElseThrow(() -> new RuntimeException("Diagnóstico no encontrado"));
            
            // Construir respuesta con datos del paciente
            Map<String, Object> response = new HashMap<>();
            response.put("idDiagnostico", diagnostico.getIdDiagnostico());
            response.put("descripcion", diagnostico.getDescripcion());
            
            // Datos del paciente (desde la consulta -> paciente)
            if (diagnostico.getRevision() != null && 
                diagnostico.getRevision().getConsulta() != null) {
                
                var consulta = diagnostico.getRevision().getConsulta();
                var paciente = consulta.getPaciente();
                var persona = paciente.getPersona();
                
                response.put("nombrePaciente", 
                    persona.getNombre() + " " + persona.getApellidoPaterno() + " " + persona.getApellidoMaterno());
                response.put("edad", persona.getEdad());
                response.put("idPaciente", paciente.getIdPaciente());
                response.put("ci", paciente.getCi());
                response.put("historialClinico", paciente.getHistorialClinico());
            }
            
            // Tratamientos asociados (desde DiagnosticoTratamiento)
            // Esto depende de cómo tengas la relación Diagnostico -> DiagnosticoTratamiento
            // Si tienes la relación, puedes agregarla aquí
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    // ===== OBTENER TRATAMIENTOS ASOCIADOS A UN DIAGNÓSTICO =====
    @GetMapping("/{idDiagnostico}/tratamientos")
    public ResponseEntity<?> obtenerTratamientosDiagnostico(@PathVariable Long idDiagnostico) {
        try {

            
            return ResponseEntity.ok(Map.of("message", "Endpoint para obtener tratamientos"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}