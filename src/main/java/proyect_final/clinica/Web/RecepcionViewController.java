package proyect_final.clinica.Web;

import proyect_final.clinica.Model.Entity.Persona;
import proyect_final.clinica.Model.Entity.Paciente;
import proyect_final.clinica.Model.Entity.TipoPaciente;
import proyect_final.clinica.Service.PersonaService;
import proyect_final.clinica.Service.PacienteService;
import proyect_final.clinica.Service.TipoPacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/recepcion")
public class RecepcionViewController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private TipoPacienteService tipoPacienteService;

    // Página principal de recepción
    @GetMapping
    public String paginaRecepcion(Model model) {
        List<Persona> personas = personaService.obtenerTodos();
        List<Paciente> pacientes = pacienteService.obtenerTodos();
        
        model.addAttribute("personas", personas);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("persona", new Persona());
        model.addAttribute("paciente", new Paciente());
        
        return "recepcion/recepcion_pg";
    }
    
    // Método auxiliar para determinar el tipo de paciente según la edad
    private TipoPaciente determinarTipoPacientePorEdad(int edad) {
        List<TipoPaciente> tipos = tipoPacienteService.obtenerTodos();
        
        // Buscar el tipo que corresponde según los rangos de edad
        return tipos.stream()
            .filter(tipo -> {
                Integer edadMin = tipo.getEdadMin();     // ← CORREGIDO: usar getEdadMin()
                Integer edadMax = tipo.getEdadMax();   // ← CORREGIDO: usar getEdadMax()
                
                // Si tiene ambos límites
                if (edadMin != null && edadMax != null) {
                    return edad >= edadMin && edad <= edadMax;
                }
                // Si solo tiene límite inferior (ej: 13 a 99+)
                else if (edadMin != null && edadMax == null) {
                    return edad >= edadMin;
                }
                // Si solo tiene límite superior
                else if (edadMin == null && edadMax != null) {
                    return edad <= edadMax;
                }
                return false;
            })
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró tipo de paciente para edad: " + edad));
    }

    // ========== OPERACIONES PARA PERSONA ==========

    @PostMapping("/persona/crear")
    public String crearPersona(@RequestParam String nombre,
                              @RequestParam String apellidoPaterno,
                              @RequestParam String apellidoMaterno,
                              @RequestParam Integer edad,
                              @RequestParam Character sexo) {
        Persona persona = new Persona();
        persona.setNombre(nombre);
        persona.setApellidoPaterno(apellidoPaterno);
        persona.setApellidoMaterno(apellidoMaterno);
        persona.setEdad(edad);
        persona.setSexo(sexo);
        
        personaService.guardar(persona);
        return "redirect:/recepcion";
    }

    // ========== OPERACIONES PARA PACIENTE ==========

    @PostMapping("/paciente/crear")
    @ResponseBody
    public ResponseEntity<String> crearPaciente(@RequestParam String nombre,
                                               @RequestParam String apellidoPaterno,
                                               @RequestParam String apellidoMaterno,
                                               @RequestParam Integer edad, 
                                               @RequestParam Character sexo,
                                               @RequestParam String historialClinico,
                                               @RequestParam String lugarNacimiento,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaNacimiento,
                                               @RequestParam String ocupacion,
                                               @RequestParam String direccion,
                                               @RequestParam String telefono,
                                               @RequestParam String gradoInstruccion,
                                               @RequestParam String estadoCivil,
                                               @RequestParam(required = false) String nacionesOriginarias,
                                               @RequestParam(required = false) String idioma,
                                               @RequestParam Integer ci) {
        
        try {
            // 1. Crear la Persona
            Persona persona = new Persona();
            persona.setNombre(nombre);
            persona.setApellidoPaterno(apellidoPaterno);
            persona.setApellidoMaterno(apellidoMaterno);
            persona.setEdad(edad);       
            persona.setSexo(sexo);
            persona = personaService.guardar(persona);
            
            // 2. Determinar el tipo de paciente según la edad
            TipoPaciente tipoPaciente = determinarTipoPacientePorEdad(edad);
            
            // 3. Crear el Paciente con el tipo_paciente seleccionado
            Paciente paciente = new Paciente();
            paciente.setHistorialClinico(historialClinico);
            paciente.setPersona(persona);
            paciente.setLugarNacimiento(lugarNacimiento);
            paciente.setFechaNacimiento(fechaNacimiento);
            paciente.setOcupacion(ocupacion);
            paciente.setDireccion(direccion);
            paciente.setTelefono(telefono);
            paciente.setGradoInstruccion(gradoInstruccion);
            paciente.setEstadoCivil(estadoCivil);
            paciente.setNacionesOriginarias(nacionesOriginarias);
            paciente.setIdioma(idioma);
            paciente.setCi(ci);
            paciente.setTipoPaciente(tipoPaciente);

            pacienteService.guardar(paciente);
            
            String mensaje = String.format("¡Paciente registrado exitosamente! Tipo: %s - %s", 
                tipoPaciente.getNombreTipo(), 
                tipoPaciente.getDescripcion());
            
            return ResponseEntity.ok(mensaje);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error al crear paciente: " + e.getMessage());
        }
    }

    @PostMapping("/paciente/actualizar/{id}")
    public String actualizarPaciente(@PathVariable Long id,
                                    @RequestParam String nombre,
                                    @RequestParam String apellidoPaterno,
                                    @RequestParam String apellidoMaterno,
                                    @RequestParam Integer edad,
                                    @RequestParam Character sexo,
                                    @RequestParam String historialClinico,
                                    @RequestParam String lugarNacimiento,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaNacimiento,
                                    @RequestParam String ocupacion,
                                    @RequestParam String direccion,
                                    @RequestParam String telefono,
                                    @RequestParam String gradoInstruccion,
                                    @RequestParam String estadoCivil,
                                    @RequestParam(required = false) String nacionesOriginarias,
                                    @RequestParam(required = false) String idioma,
                                    @RequestParam Integer ci) {
        
        try {
            Paciente pacienteExistente = pacienteService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
            
            Persona persona = pacienteExistente.getPersona();
            persona.setNombre(nombre);
            persona.setApellidoPaterno(apellidoPaterno);
            persona.setApellidoMaterno(apellidoMaterno);
            persona.setEdad(edad);
            persona.setSexo(sexo);
    
            personaService.guardar(persona);
            
            pacienteExistente.setHistorialClinico(historialClinico);
            pacienteExistente.setLugarNacimiento(lugarNacimiento);
            pacienteExistente.setFechaNacimiento(fechaNacimiento);
            pacienteExistente.setOcupacion(ocupacion);
            pacienteExistente.setDireccion(direccion);
            pacienteExistente.setTelefono(telefono);
            pacienteExistente.setGradoInstruccion(gradoInstruccion);
            pacienteExistente.setEstadoCivil(estadoCivil);
            pacienteExistente.setNacionesOriginarias(nacionesOriginarias);
            pacienteExistente.setIdioma(idioma);
            pacienteExistente.setCi(ci);
            
            // Actualizar el tipo de paciente si la edad cambió
            TipoPaciente nuevoTipo = determinarTipoPacientePorEdad(edad);
            pacienteExistente.setTipoPaciente(nuevoTipo);
            
            pacienteService.guardar(pacienteExistente);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "redirect:/recepcion";
    }

    @GetMapping("/paciente/eliminar/{id}")
    public String eliminarPaciente(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return "redirect:/recepcion";
    }

    // ========== BÚSQUEDAS ==========

    @GetMapping("/buscar/personas")
    public String buscarPersonas(@RequestParam String nombre, Model model) {
        List<Persona> personas = personaService.buscarPorNombre(nombre);
        List<Paciente> pacientes = pacienteService.obtenerTodos();
        
        model.addAttribute("personas", personas);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("persona", new Persona());
        model.addAttribute("paciente", new Paciente());
        
        return "recepcion/recepcion_pg";
    }

    @GetMapping("/paginas/registro_paciente")
    public String mostrarRegistroPaciente() {
        return "recepcion/paginas/registro_paciente";
    }
}