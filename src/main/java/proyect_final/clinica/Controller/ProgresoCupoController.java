package proyect_final.clinica.Controller;

import proyect_final.clinica.Service.ProgresoCupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progreso-cupos")
@CrossOrigin(origins = "*")
public class ProgresoCupoController {

    @Autowired
    private ProgresoCupoService progresoCupoService;

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<?> getProgresoEstudiante(@PathVariable Integer idEstudiante) {  // ✅ CAMBIADO: Long → Integer
        try {
            System.out.println("🔍 Buscando progreso para estudiante: " + idEstudiante);
            
            List<Map<String, Object>> progreso = progresoCupoService.obtenerProgresoEstudiante(idEstudiante);
            Map<String, Object> estadisticas = progresoCupoService.obtenerEstadisticasEstudiante(idEstudiante);
            
            return ResponseEntity.ok(Map.of(
                "progreso", progreso,
                "estadisticas", estadisticas
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al obtener progreso: " + e.getMessage()
            ));
        }
    }
}