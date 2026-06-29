package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.Equipo;
import proyect_final.clinica.Service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyect_final.clinica.Model.Dto.EquipoDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    // Obtener ID de usuario de la sesión HTTP
    private Integer getIdUsuarioDeSesion(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Integer idUsuario = (Integer) session.getAttribute("idUsuario");
                if (idUsuario != null) {
                    return idUsuario;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener ID de usuario de sesión: " + e.getMessage());
        }
        return 1; // Usuario por defecto (SISTEMA)
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearEquipo(@RequestBody EquipoDTO equipoDTO, HttpServletRequest request) {
        try {
            Integer usuarioId = getIdUsuarioDeSesion(request); 
            System.out.println("ID de usuario autenticado (sesión): " + usuarioId);

            String resultado = equipoService.crearEquipoConFuncion(
                equipoDTO.getCodigo(),
                equipoDTO.getNombre(),
                equipoDTO.getEstado(),
                usuarioId 
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

    @GetMapping("/disponibles")
    public ResponseEntity<?> obtenerEquiposDisponibles() {
        try {
            List<Equipo> equipos = equipoService.obtenerTodos();
            
            // Filtrar solo equipos ACTIVOS
            List<Map<String, Object>> disponibles = equipos.stream()
                .filter(e -> "ACTIVO".equals(e.getEstadoEquipo()))
                .map(e -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("idEquipo", e.getIdEquipo());
                    dto.put("nombreEquipo", e.getNombreEquipo());
                    dto.put("codigoEquipo", e.getCodigoEquipo() != null ? e.getCodigoEquipo() : "");
                    dto.put("estado", e.getEstadoEquipo() != null ? e.getEstadoEquipo() : "ACTIVO");
                    return dto;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(disponibles);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== OBTENER TODOS LOS EQUIPOS ====================
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<Equipo> equipos = equipoService.obtenerTodos();
            return ResponseEntity.ok(equipos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== OBTENER EQUIPO POR ID ====================
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Optional<Equipo> equipo = equipoService.obtenerPorId(id);
            if (equipo.isPresent()) {
                return ResponseEntity.ok(equipo.get());
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Equipo no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ACTUALIZAR EQUIPO ====================
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEquipo(@PathVariable Long id, 
                                               @RequestBody EquipoDTO equipoDTO,
                                               HttpServletRequest request) {
        try {
            Integer usuarioId = getIdUsuarioDeSesion(request); 
            System.out.println("ID de usuario para actualización: " + usuarioId);
            
            Optional<Equipo> equipoOpt = equipoService.obtenerPorId(id);
            if (!equipoOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Equipo no encontrado"));
            }
            
            Equipo equipo = equipoOpt.get();
            equipo.setCodigoEquipo(equipoDTO.getCodigo());
            equipo.setNombreEquipo(equipoDTO.getNombre());
            equipo.setEstadoEquipo(equipoDTO.getEstado());
            equipo.setUsuModEqu(usuarioId); 
            
            equipoService.guardar(equipo);
            
            return ResponseEntity.ok(Map.of(
                "exito", true,
                "mensaje", "Equipo actualizado correctamente",
                "id_equipo", equipo.getIdEquipo()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ELIMINAR EQUIPO ====================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEquipo(@PathVariable Long id) {
        try {
            equipoService.eliminar(id);
            return ResponseEntity.ok(Map.of("exito", true, "mensaje", "Equipo eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}