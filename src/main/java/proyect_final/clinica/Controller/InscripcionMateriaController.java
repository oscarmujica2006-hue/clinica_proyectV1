package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Model.Entity.InscripcionMateria;
import proyect_final.clinica.Model.Entity.Materia;
import proyect_final.clinica.Model.Entity.Periodo;
import proyect_final.clinica.Service.EstudianteService;
import proyect_final.clinica.Service.InscripcionMateriaService;
import proyect_final.clinica.Service.MateriaService;
import proyect_final.clinica.Service.PeriodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/inscripciones")
@CrossOrigin(origins = "*")
public class InscripcionMateriaController {

    @Autowired
    private InscripcionMateriaService inscripcionService;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private PeriodoService periodoService;

    @PostMapping
    public ResponseEntity<?> crearInscripcion(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== CREANDO INSCRIPCIÓN ===");
            System.out.println("Request: " + request);
            
            // Extraer datos del request
            @SuppressWarnings("unchecked")
            Map<String, Object> estudianteMap = (Map<String, Object>) request.get("estudiante");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> materiaMap = (Map<String, Object>) request.get("materia");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> periodoMap = (Map<String, Object>) request.get("periodo");
            
            if (estudianteMap == null || materiaMap == null || periodoMap == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Datos incompletos"));
            }
            
            // 🔴 IMPORTANTE: Obtener el código de estudiante
            Integer codigoEstudiante = null;
            if (estudianteMap.containsKey("codigoEstudiante")) {
                Object codigoObj = estudianteMap.get("codigoEstudiante");
                if (codigoObj instanceof Integer) {
                    codigoEstudiante = (Integer) codigoObj;
                } else if (codigoObj instanceof String) {
                    codigoEstudiante = Integer.parseInt((String) codigoObj);
                } else if (codigoObj instanceof Number) {
                    codigoEstudiante = ((Number) codigoObj).intValue();
                }
            } else if (estudianteMap.containsKey("idEstudiante")) {
                // Si envían idEstudiante, lo usamos como código (asumiendo que es el mismo)
                codigoEstudiante = ((Number) estudianteMap.get("idEstudiante")).intValue();
                System.out.println("Usando idEstudiante como código: " + codigoEstudiante);
            }
            
            System.out.println("Código de estudiante: " + codigoEstudiante);
            
            if (codigoEstudiante == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Se requiere el código del estudiante (codigoEstudiante)"));
            }
            
            // Obtener materia ID
            Long materiaId = null;
            if (materiaMap.containsKey("id_materia")) {
                materiaId = ((Number) materiaMap.get("id_materia")).longValue();
            } else if (materiaMap.containsKey("idMateria")) {
                materiaId = ((Number) materiaMap.get("idMateria")).longValue();
            }
            
            // Obtener periodo ID
            Long periodoId = null;
            if (periodoMap.containsKey("idPeriodo")) {
                periodoId = ((Number) periodoMap.get("idPeriodo")).longValue();
            } else if (periodoMap.containsKey("id_periodo")) {
                periodoId = ((Number) periodoMap.get("id_periodo")).longValue();
            }
            
            String fechaInscripcion = (String) request.get("fechaInscripcion");
            String estadoInscripcion = (String) request.get("estadoInscripcion");
            
            System.out.println("Materia ID: " + materiaId);
            System.out.println("Periodo ID: " + periodoId);
            System.out.println("Fecha: " + fechaInscripcion);
            System.out.println("Estado: " + estadoInscripcion);
            
            if (materiaId == null || periodoId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Datos incompletos: materia y periodo son requeridos"));
            }
            
            // 🔴 Buscar estudiante por código
            Optional<Estudiante> estudianteOpt = estudianteService.buscarPorCodigoExacto(codigoEstudiante);
            Optional<Materia> materiaOpt = materiaService.obtenerPorId(materiaId);
            Periodo periodo = periodoService.obtenerPorId(periodoId);
            
            System.out.println("Estudiante encontrado: " + estudianteOpt.isPresent());
            System.out.println("Materia encontrada: " + materiaOpt.isPresent());
            System.out.println("Periodo encontrado: " + (periodo != null));
            
            if (estudianteOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Estudiante no encontrado con código: " + codigoEstudiante));
            }
            if (materiaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Materia no encontrada con ID: " + materiaId));
            }
            if (periodo == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Período no encontrado con ID: " + periodoId));
            }
            
            Estudiante estudiante = estudianteOpt.get();
            Materia materia = materiaOpt.get();
            
            // Verificar si ya existe inscripción
            Optional<InscripcionMateria> existente = inscripcionService.obtenerPorEstudianteMateriaPeriodo(
                estudiante, materia, periodo
            );
            
            if (existente.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El estudiante ya está inscrito en esta materia para este período"));
            }
            
            // Crear nueva inscripción
            InscripcionMateria inscripcion = new InscripcionMateria();
            inscripcion.setEstudiante(estudiante);
            inscripcion.setMateria(materia);
            inscripcion.setPeriodo(periodo);
            inscripcion.setFechaInscripcion(LocalDate.parse(fechaInscripcion));
            inscripcion.setEstadoInscripcion(estadoInscripcion);
            
            InscripcionMateria guardada = inscripcionService.guardar(inscripcion);
            
            // Construir respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Inscripción exitosa");
            response.put("idInscripcion", guardada.getIdInscripcionMateria());
            
            String nombreEstudiante = "Estudiante";
            if (estudiante.getUsuario() != null && estudiante.getUsuario().getPersona() != null) {
                nombreEstudiante = estudiante.getUsuario().getPersona().getNombre() + " " + 
                                 (estudiante.getUsuario().getPersona().getApellidoPaterno() != null ? 
                                  estudiante.getUsuario().getPersona().getApellidoPaterno() : "");
            }
            
            response.put("estudiante", nombreEstudiante);
            response.put("materia", materia.getNombreMateria());
            response.put("periodo", periodo.getNombrePeriodo());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al crear inscripción: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}