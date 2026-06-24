package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.Equipo;
import proyect_final.clinica.Service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyect_final.clinica.Model.Dto.EquipoDTO;
import java.util.*;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    // ✅ CREAR EQUIPO
    @PostMapping("/crear")
    public ResponseEntity<?> crearEquipo(@RequestBody EquipoDTO equipoDTO) {
        try {
            // El usuario viene del frontend en el DTO
            // Si no viene, usar "SISTEMA" como fallback
            String usuario = (equipoDTO.getUsuario() != null && !equipoDTO.getUsuario().isEmpty()) 
                ? equipoDTO.getUsuario() 
                : "SISTEMA";

            String resultado = equipoService.crearEquipoConFuncion(
                equipoDTO.getCodigo(),
                equipoDTO.getNombre(),
                equipoDTO.getEstado(),
                usuario  // ← Usar el usuario que viene del frontend
            );

            if (resultado.startsWith("ERROR:")) {
                return ResponseEntity.badRequest().body(resultado);
            }

            if (resultado.startsWith("OK:")) {
                String idStr = resultado.substring(3).trim();
                Map<String, Object> response = new HashMap<>();
                response.put("exito", true);
                response.put("id_equipo", Long.parseLong(idStr));
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.badRequest().body("ERROR: Respuesta inesperada");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

}