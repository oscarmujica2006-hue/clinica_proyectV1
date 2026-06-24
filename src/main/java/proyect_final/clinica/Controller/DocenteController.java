package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.Docente;
import proyect_final.clinica.Model.Dto.DocenteDTO;
import proyect_final.clinica.Service.DocenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/docentes")
public class DocenteController {

    @Autowired
    private DocenteService docenteService;

    @GetMapping
    public ResponseEntity<List<DocenteDTO>> listarTodos() {
        List<DocenteDTO> docentes = docenteService.obtenerTodosDTO();
        return ResponseEntity.ok(docentes);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<DocenteDTO>> listarActivos() {
        List<DocenteDTO> docentes = docenteService.obtenerActivosDTO();
        return ResponseEntity.ok(docentes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocenteDTO> obtenerPorId(@PathVariable Long id) {
        return docenteService.obtenerDTOPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<DocenteDTO>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) Integer codigo) {
        
        List<Docente> docentes;
        
        // Lógica de búsqueda según parámetros
        if (codigo != null) {
            // Buscar por código
            docentes = docenteService.obtenerPorCodigoDocente(codigo)
                    .map(List::of)
                    .orElse(List.of());
        } else if (nombre != null && !nombre.isEmpty()) {
            // Buscar por nombre
            docentes = docenteService.buscarPorNombre(nombre);
        } else if (especialidad != null && !especialidad.isEmpty()) {
            // Buscar por especialidad
            docentes = docenteService.buscarPorEspecialidad(especialidad);
        } else {
            // Si no hay filtros, devolver todos
            docentes = docenteService.obtenerTodos();
        }
        
        // Convertir a DTO
        List<DocenteDTO> resultados = docentes.stream()
                .map(docenteService::convertirADTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(resultados);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody DocenteDTO docenteDTO) {
        try {
            // Validar código único
            if (docenteDTO.getCodigoDocente() != null && 
                docenteService.existePorCodigoDocente(docenteDTO.getCodigoDocente())) {
                
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ya existe un docente con el código: " + docenteDTO.getCodigoDocente());
                return ResponseEntity.badRequest().body(error);
            }
            
            // Convertir DTO a entidad y guardar
            Docente docente = docenteService.convertirAEntidad(docenteDTO);
            Docente guardado = docenteService.guardar(docente);
            DocenteDTO resultado = docenteService.convertirADTO(guardado);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear docente: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id, 
            @RequestBody DocenteDTO docenteDTO) {
        
        try {
            // Verificar si existe
            if (!docenteService.obtenerPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Validar código único (excepto para el mismo docente)
            if (docenteDTO.getCodigoDocente() != null) {
                Optional<Docente> docenteExistente = docenteService.obtenerPorCodigoDocente(docenteDTO.getCodigoDocente());
                if (docenteExistente.isPresent() && !docenteExistente.get().getIdDocente().equals(id)) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "El código ya está en uso por otro docente");
                    return ResponseEntity.badRequest().body(error);
                }
            }
            
            // Actualizar
            docenteDTO.setIdDocente(id);
            Docente docente = docenteService.convertirAEntidad(docenteDTO);
            Docente actualizado = docenteService.guardar(docente);
            DocenteDTO resultado = docenteService.convertirADTO(actualizado);
            
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (!docenteService.obtenerPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            docenteService.eliminar(id);
            
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Docente eliminado (desactivado) exitosamente");
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @GetMapping("/selectores")
    public ResponseEntity<List<Map<String, Object>>> obtenerParaSelectores() {
        List<DocenteDTO> docentes = docenteService.obtenerActivosDTO();
        List<Map<String, Object>> simplificado = docentes.stream()
            .map(d -> {
                Map<String, Object> mapa = new HashMap<>();
                mapa.put("id", d.getIdDocente());
                mapa.put("nombre", d.getNombreCompleto());
                mapa.put("especialidad", d.getEspecialidad() != null ? d.getEspecialidad() : "");
                mapa.put("codigo", d.getCodigoDocente());
                return mapa;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(simplificado);
    }
    
    @GetMapping("/especialidades")
    public ResponseEntity<List<String>> listarEspecialidades() {
        List<String> especialidades = docenteService.obtenerTodosDTO().stream()
                .map(DocenteDTO::getEspecialidad)
                .filter(e -> e != null && !e.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(especialidades);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticas() {
        List<DocenteDTO> todos = docenteService.obtenerTodosDTO();
        long activos = todos.stream().filter(DocenteDTO::getEstado).count();
        
        Map<String, Long> estadisticas = new HashMap<>();
        estadisticas.put("total", (long) todos.size());
        estadisticas.put("activos", activos);
        estadisticas.put("inactivos", todos.size() - activos);
        
        return ResponseEntity.ok(estadisticas);
    }
}