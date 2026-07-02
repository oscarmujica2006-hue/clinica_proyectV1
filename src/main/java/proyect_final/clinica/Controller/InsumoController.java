package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Dto.InsumoConsumidoDTO;
import proyect_final.clinica.Model.Dto.StockBajoDTO;
import proyect_final.clinica.Service.InsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController  
@RequestMapping("/api/insumos")  
public class InsumoController {

    @Autowired
    private InsumoService insumoService;

    // ===== ENDPOINT REST =====
    @GetMapping("/consumo")
    public ResponseEntity<List<InsumoConsumidoDTO>> obtenerConsumo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        List<InsumoConsumidoDTO> insumos;
        
        // Si hay fechas, filtrar
        if (fechaInicio != null && fechaFin != null) {
            insumos = insumoService.obtenerInsumosMasConsumidos(fechaInicio, fechaFin);
        } else {
            // Si no hay fechas, usar un rango por defecto (últimos 30 días)
            LocalDate fechaInicioDefault = LocalDate.now().minusMonths(1);
            LocalDate fechaFinDefault = LocalDate.now();
            insumos = insumoService.obtenerInsumosMasConsumidos(fechaInicioDefault, fechaFinDefault);
        }
        
        return ResponseEntity.ok(insumos);
    }
  

    @GetMapping("/stock-bajo/bajos")
    public ResponseEntity<List<StockBajoDTO>> obtenerBajos() {
        List<StockBajoDTO> todos = insumoService.obtenerStockBajo();
        List<StockBajoDTO> bajos = todos.stream()
            .filter(item -> "BAJO".equals(item.getEstado()))
            .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(bajos);
    }

    @GetMapping("/todos-insumos")
    public ResponseEntity<List<StockBajoDTO>> obtenerTodosInsumos() {
        List<StockBajoDTO> todos = insumoService.obtenerStockBajo();
        return ResponseEntity.ok(todos);
    }

}







