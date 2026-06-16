package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.Paciente;
import proyect_final.clinica.Service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publico")  // Ruta pública sin restricciones
@CrossOrigin(origins = "*")
public class PublicoPacienteController {
    
    @Autowired
    private PacienteService pacienteService;
    
    @GetMapping("/pacientes/buscar-por-ci")
    public ResponseEntity<List<Paciente>> buscarPorCiPublico(@RequestParam String ci) {
        try {
            Integer ciNumero = Integer.valueOf(ci);
            List<Paciente> pacientes = pacienteService.buscarPorCi(ciNumero);
            return ResponseEntity.ok(pacientes);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/pacientes/buscar")
    public ResponseEntity<List<Paciente>> buscarPorTerminoPublico(@RequestParam String term) {
        List<Paciente> pacientes = pacienteService.buscarPorTermino(term);
        return ResponseEntity.ok(pacientes);
    }
}