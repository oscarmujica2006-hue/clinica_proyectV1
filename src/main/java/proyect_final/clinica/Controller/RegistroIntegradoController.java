package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Dto.RegistroEstudianteDTO;
import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Model.Entity.Persona;
import proyect_final.clinica.Model.Entity.Rote;
import proyect_final.clinica.Model.Entity.Usuario;
import proyect_final.clinica.Service.EstudianteService;
import proyect_final.clinica.Service.PersonaService;
import proyect_final.clinica.Service.RoteService;
import proyect_final.clinica.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registro-integrado")
@CrossOrigin(origins = "*")
public class RegistroIntegradoController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EstudianteService estudianteService;
    
    @Autowired
    private RoteService roteService;

    @PostMapping("/estudiante")
    public ResponseEntity<?> registrarEstudianteCompleto(@Valid @RequestBody RegistroEstudianteDTO dto) {
        try {
            // ========== 1. VERIFICACIONES PREVIAS ==========
            if (usuarioService.existePorNombreUsuario(dto.getNombreUsuario())) {
                return ResponseEntity.badRequest().body(
                    crearRespuestaError("El nombre de usuario '" + dto.getNombreUsuario() + "' ya está registrado")
                );
            }
            
     
            
            if (estudianteService.existePorCodigoEstudiante(dto.getCodigoEstudiante())) {
                return ResponseEntity.badRequest().body(
                    crearRespuestaError("El código de estudiante " + dto.getCodigoEstudiante() + " ya está registrado")
                );
            }

            // ========== 2. CREAR Y GUARDAR PERSONA ==========
            Persona persona = new Persona();
            persona.setNombre(dto.getNombre());
            persona.setApellidoPaterno(dto.getApellidoPaterno());
            persona.setApellidoMaterno(dto.getApellidoMaterno());
            persona.setEdad(dto.getEdad());
            
            if (dto.getSexo() != null && !dto.getSexo().isEmpty()) {
                persona.setSexo(dto.getSexo().charAt(0));
            }
            
            Persona personaGuardada = personaService.guardar(persona);

            // ========== 3. CREAR Y GUARDAR USUARIO ==========
            Usuario usuario = new Usuario();
            usuario.setNombreUsuario(dto.getNombreUsuario());
            usuario.setContraseña(dto.getContraseña());
            usuario.setEmail(dto.getEmail());
            usuario.setEstado(dto.getEstadoUsuario() != null ? dto.getEstadoUsuario() : true);
            usuario.setPersona(personaGuardada);
            
            Usuario usuarioGuardado = usuarioService.guardar(usuario);

            // ========== 4. OBTENER EL ROTE SELECCIONADO ==========
            Rote rote = null;
            if (dto.getIdRoteActual() != null && dto.getIdRoteActual() > 0) {  // ← CAMBIADO a getIdRoteActual()
                rote = roteService.obtenerPorId(dto.getIdRoteActual())  // ← CAMBIADO a getIdRoteActual()
                    .orElse(null);
            }

            // ========== 5. CREAR Y GUARDAR ESTUDIANTE ==========
            Estudiante estudiante = new Estudiante();
            estudiante.setCodigoEstudiante(dto.getCodigoEstudiante());
            estudiante.setGestion(dto.getGestion());
            estudiante.setBloqueado(dto.getBloqueado() != null ? dto.getBloqueado() : false);
            estudiante.setUsuario(usuarioGuardado);
            estudiante.setRoteActual(rote);  // ← ASIGNAR EL ROTE
            
            Estudiante estudianteGuardado = estudianteService.guardar(estudiante);

            // ========== 6. PREPARAR RESPUESTA ==========
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Estudiante registrado exitosamente");
            respuesta.put("id_estudiante", estudianteGuardado.getIdEstudiante());
            respuesta.put("codigo_estudiante", estudianteGuardado.getCodigoEstudiante());
            respuesta.put("nombre_completo", personaGuardada.getNombre() + " " + 
                                           personaGuardada.getApellidoPaterno() + " " + 
                                           personaGuardada.getApellidoMaterno());
            respuesta.put("nombre_usuario", usuarioGuardado.getNombreUsuario());
            respuesta.put("email", usuarioGuardado.getEmail());
            respuesta.put("id_persona", personaGuardada.getId_persona());
            
            if (rote != null) {
                respuesta.put("rote_asignado", rote.getNombreRote());
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                crearRespuestaError("Error al registrar estudiante: " + e.getMessage())
            );
        }
    }
    
    // Endpoint para obtener todos los rotes
    @GetMapping("/rotes")
    public ResponseEntity<List<Rote>> obtenerRotes() {
        List<Rote> rotes = roteService.obtenerTodos();
        return ResponseEntity.ok(rotes);
    }
    
    private Map<String, String> crearRespuestaError(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        error.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return error;
    }
    
    @GetMapping("/verificar-usuario/{nombreUsuario}")
    public ResponseEntity<Map<String, Object>> verificarUsuario(@PathVariable String nombreUsuario) {
        Boolean existe = usuarioService.existePorNombreUsuario(nombreUsuario);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("nombre_usuario", nombreUsuario);
        respuesta.put("disponible", !existe);
        return ResponseEntity.ok(respuesta);
    }
    
    @GetMapping("/verificar-codigo/{codigo}")
    public ResponseEntity<Map<String, Object>> verificarCodigo(@PathVariable Integer codigo) {
        Boolean existe = estudianteService.existePorCodigoEstudiante(codigo);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("codigo_estudiante", codigo);
        respuesta.put("disponible", !existe);
        return ResponseEntity.ok(respuesta);
    }
    

}