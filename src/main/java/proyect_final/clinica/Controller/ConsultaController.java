package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.Consulta;
import proyect_final.clinica.Model.Dto.ConsultaCompletaDTO;
import proyect_final.clinica.Service.ConsultaService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import proyect_final.clinica.Model.Dto.ConsultaConFotosDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/consultas")
@CrossOrigin(origins = "*")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

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

    // ========== NUEVO MÉTODO PARA SUBIR FOTO INDIVIDUAL ==========
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