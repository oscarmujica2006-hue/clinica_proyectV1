package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.*;
import proyect_final.clinica.Model.Dao.EncargadoInsumoRepository;
import proyect_final.clinica.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/actas-entrega")
public class ActaEntregaController {

    @Autowired
    private AbastecimientoService abastecimientoService;
    
    @Autowired
    private DetalleAbastecimientoService detalleAbastecimientoService;
    
    @Autowired
    private ActaEntregaService actaEntregaService;
    
    @Autowired
    private DetalleActaEntregaService detalleActaEntregaService;
    
    @Autowired
    private LoteService loteService;
    
    @Autowired
    private InsumoService insumoService;
    
    @Autowired
    private EncargadoInsumoRepository encargadoInsumoRepository;

    // Crear Lote desde un Acta de Entrega (Encargado de Insumos)
    @PostMapping("/crear-lote")
    public ResponseEntity<?> crearLoteDesdeAbastecimiento(@RequestBody Map<String, Object> body) {
        try {
            // ✅ Validar que existan los campos requeridos
            if (!body.containsKey("idDetalleActa")) {
                return ResponseEntity.badRequest().body(Map.of("error", "El campo idDetalleActa es requerido"));
            }
            
            if (!body.containsKey("numeroLote") || body.get("numeroLote") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El número de lote es requerido"));
            }
            
            if (!body.containsKey("fechaVencimiento") || body.get("fechaVencimiento") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "La fecha de vencimiento es requerida"));
            }
            
            // ✅ Obtener valores con validación
            Long idDetalleActa = Long.valueOf(body.get("idDetalleActa").toString());
            String numeroLote = body.get("numeroLote").toString();
            String fechaVencimientoStr = body.get("fechaVencimiento").toString();
            LocalDate fechaVencimiento = LocalDate.parse(fechaVencimientoStr);
            
            // ✅ Campos opcionales con valores por defecto
            Integer cantidadEmpaque = body.containsKey("cantidadEmpaque") && body.get("cantidadEmpaque") != null ? 
                Integer.valueOf(body.get("cantidadEmpaque").toString()) : 1;
            
            Integer unidadPorEmpaque = body.containsKey("unidadPorEmpaque") && body.get("unidadPorEmpaque") != null ? 
                Integer.valueOf(body.get("unidadPorEmpaque").toString()) : 0;
            
            String observaciones = body.containsKey("observaciones") && body.get("observaciones") != null ? 
                body.get("observaciones").toString() : null;
            
            Long idEncargado = body.containsKey("idEncargado") && body.get("idEncargado") != null ? 
                Long.valueOf(body.get("idEncargado").toString()) : null;
            
            // Obtener el detalle del acta
            DetalleActaEntrega detalleActa = detalleActaEntregaService.findById(idDetalleActa)
                .orElseThrow(() -> new RuntimeException("Detalle de acta no encontrado con ID: " + idDetalleActa));
            
            // Verificar que el detalleActa no tenga lote aún
            if (detalleActa.getLote() != null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Este detalle de acta ya tiene un lote asociado"
                ));
            }
            
            DetalleAbastecimiento detalleAbast = detalleActa.getDetalleAbastecimiento();
            Insumo insumo = detalleAbast.getDetallePedido().getInsumo();
            Integer cantidadEntregada = detalleActa.getCantidadEntregada();
            
            // 1. Crear el Lote
            Lote lote = new Lote();
            lote.setNumeroLote(numeroLote);
            lote.setInsumo(insumo);
            lote.setDetalleActaEntrega(detalleActa);
            lote.setFechaIngreso(LocalDate.now());
            lote.setFechaVencimiento(fechaVencimiento);
            lote.setCantidadInicial(cantidadEntregada);
            lote.setCantidadDisponible(cantidadEntregada);
            
            // ✅ Campos opcionales
            if (cantidadEmpaque != null && cantidadEmpaque > 0) {
                lote.setCantidadEmpaque(cantidadEmpaque);
            }
            if (unidadPorEmpaque != null && unidadPorEmpaque > 0) {
                lote.setUnidadPorEmpaque(unidadPorEmpaque);
            }
            if (observaciones != null && !observaciones.isEmpty()) {
                lote.setObservaciones(observaciones);
            }
            
            lote = loteService.save(lote);
            
            // 2. Actualizar DetalleAbastecimiento
            int nuevaCantidadRecibida = detalleAbast.getCantidadRecibida() + cantidadEntregada;
            int nuevaCantidadRestante = detalleAbast.getCantidadRestante() - cantidadEntregada;
            
            detalleAbast.setCantidadRecibida(nuevaCantidadRecibida);
            detalleAbast.setCantidadRestante(nuevaCantidadRestante);
            
            // Actualizar estado del detalle
            if (nuevaCantidadRestante == 0) {
                detalleAbast.setEstadoDetalle("COMPLETADO");
            } else {
                detalleAbast.setEstadoDetalle("PARCIAL");
            }
            detalleAbastecimientoService.save(detalleAbast);
            
            // 3. Actualizar stock del insumo
            int nuevoStock = insumo.getStockTotal() + cantidadEntregada;
            insumo.setStockTotal(nuevoStock);
            insumo.setUltimaActualizacion(LocalDate.now());
            insumoService.guardar(insumo);
            
            // 4. Verificar si todos los detalles del abastecimiento están completados
            Abastecimiento abastecimiento = detalleAbast.getAbastecimiento();
            boolean todosCompletados = abastecimiento.getDetalles().stream()
                .allMatch(d -> "COMPLETADO".equals(d.getEstadoDetalle()));
            
            if (todosCompletados) {
                abastecimiento.setEstadoAbastecimiento("COMPLETADO");
                abastecimientoService.save(abastecimiento);
            }
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Lote creado exitosamente",
                "idLote", lote.getIdLote(),
                "numeroLote", lote.getNumeroLote(),
                "stockActual", insumo.getStockTotal(),
                "estadoAbastecimiento", abastecimiento.getEstadoAbastecimiento()
            ));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Formato de número inválido: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}