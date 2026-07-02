package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Dto.RegistroEncargadoDTO;
import proyect_final.clinica.Model.Dto.EncargadoListaDTO;
import proyect_final.clinica.Model.Entity.*;
import proyect_final.clinica.Service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registro-encargado")
@CrossOrigin(origins = "*")
public class RegistroEncargadoController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EncargadoInsumoService encargadoInsumoService;
    
    @Autowired
    private EncargadoRadiografiaService encargadoRadiografiaService;
    
    @Autowired
    private RecepcionService recepcionService;

    // ============================================================
    // VERIFICACIONES
    // ============================================================
    
    @GetMapping("/verificar-usuario/{nombreUsuario}")
    public ResponseEntity<Map<String, Object>> verificarUsuario(@PathVariable String nombreUsuario) {
        Map<String, Object> respuesta = new HashMap<>();
        try {
            boolean existe = usuarioService.existePorNombreUsuario(nombreUsuario);
            respuesta.put("nombre_usuario", nombreUsuario);
            respuesta.put("disponible", !existe);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            respuesta.put("error", "Error al verificar usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    // ============================================================
    // REGISTRO PRINCIPAL
    // ============================================================
    
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarEncargado(@Valid @RequestBody RegistroEncargadoDTO dto) {
        try {
            // 1. Verificar si el usuario ya existe
            if (usuarioService.existePorNombreUsuario(dto.getNombreUsuario())) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "El nombre de usuario ya está registrado")
                );
            }

            // 2. Verificar códigos específicos según el rol
            if ("RADIOGRAFIA".equals(dto.getRol())) {
                if (dto.getCodigoRadiografia() == null) {
                    return ResponseEntity.badRequest().body(
                        Map.of("error", "El código de radiografía es obligatorio")
                    );
                }
                if (encargadoRadiografiaService.existePorCodigo(dto.getCodigoRadiografia())) {
                    return ResponseEntity.badRequest().body(
                        Map.of("error", "El código de radiografía ya está registrado")
                    );
                }
            }
            
            if ("RECEPCION".equals(dto.getRol())) {
                if (dto.getCodigoRecepcion() == null) {
                    return ResponseEntity.badRequest().body(
                        Map.of("error", "El código de recepción es obligatorio")
                    );
                }
                if (recepcionService.existePorCodigo(dto.getCodigoRecepcion())) {
                    return ResponseEntity.badRequest().body(
                        Map.of("error", "El código de recepción ya está registrado")
                    );
                }
            }
            
            if ("INSUMO".equals(dto.getRol())) {
                if (dto.getAreaResponsabilidad() == null || dto.getCodigoAlmacen() == null) {
                    return ResponseEntity.badRequest().body(
                        Map.of("error", "Área de responsabilidad y código de almacén son obligatorios")
                    );
                }

            }

            // 3. Crear Persona
            Persona persona = new Persona();
            persona.setNombre(dto.getNombre());
            persona.setApellidoPaterno(dto.getApellidoPaterno());
            persona.setApellidoMaterno(dto.getApellidoMaterno());
            persona.setEdad(dto.getEdad());
            if (dto.getSexo() != null && !dto.getSexo().isEmpty()) {
                persona.setSexo(dto.getSexo().charAt(0));
            }
            Persona personaGuardada = personaService.guardar(persona);

            // 4. Crear Usuario
            Usuario usuario = new Usuario();
            usuario.setNombreUsuario(dto.getNombreUsuario());
            usuario.setContraseña(dto.getContraseña());
            usuario.setEstado(dto.getEstadoUsuario() != null ? dto.getEstadoUsuario() : true);
            usuario.setPersona(personaGuardada);
            usuario.setEmail(dto.getEmail());
            Usuario usuarioGuardado = usuarioService.guardar(usuario);

            // 5. Crear Encargado según el rol
            Long idEncargado = null;
            String rolNombre = dto.getRol();
            
            switch (dto.getRol()) {
                case "INSUMO":
                    EncargadoInsumo encargadoInsumo = new EncargadoInsumo();
                    encargadoInsumo.setUsuario(usuarioGuardado);
                    encargadoInsumo.setArea_responsabilidad(dto.getAreaResponsabilidad());
                    encargadoInsumo.setCodigo_almacen(dto.getCodigoAlmacen());
                    EncargadoInsumo insumoGuardado = encargadoInsumoService.guardar(encargadoInsumo);
                    idEncargado = insumoGuardado.getIdEncargadoInsumo();
                    break;
                    
                case "RADIOGRAFIA":
                    EncargadoRadiografia encargadoRad = new EncargadoRadiografia();
                    encargadoRad.setUsuario(usuarioGuardado);
                    encargadoRad.setCodigoEncarRadiografia(dto.getCodigoRadiografia());
                    EncargadoRadiografia radGuardado = encargadoRadiografiaService.guardar(encargadoRad);
                    idEncargado = radGuardado.getIdEncargadoRadiografia();
                    break;
                    
                case "RECEPCION":
                    Recepcion recepcion = new Recepcion();
                    recepcion.setUsuario(usuarioGuardado);
                    recepcion.setCodigoRecepcion(dto.getCodigoRecepcion());
                    Recepcion recGuardado = recepcionService.guardar(recepcion);
                    idEncargado = recGuardado.getIdRecepcion();
                    break;
                    
                default:
                    return ResponseEntity.badRequest().body(
                        Map.of("error", "Rol no válido: " + dto.getRol())
                    );
            }

            // 6. Respuesta exitosa
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Encargado registrado exitosamente");
            respuesta.put("id_encargado", idEncargado);
            respuesta.put("rol", rolNombre);
            respuesta.put("nombre_completo", personaGuardada.getNombre() + " " + 
                                           personaGuardada.getApellidoPaterno());
            respuesta.put("usuario", usuarioGuardado.getNombreUsuario());
            respuesta.put("email", usuarioGuardado.getEmail());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                Map.of("error", "Error al registrar encargado: " + e.getMessage())
            );
        }
    }

    // ============================================================
    // LISTAR TODOS LOS ENCARGADOS
    // ============================================================
    
    @GetMapping("/listar")
    public ResponseEntity<List<EncargadoListaDTO>> listarTodos() {
        try {
            List<EncargadoListaDTO> resultados = new java.util.ArrayList<>();
            
            // 1. Encargados de Insumos
            List<EncargadoInsumo> insumos = encargadoInsumoService.obtenerTodos();
            for (EncargadoInsumo ei : insumos) {
                EncargadoListaDTO dto = new EncargadoListaDTO();
                dto.setIdEncargado(ei.getIdEncargadoInsumo());
                dto.setRol("INSUMO");
                Persona persona = ei.getUsuario().getPersona();
                dto.setNombreCompleto(persona.getNombre() + " " + persona.getApellidoPaterno());
                dto.setNombreUsuario(ei.getUsuario().getNombreUsuario());
                dto.setEmail(ei.getUsuario().getEmail());
                dto.setEstado(ei.getUsuario().getEstado());
                dto.setAreaResponsabilidad(ei.getArea_responsabilidad());
                dto.setCodigoAlmacen(ei.getCodigo_almacen());
                resultados.add(dto);
            }
            
            // 2. Encargados de Radiografía
            List<EncargadoRadiografia> radiografias = encargadoRadiografiaService.obtenerTodos();
            for (EncargadoRadiografia er : radiografias) {
                EncargadoListaDTO dto = new EncargadoListaDTO();
                dto.setIdEncargado(er.getIdEncargadoRadiografia());
                dto.setRol("RADIOGRAFIA");
                Persona persona = er.getUsuario().getPersona();
                dto.setNombreCompleto(persona.getNombre() + " " + persona.getApellidoPaterno());
                dto.setNombreUsuario(er.getUsuario().getNombreUsuario());
                dto.setEmail(er.getUsuario().getEmail());
                dto.setEstado(er.getUsuario().getEstado());
                dto.setCodigoRadiografia(er.getCodigoEncarRadiografia());
                resultados.add(dto);
            }
            
            // 3. Recepcionistas
            List<Recepcion> recepciones = recepcionService.obtenerTodos();
            for (Recepcion r : recepciones) {
                EncargadoListaDTO dto = new EncargadoListaDTO();
                dto.setIdEncargado(r.getIdRecepcion());
                dto.setRol("RECEPCION");
                Persona persona = r.getUsuario().getPersona();
                dto.setNombreCompleto(persona.getNombre() + " " + persona.getApellidoPaterno());
                dto.setNombreUsuario(r.getUsuario().getNombreUsuario());
                dto.setEmail(r.getUsuario().getEmail());
                dto.setEstado(r.getUsuario().getEstado());
                dto.setCodigoRecepcion(r.getCodigoRecepcion());
                resultados.add(dto);
            }
            
            return ResponseEntity.ok(resultados);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ============================================================
    // LISTAR ENCARGADOS POR ROL
    // ============================================================
    
    @GetMapping("/listar-por-rol/{rol}")
    public ResponseEntity<List<EncargadoListaDTO>> listarPorRol(@PathVariable String rol) {
        try {
            List<EncargadoListaDTO> resultados = new java.util.ArrayList<>();
            
            switch (rol.toUpperCase()) {
                case "INSUMO":
                    List<EncargadoInsumo> insumos = encargadoInsumoService.obtenerTodos();
                    for (EncargadoInsumo ei : insumos) {
                        EncargadoListaDTO dto = new EncargadoListaDTO();
                        dto.setIdEncargado(ei.getIdEncargadoInsumo());
                        dto.setRol("INSUMO");
                        Persona persona = ei.getUsuario().getPersona();
                        dto.setNombreCompleto(persona.getNombre() + " " + persona.getApellidoPaterno());
                        dto.setNombreUsuario(ei.getUsuario().getNombreUsuario());
                        dto.setEmail(ei.getUsuario().getEmail());
                        dto.setEstado(ei.getUsuario().getEstado());
                        dto.setAreaResponsabilidad(ei.getArea_responsabilidad());
                        dto.setCodigoAlmacen(ei.getCodigo_almacen());
                        resultados.add(dto);
                    }
                    break;
                    
                case "RADIOGRAFIA":
                    List<EncargadoRadiografia> radiografias = encargadoRadiografiaService.obtenerTodos();
                    for (EncargadoRadiografia er : radiografias) {
                        EncargadoListaDTO dto = new EncargadoListaDTO();
                        dto.setIdEncargado(er.getIdEncargadoRadiografia());
                        dto.setRol("RADIOGRAFIA");
                        Persona persona = er.getUsuario().getPersona();
                        dto.setNombreCompleto(persona.getNombre() + " " + persona.getApellidoPaterno());
                        dto.setNombreUsuario(er.getUsuario().getNombreUsuario());
                        dto.setEmail(er.getUsuario().getEmail());
                        dto.setEstado(er.getUsuario().getEstado());
                        dto.setCodigoRadiografia(er.getCodigoEncarRadiografia());
                        resultados.add(dto);
                    }
                    break;
                    
                case "RECEPCION":
                    List<Recepcion> recepciones = recepcionService.obtenerTodos();
                    for (Recepcion r : recepciones) {
                        EncargadoListaDTO dto = new EncargadoListaDTO();
                        dto.setIdEncargado(r.getIdRecepcion());
                        dto.setRol("RECEPCION");
                        Persona persona = r.getUsuario().getPersona();
                        dto.setNombreCompleto(persona.getNombre() + " " + persona.getApellidoPaterno());
                        dto.setNombreUsuario(r.getUsuario().getNombreUsuario());
                        dto.setEmail(r.getUsuario().getEmail());
                        dto.setEstado(r.getUsuario().getEstado());
                        dto.setCodigoRecepcion(r.getCodigoRecepcion());
                        resultados.add(dto);
                    }
                    break;
                    
                default:
                    return ResponseEntity.badRequest().build();
            }
            
            return ResponseEntity.ok(resultados);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}