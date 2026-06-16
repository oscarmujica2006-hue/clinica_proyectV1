package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Dto.PrestamoActualDTO;
import proyect_final.clinica.Model.Entity.PrestamoActual;
import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Model.Entity.Archivo;
import proyect_final.clinica.Service.PrestamoActualService;
import proyect_final.clinica.Service.EstudianteService;
import proyect_final.clinica.Service.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prestamos-actuales")
@CrossOrigin(origins = "*")
public class PrestamoActualController {

    @Autowired
    private PrestamoActualService prestamoActualService;
    
    @Autowired
    private EstudianteService estudianteService;
    
    @Autowired
    private ArchivoService archivoService;

    // 1. Obtener todos los préstamos (devuelve DTO)
    @GetMapping
    public ResponseEntity<List<PrestamoActualDTO>> obtenerTodos() {
        List<PrestamoActual> prestamos = prestamoActualService.obtenerTodos();
        List<PrestamoActualDTO> dtos = prestamos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // 2. Obtener préstamo por ID (devuelve DTO)
    @GetMapping("/{id}")
    public ResponseEntity<PrestamoActualDTO> obtenerPorId(@PathVariable Long id) {
        Optional<PrestamoActual> prestamo = prestamoActualService.obtenerPorId(id);
        return prestamo.map(p -> ResponseEntity.ok(convertirADTO(p)))
                      .orElse(ResponseEntity.notFound().build());
    }

    // 3. Crear nuevo préstamo (recibe DTO simplificado)
    @PostMapping
    public ResponseEntity<PrestamoActualDTO> crearPrestamo(@RequestBody PrestamoActualDTO prestamoDTO) {
        try {
            // Crear entidad desde el DTO
            PrestamoActual prestamo = new PrestamoActual();
            prestamo.setIdArchivo(prestamoDTO.getIdArchivo());
            prestamo.setIdEstudiante(prestamoDTO.getIdEstudiante());
            prestamo.setFechaPrestamo(LocalDate.now());
            prestamo.setFechaLimitePrestamo(prestamoDTO.getFechaLimitePrestamo());
            prestamo.setTipoPrestamo(prestamoDTO.getTipoPrestamo());
            prestamo.setEncargadoPrestamo(prestamoDTO.getEncargadoPrestamo());
            prestamo.setMotivoPrestamo(prestamoDTO.getMotivoPrestamo());
            prestamo.setEstadoPrestamo("ACTIVO");
            
            PrestamoActual nuevoPrestamo = prestamoActualService.registrarPrestamo(prestamo);
            return new ResponseEntity<>(convertirADTO(nuevoPrestamo), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 4. Actualizar préstamo existente
    @PutMapping("/{id}")
    public ResponseEntity<PrestamoActualDTO> actualizarPrestamo(@PathVariable Long id, 
                                                           @RequestBody PrestamoActualDTO prestamoDTO) {
        try {
            PrestamoActual prestamo = prestamoActualService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
            
            prestamo.setFechaLimitePrestamo(prestamoDTO.getFechaLimitePrestamo());
            prestamo.setTipoPrestamo(prestamoDTO.getTipoPrestamo());
            prestamo.setEncargadoPrestamo(prestamoDTO.getEncargadoPrestamo());
            prestamo.setMotivoPrestamo(prestamoDTO.getMotivoPrestamo());
            prestamo.setEstadoPrestamo(prestamoDTO.getEstadoPrestamo());
            
            PrestamoActual prestamoActualizado = prestamoActualService.actualizar(id, prestamo);
            return ResponseEntity.ok(convertirADTO(prestamoActualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. Eliminar préstamo
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> eliminarPrestamo(@PathVariable Long id) {
        try {
            prestamoActualService.eliminar(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 6. Buscar préstamos por ID de estudiante (devuelve DTO)
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<PrestamoActualDTO>> buscarPorIdEstudiante(@PathVariable Long idEstudiante) {
        try {
            List<PrestamoActual> prestamos = prestamoActualService.buscarPorIdEstudiante(idEstudiante);
            if (prestamos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            List<PrestamoActualDTO> dtos = prestamos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 7. Buscar préstamos por ID de archivo (devuelve DTO)
    @GetMapping("/archivo/{idArchivo}")
    public ResponseEntity<List<PrestamoActualDTO>> buscarPorIdArchivo(@PathVariable Long idArchivo) {
        try {
            List<PrestamoActual> prestamos = prestamoActualService.buscarPorIdArchivo(idArchivo);
            if (prestamos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            List<PrestamoActualDTO> dtos = prestamos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 8. Registrar devolución (devuelve DTO)
    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> registrarDevolucion(@PathVariable Long id) {
        try {
            PrestamoActual actualizado = prestamoActualService.devolverArchivo(id, LocalDate.now());
            return ResponseEntity.ok(convertirADTO(actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 9. Desbloquear estudiante
    @PostMapping("/desbloquear/{idEstudiante}")
    public ResponseEntity<?> desbloquearEstudiante(@PathVariable Long idEstudiante) {
        try {
            prestamoActualService.desbloquearEstudiante(idEstudiante);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    private PrestamoActualDTO convertirADTO(PrestamoActual prestamo) {
        PrestamoActualDTO dto = new PrestamoActualDTO(prestamo);
        
        // Completar datos del estudiante
        Optional<Estudiante> estudianteOpt = estudianteService.obtenerPorId(prestamo.getIdEstudiante());
        if (estudianteOpt.isPresent()) {
            Estudiante estudiante = estudianteOpt.get();
            dto.setCodigoEstudiante(estudiante.getCodigoEstudiante());
            
            if (estudiante.getUsuario() != null) {
                dto.setEmailEstudiante(estudiante.getUsuario().getEmail());
                
                if (estudiante.getUsuario().getPersona() != null) {
                    var persona = estudiante.getUsuario().getPersona();
                    String nombreCompleto = (persona.getNombre() != null ? persona.getNombre() : "") + " " + 
                                        (persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "") + " " + 
                                        (persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : "");
                    dto.setNombreEstudiante(nombreCompleto.trim());
                }
            }
        }
        
        // ✅ CORREGIDO: Usar getArchivoById que existe en ArchivoService
        Optional<Archivo> archivoOpt = archivoService.getArchivoById(prestamo.getIdArchivo());
        if (archivoOpt.isPresent()) {
            dto.setCodigoArchivo(archivoOpt.get().getCodigoArchivo());
        } else {
            dto.setCodigoArchivo("No disponible");
        }
        
        return dto;
}
}