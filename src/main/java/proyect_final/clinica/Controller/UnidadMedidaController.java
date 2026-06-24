package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.UnidadMedida;
import proyect_final.clinica.Service.UnidadMedidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades-medida")
@CrossOrigin(origins = "*")
public class UnidadMedidaController {

    @Autowired
    private UnidadMedidaService unidadMedidaService;

    // Obtener todas las unidades de medida
    @GetMapping
    public ResponseEntity<List<UnidadMedida>> getAllUnidades() {
        try {
            List<UnidadMedida> unidades = unidadMedidaService.findAll();
            return ResponseEntity.ok(unidades);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Obtener unidad de medida por ID
    @GetMapping("/{id}")
    public ResponseEntity<UnidadMedida> getUnidadById(@PathVariable Long id) {
        try {
            return unidadMedidaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Crear nueva unidad de medida
    @PostMapping
    public ResponseEntity<UnidadMedida> createUnidad(@RequestBody UnidadMedida unidadMedida) {
        try {
            UnidadMedida saved = unidadMedidaService.save(unidadMedida);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Actualizar unidad de medida
    @PutMapping("/{id}")
    public ResponseEntity<UnidadMedida> updateUnidad(@PathVariable Long id, @RequestBody UnidadMedida unidadMedida) {
        try {
            unidadMedida.setIdUnidadMedida(id);
            UnidadMedida updated = unidadMedidaService.save(unidadMedida);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Eliminar unidad de medida
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnidad(@PathVariable Long id) {
        try {
            unidadMedidaService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}