package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.Consulta;
import proyect_final.clinica.Model.Dto.ConsultaCompletaDTO;
import proyect_final.clinica.Model.Dto.ConsultaConRevisionDTO;
import proyect_final.clinica.Service.ConsultaService;
import proyect_final.clinica.Model.Entity.Revision;
import proyect_final.clinica.Service.OdontogramaService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/consultas")
@CrossOrigin(origins = "*")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private OdontogramaService odontogramaService;

    // ========== MÉTODOS EXISTENTES ==========
    
    @GetMapping
    public List<Consulta> obtenerTodas() {
        return consultaService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consulta> obtenerPorId(@PathVariable Long id) {
        Optional<Consulta> consulta = consultaService.obtenerPorId(id);
        return consulta.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/completa")
    public ResponseEntity<?> crearConsultaCompleta(@RequestBody ConsultaCompletaDTO consultaDTO) {
        try {
            Consulta nuevaConsulta = consultaService.guardarConsultaCompleta(consultaDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("idConsulta", nuevaConsulta.getIdConsulta());
            response.put("mensaje", "Consulta guardada exitosamente");
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error al guardar la consulta: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ===== NUEVO ENDPOINT: Guarda Consulta + Revisión (Odontograma) =====
    @PostMapping("/completa-con-revision")
    public ResponseEntity<?> crearConsultaCompletaConRevision(
            @RequestBody ConsultaConRevisionDTO consultaDTO) {
        
        try {
            // 1. Guardar la Consulta completa
            Consulta nuevaConsulta = consultaService.guardarConsultaCompleta(consultaDTO);
            Long idConsulta = nuevaConsulta.getIdConsulta();

            // 2. Preparar datos de revisión
            Map<String, Object> revisionData = new HashMap<>();
            revisionData.put("observaciones_generales", consultaDTO.getObservacionesGenerales());
            
            // ===== DIAGNÓSTICO COMPLETO (CPO + CEO) =====
            Map<String, Object> diagnostico = consultaDTO.getDiagnostico();
            if (diagnostico != null) {
                // Asegurar que tiene todos los campos
                revisionData.put("diagnostico", diagnostico);
            }
            
            // ===== DIENTES DE AMBOS TIPOS =====
            revisionData.put("dientesPermanentes", consultaDTO.getDientesPermanentes());
            revisionData.put("dientesTemporales", consultaDTO.getDientesTemporales());
            
            // Tipo activo (para saber cuál mostrar al cargar)
            revisionData.put("tipoDenticionActivo", consultaDTO.getTipoDenticionActivo());

            // 3. Guardar la Revisión (odontograma)
            Revision revision = odontogramaService.guardarRevision(idConsulta, revisionData);

            // 4. Respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("idConsulta", idConsulta);
            response.put("idRevision", revision.getIdRevision());
            response.put("mensaje", "Consulta y revisión guardadas exitosamente");

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error al guardar: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ===== NUEVO ENDPOINT: Cargar revisión por ID de consulta =====
    @GetMapping("/revision/{idConsulta}")
    public ResponseEntity<?> obtenerRevisionPorConsulta(@PathVariable Long idConsulta) {
        try {
            Revision revision = odontogramaService.cargarRevisionPorConsulta(idConsulta);
            if (revision == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", "Revisión no encontrada"));
            }
            
            Map<String, Object> datosRevision = odontogramaService.obtenerDatosRevision(revision.getIdRevision());
            return ResponseEntity.ok(datosRevision);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ===== NUEVO ENDPOINT: Eliminar revisión =====
    @DeleteMapping("/revision/{idRevision}")
    public ResponseEntity<?> eliminarRevision(@PathVariable Long idRevision) {
        try {
            odontogramaService.eliminarRevision(idRevision);
            return ResponseEntity.ok(Map.of("success", true, "mensaje", "Revisión eliminada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ========== MÉTODOS EXISTENTES (sin cambios) ==========
    
    @PostMapping
    public ResponseEntity<Consulta> crearConsulta(@RequestBody Consulta consulta) {
        try {
            Consulta nuevaConsulta = consultaService.guardar(consulta);
            return new ResponseEntity<>(nuevaConsulta, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> eliminarConsulta(@PathVariable Long id) {
        try {
            consultaService.eliminar(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/paciente/{idPaciente}")
    public List<Consulta> obtenerPorPaciente(@PathVariable Long idPaciente) {
        return consultaService.obtenerPorPaciente(idPaciente);
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public List<Consulta> obtenerPorEstudiante(@PathVariable Long idEstudiante) {
        return consultaService.obtenerPorEstudiante(idEstudiante);
    }
    
    @GetMapping("/paciente/{idPaciente}/completo")
    public ResponseEntity<List<ConsultaCompletaDTO>> obtenerConsultasCompletasPorPaciente(@PathVariable Long idPaciente) {
        try {
            List<ConsultaCompletaDTO> consultas = consultaService.obtenerConsultasCompletasPorPaciente(idPaciente);
            System.out.println("✅ Consultas DTO encontradas: " + consultas.size());
            return ResponseEntity.ok(consultas);
        } catch (Exception e) {
            System.out.println("❌ Error al obtener consultas completas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/subir-foto")
    public ResponseEntity<?> subirFotoConsulta(
            @RequestParam Long idConsulta,
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            consultaService.guardarFotoConsulta(idConsulta, archivo);
            return ResponseEntity.ok(Map.of("success", true, "mensaje", "Foto subida exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}