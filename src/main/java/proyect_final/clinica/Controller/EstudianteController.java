package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Dto.EstudiantePrestamoDTO;
// import proyect_final.clinica.Model.Dto.ProgresoMateriaDTO;
import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Service.EstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;

    @GetMapping
    public ResponseEntity<List<EstudiantePrestamoDTO>> getAllEstudiantes() {
        List<Estudiante> estudiantes = estudianteService.obtenerTodos();
        // Convertir a DTO para evitar ciclos
        List<EstudiantePrestamoDTO> dtos = estudiantes.stream()
            .map(EstudiantePrestamoDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudiantePrestamoDTO> getEstudianteById(@PathVariable Long id) {
        Optional<Estudiante> estudiante = estudianteService.obtenerPorId(id);
        return estudiante.map(e -> ResponseEntity.ok(new EstudiantePrestamoDTO(e)))
                        .orElse(ResponseEntity.notFound().build());
    }

    // ✅ CORREGIDO: Ahora devuelve DTO en lugar de entidad directa
    @GetMapping("/buscar-por-codigo")
    public ResponseEntity<List<EstudiantePrestamoDTO>> buscarPorCodigo(@RequestParam Integer codigo) {
        List<Estudiante> estudiantes = estudianteService.buscarPorCodigoEstudiante(codigo);
        
        // Convertir a DTO para evitar ciclos de anidación
        List<EstudiantePrestamoDTO> dtos = estudiantes.stream()
            .map(EstudiantePrestamoDTO::new)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(dtos);
    }

    // ✅ ENDPOINT para buscar estudiante y devolver DTO (mantener)
    @GetMapping("/buscar-estudiante/{codigo}")
    public ResponseEntity<EstudiantePrestamoDTO> buscarEstudianteParaPrestamo(@PathVariable Integer codigo) {
        List<Estudiante> estudiantes = estudianteService.buscarPorCodigoEstudiante(codigo);
        
        if (estudiantes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Estudiante estudiante = estudiantes.get(0);
        EstudiantePrestamoDTO dto = new EstudiantePrestamoDTO(estudiante);
        
        return ResponseEntity.ok(dto);
    }

    // @GetMapping("/{id}/progreso")
    // public ResponseEntity<List<ProgresoMateriaDTO>> obtenerProgreso(@PathVariable Long id) {
    //     List<ProgresoMateriaDTO> progreso = estudianteService.obtenerProgresoPorEstudiante(id);
    //     return ResponseEntity.ok(progreso);
    // }
}