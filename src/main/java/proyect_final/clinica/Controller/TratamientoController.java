package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.Tratamiento;
import proyect_final.clinica.Model.Entity.TratamientoInsumo;
import proyect_final.clinica.Service.TratamientoService;
import proyect_final.clinica.Service.TratamientoInsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;
    
    @Autowired
    private TratamientoInsumoService tratamientoInsumoService;  // ← Agregar esto

    @GetMapping
    public List<Tratamiento> listarTodos() {
        return tratamientoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tratamiento> obtenerPorId(@PathVariable Long id) {
        return tratamientoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tratamiento> crear(@RequestBody Tratamiento tratamiento) {
        Tratamiento nuevo = tratamientoService.guardar(tratamiento);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tratamiento> actualizar(@PathVariable Long id, @RequestBody Tratamiento tratamiento) {
        return tratamientoService.obtenerPorId(id)
                .map(tratamientoExistente -> {
                    tratamientoExistente.setNombreTratamiento(tratamiento.getNombreTratamiento());
                    tratamientoExistente.setDescripcionTratamiento(tratamiento.getDescripcionTratamiento());
                    tratamientoExistente.setPrecioTratamiento(tratamiento.getPrecioTratamiento());
               
                    return ResponseEntity.ok(tratamientoService.guardar(tratamientoExistente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return tratamientoService.obtenerPorId(id)
                .map(tratamiento -> {
                    tratamientoService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== NUEVOS ENDPOINTS PARA TRATAMIENTO_INSUMO ==========

    // Obtener todos los insumos asociados a un tratamiento
    @GetMapping("/{id}/insumos")
    public ResponseEntity<?> obtenerInsumosPorTratamiento(@PathVariable Long id) {
        try {
            List<TratamientoInsumo> tratamientoInsumos = tratamientoInsumoService.findByTratamientoId(id);
            
            if (tratamientoInsumos.isEmpty()) {
                return ResponseEntity.ok().body(Map.of("mensaje", "Este tratamiento no tiene insumos asociados"));
            }
            
            List<Map<String, Object>> insumos = tratamientoInsumos.stream()
                .map(ti -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idTratamientoInsumo", ti.getIdTratamientoInsumo());
                    map.put("idInsumo", ti.getInsumo().getIdInsumo());
                    map.put("nombreInsumo", ti.getInsumo().getNombreInsumo());
                    map.put("cantidadRequerida", ti.getCantidadRequerida());
                    map.put("unidadBase", ti.getInsumo().getUnidadBase());
                    map.put("concentracion", ti.getInsumo().getConcentracion());
                    return map;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(insumos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error al obtener insumos: " + e.getMessage()));
        }
    }

    // Agregar un insumo a un tratamiento
    @PostMapping("/{id}/insumos")
    public ResponseEntity<?> agregarInsumoATratamiento(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            // Verificar que el tratamiento existe
            Tratamiento tratamiento = tratamientoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado"));
            
            // Crear la relación
            TratamientoInsumo tratamientoInsumo = new TratamientoInsumo();
            tratamientoInsumo.setTratamiento(tratamiento);
            
            // Aquí necesitas obtener el insumo por ID
            // Long idInsumo = Long.valueOf(request.get("idInsumo").toString());
            // Insumo insumo = insumoService.obtenerPorId(idInsumo).orElseThrow(...);
            // tratamientoInsumo.setInsumo(insumo);
            
            Integer cantidadRequerida = Integer.valueOf(request.get("cantidadRequerida").toString());
            tratamientoInsumo.setCantidadRequerida(cantidadRequerida);
            
            TratamientoInsumo guardado = tratamientoInsumoService.guardar(tratamientoInsumo);
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Eliminar un insumo de un tratamiento
    @DeleteMapping("/{idTratamiento}/insumos/{idTratamientoInsumo}")
    public ResponseEntity<?> eliminarInsumoDeTratamiento(@PathVariable Long idTratamientoInsumo) {
        try {
            tratamientoInsumoService.eliminar(idTratamientoInsumo);
            return ResponseEntity.ok(Map.of("mensaje", "Insumo eliminado del tratamiento"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
}