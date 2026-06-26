package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Dto.EstudiantePrestamoDTO;
import proyect_final.clinica.Model.Dto.ProgresoMateriaDTO;
import proyect_final.clinica.Model.Dto.ProgresoTratamientoDTO;
import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Service.EstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;

    // ==================== OBTENER TODOS LOS ESTUDIANTES ====================
    @GetMapping
    public ResponseEntity<List<EstudiantePrestamoDTO>> getAllEstudiantes() {
        List<Estudiante> estudiantes = estudianteService.obtenerTodos();
        List<EstudiantePrestamoDTO> dtos = estudiantes.stream()
            .map(EstudiantePrestamoDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ==================== OBTENER ESTUDIANTE POR ID ====================
    @GetMapping("/{id}")
    public ResponseEntity<EstudiantePrestamoDTO> getEstudianteById(@PathVariable Long id) {
        Optional<Estudiante> estudiante = estudianteService.obtenerPorId(id);
        return estudiante.map(e -> ResponseEntity.ok(new EstudiantePrestamoDTO(e)))
                        .orElse(ResponseEntity.notFound().build());
    }

    // ==================== BUSCAR POR CÓDIGO ====================
    @GetMapping("/buscar-por-codigo")
    public ResponseEntity<List<EstudiantePrestamoDTO>> buscarPorCodigo(@RequestParam Integer codigo) {
        List<Estudiante> estudiantes = estudianteService.buscarPorCodigoEstudiante(codigo);
        List<EstudiantePrestamoDTO> dtos = estudiantes.stream()
            .map(EstudiantePrestamoDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ==================== BUSCAR ESTUDIANTE PARA PRÉSTAMO ====================
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

    // ==================== OBTENER PROGRESO DEL ESTUDIANTE ====================
    @GetMapping("/{id}/progreso")
    public ResponseEntity<?> obtenerProgreso(@PathVariable Long id) {
        try {
            Optional<Estudiante> estudianteOpt = estudianteService.obtenerPorId(id);
            if (estudianteOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Estudiante no encontrado con ID: " + id
                ));
            }
            
            List<ProgresoMateriaDTO> progreso = estudianteService.obtenerProgresoPorEstudiante(id);
            
            if (progreso.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "mensaje", "El estudiante no tiene materias inscritas o no hay tratamientos asignados",
                    "data", progreso
                ));
            }
            
            return ResponseEntity.ok(progreso);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al obtener progreso: " + e.getMessage()
            ));
        }
    }

    // ==================== OBTENER PROGRESO CON ESTADÍSTICAS ====================
    @GetMapping("/{id}/progreso-completo")
    public ResponseEntity<?> obtenerProgresoCompleto(@PathVariable Long id) {
        try {
            Optional<Estudiante> estudianteOpt = estudianteService.obtenerPorId(id);
            if (estudianteOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Estudiante no encontrado con ID: " + id
                ));
            }
            
            List<ProgresoMateriaDTO> progreso = estudianteService.obtenerProgresoPorEstudiante(id);
            
            // Calcular estadísticas generales
            int totalTratamientos = 0;
            int totalRequeridos = 0;
            int totalRealizados = 0;
            
            for (ProgresoMateriaDTO materia : progreso) {
                // ✅ CORREGIDO: Usar ProgresoTratamientoDTO en lugar de ProgresoMateriaDTO.TratamientoProgresoDTO
                for (ProgresoTratamientoDTO trat : materia.getTratamientos()) {
                    totalTratamientos++;
                    totalRequeridos += trat.getRequerido() != null ? trat.getRequerido() : 0;
                    totalRealizados += trat.getRealizado() != null ? trat.getRealizado() : 0;
                }
            }
            
            double porcentajeGeneral = totalRequeridos > 0 
                ? (totalRealizados * 100.0 / totalRequeridos) 
                : 0;
            
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("materias", progreso);
            response.put("estadisticas", Map.of(
                "totalTratamientos", totalTratamientos,
                "totalRequeridos", totalRequeridos,
                "totalRealizados", totalRealizados,
                "porcentajeGeneral", Math.round(porcentajeGeneral * 10.0) / 10.0
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al obtener progreso completo: " + e.getMessage()
            ));
        }
    }
}