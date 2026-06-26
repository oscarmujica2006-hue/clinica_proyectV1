package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Dto.TratamientoCupoDTO;
import proyect_final.clinica.Model.Entity.Cupo;
import proyect_final.clinica.Model.Entity.Materia;
import proyect_final.clinica.Model.Entity.Tratamiento;
import proyect_final.clinica.Service.CupoService;
import proyect_final.clinica.Service.MateriaService;
import proyect_final.clinica.Service.TratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cupos")
@CrossOrigin(origins = "*")
public class CuposController {

    @Autowired
    private CupoService cupoService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private TratamientoService tratamientoService;

    @GetMapping("/tratamientos-por-materia/{idMateria}")
    public ResponseEntity<List<TratamientoCupoDTO>> getTratamientosByMateria(@PathVariable Long idMateria) {
        List<TratamientoCupoDTO> list = cupoService.obtenerTratamientosConCuposPorMateria(idMateria);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<List<Cupo>> listarTodosLosCupos() {
        List<Cupo> cupos = cupoService.listarTodos();
        return ResponseEntity.ok(cupos);
    }
    @PostMapping
    public ResponseEntity<?> crearCupo(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("=== POST /api/cupos ===");
            System.out.println("Payload recibido: " + payload);
            
            // Extraer datos del payload
            Map<String, Object> materiaMap = (Map<String, Object>) payload.get("materia");
            Map<String, Object> tratamientoMap = (Map<String, Object>) payload.get("tratamiento");
            Integer cuposDisponibles = null;
            
            // Obtener cuposDisponibles (puede venir como Integer o como Number)
            Object cuposObj = payload.get("cuposDisponibles");
            if (cuposObj instanceof Integer) {
                cuposDisponibles = (Integer) cuposObj;
            } else if (cuposObj instanceof Number) {
                cuposDisponibles = ((Number) cuposObj).intValue();
            }
            
            if (materiaMap == null || tratamientoMap == null || cuposDisponibles == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Datos incompletos: materia, tratamiento y cuposDisponibles son requeridos"));
            }
            
            // Obtener IDs - IMPORTANTE: Usar el nombre correcto del campo
            Long materiaId = null;
            Long tratamientoId = null;
            
            // Para materia, puede venir como "id_materia" o "idMateria"
            if (materiaMap.containsKey("id_materia")) {
                materiaId = ((Number) materiaMap.get("id_materia")).longValue();
            } else if (materiaMap.containsKey("idMateria")) {
                materiaId = ((Number) materiaMap.get("idMateria")).longValue();
            }
            
            // Para tratamiento, puede venir como "idTratamiento" o "id_tratamiento"
            if (tratamientoMap.containsKey("idTratamiento")) {
                tratamientoId = ((Number) tratamientoMap.get("idTratamiento")).longValue();
            } else if (tratamientoMap.containsKey("id_tratamiento")) {
                tratamientoId = ((Number) tratamientoMap.get("id_tratamiento")).longValue();
            }
            
            System.out.println("Materia ID: " + materiaId);
            System.out.println("Tratamiento ID: " + tratamientoId);
            System.out.println("Cupos: " + cuposDisponibles);
            
            if (materiaId == null || tratamientoId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "IDs de materia o tratamiento no encontrados"));
            }
            
            // Buscar materia y tratamiento
            Optional<Materia> materiaOpt = materiaService.obtenerPorId(materiaId);
            Optional<Tratamiento> tratamientoOpt = tratamientoService.obtenerPorId(tratamientoId);
            
            if (materiaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Materia no encontrada con ID: " + materiaId));
            }
            if (tratamientoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tratamiento no encontrado con ID: " + tratamientoId));
            }
            
            // Crear cupo
            Cupo cupo = new Cupo();
            cupo.setMateria(materiaOpt.get());
            cupo.setTratamiento(tratamientoOpt.get());
            cupo.setCuposDisponibles(cuposDisponibles);
            
            Cupo saved = cupoService.guardar(cupo);
            return ResponseEntity.ok(saved);
            
        } catch (Exception e) {
            System.err.println("Error al crear cupo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Error al crear cupo: " + e.getMessage()));
        }
    }

    // ✅ AGREGAR MÉTODO PUT PARA ACTUALIZAR CUPOS
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCupo(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Optional<Cupo> cupoOpt = cupoService.obtenerPorId(id);
            if (cupoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Integer cuposDisponibles = null;
            Object cuposObj = payload.get("cuposDisponibles");
            if (cuposObj instanceof Integer) {
                cuposDisponibles = (Integer) cuposObj;
            } else if (cuposObj instanceof Number) {
                cuposDisponibles = ((Number) cuposObj).intValue();
            }
            
            if (cuposDisponibles == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Se requiere cuposDisponibles"));
            }
            
            Cupo cupo = cupoOpt.get();
            cupo.setCuposDisponibles(cuposDisponibles);
            
            Cupo saved = cupoService.guardar(cupo);
            return ResponseEntity.ok(saved);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/todos-con-detalles")
    public ResponseEntity<?> obtenerTodosLosCuposConDetalles() {
        try {
            List<Cupo> cupos = cupoService.listarTodos();
            
            List<Map<String, Object>> response = cupos.stream()
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idCupo", c.getIdCupo());
                    map.put("cuposDisponibles", c.getCuposDisponibles());
                    
                    // Datos de la materia
                    map.put("materiaId", c.getMateria().getId_materia());
                    map.put("materiaNombre", c.getMateria().getNombreMateria());
                    map.put("materiaCodigo", c.getMateria().getCodigoMateria());
                    
                    // Datos del tratamiento
                    map.put("tratamientoId", c.getTratamiento().getIdTratamiento());
                    map.put("tratamientoNombre", c.getTratamiento().getNombreTratamiento());
                    map.put("tratamientoDescripcion", c.getTratamiento().getDescripcionTratamiento());
                    map.put("tratamientoPrecio", c.getTratamiento().getPrecioTratamiento());
                    
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al obtener cupos: " + e.getMessage()
            ));
        }
    }








}