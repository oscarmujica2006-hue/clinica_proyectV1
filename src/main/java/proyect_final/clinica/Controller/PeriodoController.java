package proyect_final.clinica.Controller;
import proyect_final.clinica.Model.Entity.Periodo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyect_final.clinica.Service.PeriodoService; 
import java.util.*;
@RestController
@RequestMapping("/api/periodos")
public class PeriodoController {
    @Autowired
    private PeriodoService periodoService;  

    @GetMapping("/activos")
    public ResponseEntity<List<Periodo>> getPeriodosActivos() {
        List<Periodo> periodos = periodoService.listarActivosPorFecha();
        return ResponseEntity.ok(periodos);
    }

    @PostMapping
    public ResponseEntity<Periodo> crearPeriodo(@RequestBody Periodo periodo) {
        Periodo nuevoPeriodo = periodoService.guardar(periodo);
        return ResponseEntity.ok(nuevoPeriodo);
    }
}