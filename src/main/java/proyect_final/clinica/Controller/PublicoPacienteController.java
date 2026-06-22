package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.Paciente;
import proyect_final.clinica.Model.Dto.PacienteBusquedaDTO;
import proyect_final.clinica.Service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/publico")  // Ruta pública sin restricciones
@CrossOrigin(origins = "*")
public class PublicoPacienteController {
    
    @Autowired
    private PacienteService pacienteService;
    
    @GetMapping("/pacientes/buscar-por-ci")
    public ResponseEntity<?> buscarPorCiPublico(@RequestParam String ci) {
        try {
            Integer ciNumero = Integer.valueOf(ci);
            List<Paciente> pacientes = pacienteService.buscarPorCi(ciNumero);
            
            // ✅ Convertir a DTO para evitar recursión
            List<PacienteBusquedaDTO> dtos = pacientes.stream()
                .map(PacienteBusquedaDTO::new)
                .collect(Collectors.toList());
            
            // ✅ Si hay un solo paciente, devolverlo como objeto (no array)
            if (dtos.size() == 1) {
                return ResponseEntity.ok(dtos.get(0));
            }
            
            return ResponseEntity.ok(dtos);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/pacientes/buscar")
    public ResponseEntity<List<PacienteBusquedaDTO>> buscarPorTerminoPublico(@RequestParam String term) {
        List<Paciente> pacientes = pacienteService.buscarPorTermino(term);
        
        // ✅ Convertir a DTO
        List<PacienteBusquedaDTO> dtos = pacientes.stream()
            .map(PacienteBusquedaDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
}