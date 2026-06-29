package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.Archivo;
import proyect_final.clinica.Model.Entity.Paciente;
import proyect_final.clinica.Service.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/archivos")
@CrossOrigin(origins = "*")
public class ArchivoController {

    @Autowired
    private ArchivoService archivoService;

    @GetMapping
    public ResponseEntity<List<Archivo>> getAllArchivos() {
        List<Archivo> archivos = archivoService.getAllArchivos();
        return ResponseEntity.ok(archivos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Archivo> getArchivoById(@PathVariable Long id) {
        Optional<Archivo> archivo = archivoService.getArchivoById(id);
        return archivo.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Archivo> createArchivo(@RequestBody Archivo archivo) {
        Archivo nuevoArchivo = archivoService.saveArchivo(archivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoArchivo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Archivo> updateArchivo(@PathVariable Long id, @RequestBody Archivo archivo) {
        if (!archivoService.getArchivoById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        archivo.setIdArchivo(id);
        Archivo archivoActualizado = archivoService.saveArchivo(archivo);
        return ResponseEntity.ok(archivoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArchivo(@PathVariable Long id) {
        if (!archivoService.getArchivoById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        archivoService.deleteArchivo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar-por-paciente")
    public ResponseEntity<?> buscarArchivoPorPaciente(
            @RequestParam(required = false) Integer ci,
            @RequestParam(required = false) String nombre) {

        try {

            if ((ci == null || ci == 0) &&
                (nombre == null || nombre.trim().isEmpty())) {

                return ResponseEntity.badRequest()
                        .body(createErrorResponse(
                                "Debe proporcionar CI o nombre del paciente"));
            }

            Optional<Archivo> archivoOpt =
                    archivoService.findArchivoByPacienteCiOrNombre(ci, nombre);

            if (archivoOpt.isPresent()) {

                Archivo archivo = archivoOpt.get();
                Paciente paciente = archivo.getPaciente();

                Map<String, Object> response =
                        createSuccessResponse("Archivo encontrado");

                // Datos del archivo
                Map<String, Object> archivoData = new HashMap<>();
                archivoData.put("idArchivo", archivo.getIdArchivo());
                archivoData.put("codigoArchivo", archivo.getCodigoArchivo());
                archivoData.put("ubicacionFisica", archivo.getUbicacionFisica());
                response.put("archivo", archivoData);

                // Datos completos del paciente
                Map<String, Object> pacienteData = new HashMap<>();
                pacienteData.put("id", paciente.getIdPaciente());
                pacienteData.put("ci", paciente.getCi());
                pacienteData.put("nombreCompleto", getNombreCompleto(paciente.getPersona()));
                pacienteData.put("historialClinico", paciente.getHistorialClinico());
                pacienteData.put("telefono", paciente.getTelefono());
                pacienteData.put("direccion", paciente.getDireccion());
                
                response.put("paciente", pacienteData);

                return ResponseEntity.ok(response);

            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse(
                                "No se encontró archivo para el paciente",
                                false));

            }

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(
                            "Error: " + e.getMessage()));
        }
    }

@GetMapping("/id-por-paciente")
public ResponseEntity<?> getIdArchivoPorPaciente(
        @RequestParam(required = false) String ci,
        @RequestParam(required = false) String nombre) {
    
    try {
        Integer ciInt = null;
        String nombreBusqueda = null;
        
        // Determinar qué parámetro usar
        if (ci != null && !ci.trim().isEmpty()) {
            try {
                ciInt = Integer.parseInt(ci.trim());
            } catch (NumberFormatException e) {
                nombreBusqueda = ci.trim();
            }
        }
        
        if (nombre != null && !nombre.trim().isEmpty()) {
            nombreBusqueda = nombre.trim();
        }
        
        if (ciInt == null && (nombreBusqueda == null || nombreBusqueda.isEmpty())) {
            return ResponseEntity.badRequest().body(
                createErrorResponse("Debe proporcionar CI o nombre del paciente")
            );
        }
        
        Optional<Archivo> archivoOpt = archivoService.buscarArchivoPorCiONombre(ciInt, nombreBusqueda);
        
        if (archivoOpt.isPresent()) {
            Archivo archivo = archivoOpt.get();
            Map<String, Object> response = createSuccessResponse("ID de archivo encontrado");
            response.put("idArchivo", archivo.getIdArchivo());
            response.put("found", true);
            response.put("codigoArchivo", archivo.getCodigoArchivo());
            
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = createErrorResponse("No se encontró archivo", false);
            response.put("found", false);
            response.put("idArchivo", null);
            
            return ResponseEntity.ok(response);
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error: " + e.getMessage()));
    }
}
    
    //  Verificar si paciente tiene archivo
    @GetMapping("/verificar/{idPaciente}")
    public ResponseEntity<?> verificarPacienteTieneArchivo(@PathVariable Long idPaciente) {
        try {
            boolean tieneArchivo = archivoService.pacienteTieneArchivo(idPaciente);
            
            Map<String, Object> response = createSuccessResponse("Verificación completada");
            response.put("idPaciente", idPaciente);
            response.put("tieneArchivo", tieneArchivo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error: " + e.getMessage()));
        }
    }
    
    // ===== MÉTODOS AUXILIARES =====
    
    private String getNombreCompleto(proyect_final.clinica.Model.Entity.Persona persona) {
        if (persona == null) return "No disponible";
        return String.format("%s %s %s", 
            persona.getNombre() != null ? persona.getNombre() : "",
            persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "",
            persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : ""
        ).trim();
    }
    
    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        return createErrorResponse(message, true);
    }
    
    private Map<String, Object> createErrorResponse(String message, boolean isError) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        if (isError) {
            response.put("error", true);
        }
        return response;
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<Archivo> getArchivoByPacienteId(@PathVariable Long idPaciente) {
        Optional<Archivo> archivo = archivoService.findArchivoByPacienteId(idPaciente);
        return archivo.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
}