package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Dto.ClinicaConDocenteDTO;
import proyect_final.clinica.Model.Dto.DocenteDTO;
import proyect_final.clinica.Model.Entity.Clinica;
import proyect_final.clinica.Model.Entity.Docente;
import proyect_final.clinica.Model.Entity.Persona;
import proyect_final.clinica.Service.ClinicaService;
import proyect_final.clinica.Service.DocenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clinicas")
@CrossOrigin(origins = "*")
public class ClinicaController {

    @Autowired
    private ClinicaService clinicaService;

    @Autowired
    private DocenteService docenteService;
    

    @GetMapping
    public ResponseEntity<List<ClinicaConDocenteDTO>> listarTodas() {
        List<Clinica> clinicas = clinicaService.obtenerTodas();
        List<ClinicaConDocenteDTO> resultado = clinicas.stream().map(clinica -> {
            String nombreDoctor = "No asignado";
            if (clinica.getDocentes() != null && !clinica.getDocentes().isEmpty()) {
                // Obtener el primer docente
                Docente primerDocente = clinica.getDocentes().get(0);
                nombreDoctor = obtenerNombreCompletoDocente(primerDocente);
            }
            return new ClinicaConDocenteDTO(
                clinica.getIdClinica(),
                clinica.getNombreClinica(),
                clinica.getTurno().getNombreTurno(),
                nombreDoctor
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    // Método auxiliar para obtener nombre completo 
    private String obtenerNombreCompletoDocente(Docente docente) {
        if (docente.getUsuario() != null && docente.getUsuario().getPersona() != null) {
            Persona p = docente.getUsuario().getPersona();
            return (p.getNombre() + " " + p.getApellidoPaterno() + " " + p.getApellidoMaterno()).trim();
        }
        return "Docente";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Clinica> obtenerPorId(@PathVariable Long id) {
        return clinicaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<Clinica>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(clinicaService.buscarPorNombre(nombre));
    }

    @GetMapping("/buscar/turno")
    public ResponseEntity<List<Clinica>> buscarPorTurno(@RequestParam String turno) {
        return ResponseEntity.ok(clinicaService.buscarPorTurno(turno));
    }

    //  Crear nueva clínica
    @PostMapping
    public ResponseEntity<Clinica> crear(@RequestBody Clinica clinica) {
        Clinica nuevaClinica = clinicaService.guardar(clinica);
        return ResponseEntity.ok(nuevaClinica);
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clinicaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/docentes")
    public ResponseEntity<List<DocenteDTO>> listarDocentesPorClinica(@PathVariable Long id) {
        List<Docente> docentes = docenteService.obtenerPorClinica(id);
        List<DocenteDTO> dtos = docentes.stream()
                .map(docenteService::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}