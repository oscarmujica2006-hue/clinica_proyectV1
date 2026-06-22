package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Model.Dao.EstudianteRepository;
import proyect_final.clinica.Model.Dto.PacienteBusquedaDTO;
import proyect_final.clinica.Model.Entity.Paciente;
import proyect_final.clinica.Service.PacienteService;
import proyect_final.clinica.Service.PrestamoActualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {
    
    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private PrestamoActualService prestamoService;
    
    @Autowired
    private HttpSession session;

    @Autowired
    private EstudianteRepository estudianteRepository;

    // ===== CLASE PARA RESPUESTAS DE ERROR =====
    static class ErrorResponse {
        private String mensaje;
        private Object datos;
        
        public ErrorResponse(String mensaje) {
            this.mensaje = mensaje;
        }
        
        public ErrorResponse(String mensaje, Object datos) {
            this.mensaje = mensaje;
            this.datos = datos;
        }
        
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public Object getDatos() { return datos; }
        public void setDatos(Object datos) { this.datos = datos; }
    }
    
    // ===== ENDPOINTS PÚBLICOS (para ARCHIVO/DOCTORES) =====
    
    @GetMapping("/public/buscar-por-ci")
    public ResponseEntity<?> buscarPorCiPublico(@RequestParam String ci) {
        try {
            Integer ciNumero = Integer.valueOf(ci);
            List<Paciente> pacientes = pacienteService.buscarPorCi(ciNumero);
            
            // ✅ Convertir a DTO
            List<PacienteBusquedaDTO> dtos = pacientes.stream()
                .map(PacienteBusquedaDTO::new)
                .collect(Collectors.toList());
            
            if (dtos.size() == 1) {
                return ResponseEntity.ok(dtos.get(0));
            }
            
            return ResponseEntity.ok(dtos);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("CI inválido"));
        }
    }
    
    @GetMapping("/public/buscar")
    public ResponseEntity<List<PacienteBusquedaDTO>> buscarPorTerminoPublico(@RequestParam String term) {
        List<Paciente> pacientes = pacienteService.buscarPorTermino(term);
        
        // ✅ Convertir a DTO
        List<PacienteBusquedaDTO> dtos = pacientes.stream()
            .map(PacienteBusquedaDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/public")
    public ResponseEntity<List<PacienteBusquedaDTO>> obtenerTodosPublico() {
        List<Paciente> pacientes = pacienteService.obtenerTodos();
        
        // ✅ Convertir a DTO
        List<PacienteBusquedaDTO> dtos = pacientes.stream()
            .map(PacienteBusquedaDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/public/{id}")
    public ResponseEntity<PacienteBusquedaDTO> obtenerPorIdPublico(@PathVariable Long id) {
        Optional<Paciente> pacienteOpt = pacienteService.obtenerPorId(id);
        
        if (pacienteOpt.isPresent()) {
            PacienteBusquedaDTO dto = new PacienteBusquedaDTO(pacienteOpt.get());
            return ResponseEntity.ok(dto);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    // ===== ENDPOINTS PARA ESTUDIANTES (CON VALIDACIÓN) =====
    
    @GetMapping("/estudiante/paciente-prestado")
    public ResponseEntity<?> obtenerPacientePrestado() {
        Long idEstudiante = (Long) session.getAttribute("idEstudiante");
        
        if (idEstudiante == null) {
            return ResponseEntity.status(401).body(new ErrorResponse("No ha iniciado sesión como estudiante"));
        }
        
        Map<String, Object> info = prestamoService.obtenerInfoPacientePrestado(idEstudiante);
        return ResponseEntity.ok(info);
    }
    
    @GetMapping("/estudiante/buscar-por-ci")
    public ResponseEntity<?> buscarPorCiEstudiante(@RequestParam String ci) {
        try {
            Long idEstudiante = (Long) session.getAttribute("idEstudiante");
            
            if (idEstudiante == null) {
                return ResponseEntity.status(401).body(new ErrorResponse("Debe iniciar sesión como estudiante"));
            }
            
            Integer ciNumero = Integer.valueOf(ci);
            List<Paciente> pacientes = pacienteService.buscarPorCi(ciNumero);
            
            if (pacientes.isEmpty()) {
                return ResponseEntity.ok(List.of());
            }
            
            Paciente paciente = pacientes.get(0);
            
            boolean puedeConsultar = prestamoService.puedeRealizarConsulta(
                idEstudiante, paciente.getIdPaciente());
            
            if (!puedeConsultar) {
                Map<String, Object> infoPrestado = prestamoService.obtenerInfoPacientePrestado(idEstudiante);
                
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("mensaje", "No tienes permiso para consultar este paciente");
                errorData.put("pacienteBuscado", 
                    (paciente.getPersona() != null ? 
                        paciente.getPersona().getNombre() + " " + 
                        paciente.getPersona().getApellidoPaterno() : "Desconocido"));
                errorData.put("pacientePermitido", infoPrestado);
                
                return ResponseEntity.status(403).body(errorData);
            }
            
            // ✅ Convertir a DTO
            PacienteBusquedaDTO dto = new PacienteBusquedaDTO(paciente);
            return ResponseEntity.ok(dto);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("CI inválido"));
        }
    }
    
    @GetMapping("/estudiante/buscar")
    public ResponseEntity<?> buscarPorTerminoEstudiante(@RequestParam String term) {
        try {
            Long idEstudiante = (Long) session.getAttribute("idEstudiante");
            
            if (idEstudiante == null) {
                return ResponseEntity.status(401).body(new ErrorResponse("Debe iniciar sesión como estudiante"));
            }
            
            Map<String, Object> infoPrestado = prestamoService.obtenerInfoPacientePrestado(idEstudiante);
            
            if (infoPrestado.isEmpty() || !(boolean) infoPrestado.getOrDefault("tienePrestamo", false)) {
                return ResponseEntity.status(403).body(new ErrorResponse(
                    "No tienes ningún préstamo activo"));
            }
            
            Long idPacientePrestado = (Long) infoPrestado.get("idPaciente");
            List<Paciente> todosLosPacientes = pacienteService.buscarPorTermino(term);
            
            List<Paciente> pacientesFiltrados = todosLosPacientes.stream()
                .filter(p -> p.getIdPaciente().equals(idPacientePrestado))
                .collect(Collectors.toList());
            
            if (pacientesFiltrados.isEmpty()) {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("mensaje", "No se encontró al paciente que tienes prestado con ese término de búsqueda");
                errorData.put("pacientePermitido", infoPrestado);
                errorData.put("terminoBuscado", term);
                
                return ResponseEntity.status(403).body(errorData);
            }
            
            // ✅ Convertir a DTO
            List<PacienteBusquedaDTO> dtos = pacientesFiltrados.stream()
                .map(PacienteBusquedaDTO::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Error en la búsqueda: " + e.getMessage()));
        }
    }
    
    @GetMapping("/estudiante/verificar-permiso")
    public ResponseEntity<?> verificarPermisoEstudiante(@RequestParam Long idPaciente) {
        Long idEstudiante = (Long) session.getAttribute("idEstudiante");
        
        if (idEstudiante == null) {
            return ResponseEntity.status(401).body(new ErrorResponse("No ha iniciado sesión como estudiante"));
        }
        
        boolean puede = prestamoService.puedeRealizarConsulta(idEstudiante, idPaciente);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("puedeConsultar", puede);
        
        if (!puede) {
            Map<String, Object> infoPrestado = prestamoService.obtenerInfoPacientePrestado(idEstudiante);
            respuesta.put("pacientePermitido", infoPrestado);
            respuesta.put("mensaje", "No tienes permiso para consultar este paciente");
        }
        
        return ResponseEntity.ok(respuesta);
    }
    
    // ===== CRUD BÁSICO (PROTEGIDO) =====
    
    @PostMapping
    public ResponseEntity<Paciente> crearPaciente(@RequestBody Paciente paciente) {
        try {
            Paciente nuevoPaciente = pacienteService.guardar(paciente);
            return new ResponseEntity<>(nuevoPaciente, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizarPaciente(@PathVariable Long id, 
                                                      @RequestBody Paciente paciente) {
        try {
            Paciente pacienteActualizado = pacienteService.actualizar(id, paciente);
            return ResponseEntity.ok(pacienteActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> eliminarPaciente(@PathVariable Long id) {
        try {
            pacienteService.eliminar(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/debug/verificar-sesion")
    public ResponseEntity<?> verificarSesionEstudiante() {
        Long idEstudiante = (Long) session.getAttribute("idEstudiante");
        Integer codigoEstudiante = (Integer) session.getAttribute("codigoEstudiante");
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("idEstudianteEnSesion", idEstudiante);
        respuesta.put("codigoEstudianteEnSesion", codigoEstudiante);
        respuesta.put("tieneSesion", idEstudiante != null);
        
        if (idEstudiante != null) {
            try {
                Optional<Estudiante> estudiante = estudianteRepository.findById(idEstudiante);
                respuesta.put("existeEnBD", estudiante.isPresent());
                if (estudiante.isPresent()) {
                    respuesta.put("codigoEstudianteBD", estudiante.get().getCodigoEstudiante());
                    respuesta.put("bloqueado", estudiante.get().getBloqueado());
                }
            } catch (Exception e) {
                respuesta.put("error", e.getMessage());
            }
        }
        
        return ResponseEntity.ok(respuesta);
    }
}