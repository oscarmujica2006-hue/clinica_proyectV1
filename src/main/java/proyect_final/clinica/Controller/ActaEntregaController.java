package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.*;
import proyect_final.clinica.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    // ================================================================
    // 1. CREAR LOTE DESDE ACTA DE ENTREGA
    // ================================================================
    @PostMapping("/crear-lote")
    public ResponseEntity<?> crearLoteDesdeAbastecimiento(@RequestBody Map<String, Object> body) {
        try {
            // ----- VALIDAR CAMPOS REQUERIDOS -----
            if (!body.containsKey("idDetalleActa")) {
                return ResponseEntity.badRequest().body(Map.of("error", "El campo idDetalleActa es requerido"));
            }
            if (!body.containsKey("numeroLote") || body.get("numeroLote") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El número de lote es requerido"));
            }
            if (!body.containsKey("fechaVencimiento") || body.get("fechaVencimiento") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "La fecha de vencimiento es requerida"));
            }
            
            Long idDetalleActa = Long.valueOf(body.get("idDetalleActa").toString());
            String numeroLote = body.get("numeroLote").toString();
            String fechaVencimientoStr = body.get("fechaVencimiento").toString();
            LocalDate fechaVencimiento = LocalDate.parse(fechaVencimientoStr);
            
            // ----- OBTENER DATOS DEL ACTA -----
            Integer cantidadEmpaquesRecibidos = body.containsKey("cantidadEmpaque") && body.get("cantidadEmpaque") != null ? 
                Integer.valueOf(body.get("cantidadEmpaque").toString()) : 1;
            
            Integer unidadesPorEmpaque = body.containsKey("unidadPorEmpaque") && body.get("unidadPorEmpaque") != null ? 
                Integer.valueOf(body.get("unidadPorEmpaque").toString()) : 1;
            
            String observaciones = body.containsKey("observaciones") && body.get("observaciones") != null ? 
                body.get("observaciones").toString() : null;
            
            Long idEncargado = body.containsKey("idEncargado") && body.get("idEncargado") != null ? 
                Long.valueOf(body.get("idEncargado").toString()) : null;
            
            // ----- BUSCAR DETALLE DEL ACTA -----
            DetalleActaEntrega detalleActa = detalleActaEntregaService.findById(idDetalleActa)
                .orElseThrow(() -> new RuntimeException("Detalle de acta no encontrado con ID: " + idDetalleActa));
            
            if (detalleActa.getCantidadEntregada() == null || detalleActa.getCantidadEntregada() <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No se puede crear un lote con cantidad 0"
                ));
            }
            
            if (detalleActa.getLote() != null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Este detalle de acta ya tiene un lote asociado"
                ));
            }
            
            DetalleAbastecimiento detalleAbast = detalleActa.getDetalleAbastecimiento();
            Insumo insumo = detalleAbast.getDetallePedido().getInsumo();
            
            // ============================================================
            // 🔥 ACTUALIZAR DetalleAbastecimiento (TODO EN EMPAQUES)
            // ============================================================
            // Ejemplo: 
            // - cantidad_comprometida = 11 (empaques que la directora aprobó)
            // - cantidad_recibida = 0 + 6 = 6 (empaques que llegaron en esta acta)
            // - cantidad_restante = 11 - 6 = 5 (empaques pendientes)
            // ============================================================
            
            int nuevaCantidadRecibida = detalleAbast.getCantidadRecibida() + cantidadEmpaquesRecibidos;
            int nuevaCantidadRestante = detalleAbast.getCantidadRestante() - cantidadEmpaquesRecibidos;
            
            detalleAbast.setCantidadRecibida(nuevaCantidadRecibida);  // ← Guarda empaques
            detalleAbast.setCantidadRestante(nuevaCantidadRestante);   // ← Guarda empaques
            
            // ⚠️ cantidadComprometida NO se modifica, ya se guardó al aprobar
            
            if (nuevaCantidadRestante <= 0) {
                detalleAbast.setEstadoDetalle("COMPLETADO");
                detalleAbast.setCantidadRestante(0);
            } else {
                detalleAbast.setEstadoDetalle("PARCIAL");
            }
            detalleAbastecimientoService.save(detalleAbast);
            
            // ============================================================
            // 🔥 CALCULAR TOTAL DE UNIDADES PARA EL LOTE Y STOCK
            // ============================================================
            // totalUnidades = 6 empaques × 10 unidades/empaque = 60 unidades
            Integer totalUnidades = cantidadEmpaquesRecibidos * unidadesPorEmpaque;
            
            // ============================================================
            // ✅ CREAR LOTE (en UNIDADES)
            // ============================================================
            Lote lote = new Lote();
            lote.setNumeroLote(numeroLote);
            lote.setInsumo(insumo);
            lote.setDetalleActaEntrega(detalleActa);
            lote.setFechaIngreso(LocalDate.now());
            lote.setFechaVencimiento(fechaVencimiento);
            lote.setCantidadInicial(totalUnidades);              // ← 60 unidades
            lote.setCantidadDisponible(totalUnidades);           // ← 60 unidades
            lote.setCantidadEmpaque(cantidadEmpaquesRecibidos);  // ← 6 empaques
            lote.setUnidadPorEmpaque(unidadesPorEmpaque);        // ← 10 unidades/empaque
            lote.setEstadoLote("DISPONIBLE");
            
            if (observaciones != null && !observaciones.isEmpty()) {
                lote.setObservaciones(observaciones);
            }
            if (idEncargado != null) {
                lote.setUsuRegLot(idEncargado.intValue());
            }
            
            lote = loteService.save(lote);
            
            // ============================================================
            // ✅ ACTUALIZAR STOCK DEL INSUMO (sumar el total de unidades)
            // ============================================================
            int nuevoStock = insumo.getStockTotal() + totalUnidades;
            insumo.setStockTotal(nuevoStock);
            insumo.setUltimaActualizacion(LocalDate.now());
            insumoService.guardar(insumo);
            
            // ============================================================
            // ✅ VERIFICAR SI EL ABASTECIMIENTO ESTÁ COMPLETO
            // ============================================================
            Abastecimiento abastecimiento = detalleAbast.getAbastecimiento();
            boolean todosCompletados = abastecimiento.getDetalles().stream()
                .allMatch(d -> d.getCantidadRestante() <= 0);
            
            if (todosCompletados) {
                abastecimiento.setEstadoAbastecimiento("COMPLETADO");
                abastecimientoService.save(abastecimiento);
            }
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Lote creado exitosamente",
                "idLote", lote.getIdLote(),
                "numeroLote", lote.getNumeroLote(),
                "cantidadEmpaquesRecibidos", cantidadEmpaquesRecibidos,
                "unidadesPorEmpaque", unidadesPorEmpaque,
                "totalUnidades", totalUnidades,
                "empaquesRecibidosAcumulados", detalleAbast.getCantidadRecibida(),
                "empaquesRestantes", detalleAbast.getCantidadRestante(),
                "stockActualInsumo", insumo.getStockTotal(),
                "estadoAbastecimiento", abastecimiento.getEstadoAbastecimiento()
            ));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Formato de número inválido: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ================================================================
    // 2. RECIBIR ABASTECIMIENTO (CREAR MÚLTIPLES LOTES)
    // ================================================================
    @PutMapping("/abastecimiento/{idAbastecimiento}/recibir")
    public ResponseEntity<?> recibirAbastecimiento(@PathVariable Long idAbastecimiento,
                                                    @RequestBody Map<String, Object> body) {
        try {
            if (!body.containsKey("idEncargado") || body.get("idEncargado") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El idEncargado es requerido"));
            }
            
            Long idEncargado = Long.valueOf(body.get("idEncargado").toString());
            String observacion = body.containsKey("observacion") ? 
                (String) body.get("observacion") : null;
            
            Abastecimiento abastecimiento = abastecimientoService.findById(idAbastecimiento)
                .orElseThrow(() -> new RuntimeException("Abastecimiento no encontrado con ID: " + idAbastecimiento));
            
            if (!"EN_PROCESO".equals(abastecimiento.getEstadoAbastecimiento()) && 
                !"PARCIAL".equals(abastecimiento.getEstadoAbastecimiento())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Este abastecimiento no está en estado válido para recibir. Estado actual: " + 
                    abastecimiento.getEstadoAbastecimiento()
                ));
            }
            
            List<DetalleAbastecimiento> detallesPendientes = abastecimiento.getDetalles().stream()
                .filter(d -> d.getCantidadRestante() > 0)
                .collect(Collectors.toList());
            
            if (detallesPendientes.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No hay insumos pendientes por recibir en este abastecimiento"
                ));
            }
            
            List<Map<String, Object>> lotesCreados = new ArrayList<>();
            
            for (DetalleAbastecimiento detalle : detallesPendientes) {
                Insumo insumo = detalle.getDetallePedido().getInsumo();
                
                // Obtener factores de conversión desde el body
                Integer cantidadEmpaquesRecibidos = 1;
                Integer unidadesPorEmpaque = 1;
                
                String empaqueKey = "cantidadEmpaques_" + detalle.getIdDetalleAbastecimiento();
                String unidadKey = "unidadesPorEmpaque_" + detalle.getIdDetalleAbastecimiento();
                
                if (body.containsKey(empaqueKey) && body.get(empaqueKey) != null) {
                    cantidadEmpaquesRecibidos = Integer.valueOf(body.get(empaqueKey).toString());
                }
                if (body.containsKey(unidadKey) && body.get(unidadKey) != null) {
                    unidadesPorEmpaque = Integer.valueOf(body.get(unidadKey).toString());
                }
                
                // 🔥 Validar que no exceda los empaques pendientes
                if (cantidadEmpaquesRecibidos > detalle.getCantidadRestante()) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "La cantidad de empaques (" + cantidadEmpaquesRecibidos + 
                        ") excede lo pendiente (" + detalle.getCantidadRestante() + 
                        ") para el insumo: " + insumo.getNombreInsumo()
                    ));
                }
                
                // ============================================================
                // ✅ ACTUALIZAR DetalleAbastecimiento (TODO EN EMPAQUES)
                // ============================================================
                int nuevaCantidadRecibida = detalle.getCantidadRecibida() + cantidadEmpaquesRecibidos;
                int nuevaCantidadRestante = detalle.getCantidadRestante() - cantidadEmpaquesRecibidos;
                
                detalle.setCantidadRecibida(nuevaCantidadRecibida);  // ← Guarda empaques
                detalle.setCantidadRestante(nuevaCantidadRestante);   // ← Guarda empaques
                
                if (nuevaCantidadRestante <= 0) {
                    detalle.setEstadoDetalle("COMPLETADO");
                    detalle.setCantidadRestante(0);
                } else {
                    detalle.setEstadoDetalle("PARCIAL");
                }
                detalleAbastecimientoService.save(detalle);
                
                // ============================================================
                // 🔥 CALCULAR TOTAL DE UNIDADES PARA EL LOTE Y STOCK
                // ============================================================
                Integer totalUnidades = cantidadEmpaquesRecibidos * unidadesPorEmpaque;
                
                // ============================================================
                // ✅ CREAR LOTE
                // ============================================================
                Lote lote = new Lote();
                lote.setInsumo(insumo);
                lote.setNumeroLote(generarNumeroLote(insumo));
                lote.setFechaVencimiento(LocalDate.now().plusMonths(6));
                lote.setCantidadInicial(totalUnidades);
                lote.setCantidadDisponible(totalUnidades);
                lote.setCantidadEmpaque(cantidadEmpaquesRecibidos);
                lote.setUnidadPorEmpaque(unidadesPorEmpaque);
                lote.setEstadoLote("DISPONIBLE");
                lote.setObservaciones(observacion);
                lote.setUsuRegLot(idEncargado.intValue());
                lote = loteService.save(lote);
                
                // ============================================================
                // ✅ ACTUALIZAR STOCK DEL INSUMO
                // ============================================================
                Integer stockActual = insumo.getStockTotal() != null ? insumo.getStockTotal() : 0;
                insumo.setStockTotal(stockActual + totalUnidades);
                insumo.setUltimaActualizacion(LocalDate.now());
                insumoService.guardar(insumo);
                
                Map<String, Object> loteInfo = new HashMap<>();
                loteInfo.put("idLote", lote.getIdLote());
                loteInfo.put("numeroLote", lote.getNumeroLote());
                loteInfo.put("insumo", insumo.getNombreInsumo());
                loteInfo.put("cantidadEmpaquesRecibidos", cantidadEmpaquesRecibidos);
                loteInfo.put("unidadesPorEmpaque", unidadesPorEmpaque);
                loteInfo.put("totalUnidades", totalUnidades);
                loteInfo.put("fechaVencimiento", lote.getFechaVencimiento());
                loteInfo.put("stockActualInsumo", insumo.getStockTotal());
                lotesCreados.add(loteInfo);
            }
            
            boolean todosCompletados = abastecimiento.getDetalles().stream()
                .allMatch(d -> d.getCantidadRestante() <= 0);
            
            if (todosCompletados) {
                abastecimiento.setEstadoAbastecimiento("COMPLETADO");
            } else {
                abastecimiento.setEstadoAbastecimiento("PARCIAL");
            }
            abastecimientoService.save(abastecimiento);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "✅ Abastecimiento recibido correctamente",
                "lotesCreados", lotesCreados,
                "cantidadLotes", lotesCreados.size(),
                "estadoAbastecimiento", abastecimiento.getEstadoAbastecimiento()
            ));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Formato de número inválido: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ================================================================
    // 3. OBTENER ACTAS PENDIENTES
    // ================================================================
    @GetMapping("/pendientes")
    public ResponseEntity<?> getActasPendientes() {
        try {
            List<ActaEntrega> actas = actaEntregaService.findAll();
            
            List<Map<String, Object>> actasPendientes = new ArrayList<>();
            
            for (ActaEntrega acta : actas) {
                // Filtrar detalles sin lote Y con cantidad > 0
                List<DetalleActaEntrega> detallesSinLote = acta.getDetalles().stream()
                    .filter(d -> d.getLote() == null)
                    .filter(d -> d.getCantidadEntregada() != null && d.getCantidadEntregada() > 0)
                    .collect(Collectors.toList());
                
                if (!detallesSinLote.isEmpty()) {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("idActa", acta.getIdActaEntrega());
                    dto.put("numeroActa", acta.getNumeroActa());
                    dto.put("fechaEntrega", acta.getFechaEntrega());
                    dto.put("codigoAbastecimiento", acta.getAbastecimiento().getCodigoAbastecimiento());
                    
                    List<Map<String, Object>> detallesDTO = detallesSinLote.stream().map(d -> {
                        DetalleAbastecimiento detalleAbast = d.getDetalleAbastecimiento();
                        Map<String, Object> detDTO = new HashMap<>();
                        detDTO.put("idDetalleActa", d.getIdDetalleActa());
                        detDTO.put("nombreInsumo", detalleAbast.getDetallePedido().getInsumo().getNombreInsumo());
                        detDTO.put("cantidadEntregada", d.getCantidadEntregada());
                        detDTO.put("unidadMedida", detalleAbast.getDetallePedido().getUnidadMedida().getNombreUnidad());
                        // 🔥 Mostrar cuántos empaques quedan pendientes en el abastecimiento
                        detDTO.put("empaquesPendientes", detalleAbast.getCantidadRestante());
                        return detDTO;
                    }).collect(Collectors.toList());
                    
                    dto.put("detallesPendientes", detallesDTO);
                    actasPendientes.add(dto);
                }
            }
            
            return ResponseEntity.ok(actasPendientes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ================================================================
    // 4. OBTENER DETALLES SIN LOTE DE UN ACTA ESPECÍFICA
    // ================================================================
    @GetMapping("/{idActa}/detalles-sin-lote")
    public ResponseEntity<?> getDetallesSinLote(@PathVariable Long idActa) {
        try {
            ActaEntrega acta = actaEntregaService.findById(idActa)
                .orElseThrow(() -> new RuntimeException("Acta no encontrada"));
            
            List<Map<String, Object>> detallesSinLote = acta.getDetalles().stream()
                .filter(d -> d.getLote() == null)
                .filter(d -> d.getCantidadEntregada() != null && d.getCantidadEntregada() > 0)
                .map(d -> {
                    DetalleAbastecimiento detalleAbast = d.getDetalleAbastecimiento();
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("idDetalleActa", d.getIdDetalleActa());
                    dto.put("nombreInsumo", detalleAbast.getDetallePedido().getInsumo().getNombreInsumo());
                    dto.put("cantidadEntregada", d.getCantidadEntregada());
                    dto.put("unidadMedida", detalleAbast.getDetallePedido().getUnidadMedida().getNombreUnidad());
                    // 🔥 Mostrar cuántos empaques quedan pendientes
                    dto.put("empaquesPendientes", detalleAbast.getCantidadRestante());
                    return dto;
                }).collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("idActa", acta.getIdActaEntrega());
            response.put("numeroActa", acta.getNumeroActa());
            response.put("fechaEntrega", acta.getFechaEntrega());
            response.put("codigoAbastecimiento", acta.getAbastecimiento().getCodigoAbastecimiento());
            response.put("detalles", detallesSinLote);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ================================================================
    // 5. OBTENER TODOS LOS LOTES (PARA HISTORIAL)
    // ================================================================
    @GetMapping("/lotes/todos")
    public ResponseEntity<?> obtenerTodosLosLotes() {
        try {
            List<Lote> lotes = loteService.findAll();
            
            List<Map<String, Object>> response = lotes.stream().map(lote -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("idLote", lote.getIdLote());
                dto.put("numeroLote", lote.getNumeroLote());
                dto.put("fechaIngreso", lote.getFechaIngreso());
                dto.put("fechaVencimiento", lote.getFechaVencimiento());
                dto.put("nombreInsumo", lote.getInsumo().getNombreInsumo());
                dto.put("cantidadInicial", lote.getCantidadInicial());
                dto.put("cantidadDisponible", lote.getCantidadDisponible());
                dto.put("cantidadEmpaque", lote.getCantidadEmpaque());
                dto.put("unidadPorEmpaque", lote.getUnidadPorEmpaque());
                dto.put("estadoLote", lote.getEstadoLote());
                dto.put("observaciones", lote.getObservaciones());
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ================================================================
    // 6. OBTENER LOTES POR RANGO DE FECHAS
    // ================================================================
    // @GetMapping("/lotes/historial")
    // public ResponseEntity<?> obtenerHistorialPorFechas(@RequestParam String desde, @RequestParam String hasta) {
    //     try {
    //         LocalDate fechaDesde = LocalDate.parse(desde);
    //         LocalDate fechaHasta = LocalDate.parse(hasta);
            
    //         // Asumiendo que tienes un método en tu servicio
    //         List<Lote> lotes = loteService.findByFechaIngresoBetween(fechaDesde, fechaHasta);
            
    //         List<Map<String, Object>> response = lotes.stream().map(lote -> {
    //             Map<String, Object> dto = new HashMap<>();
    //             dto.put("idLote", lote.getIdLote());
    //             dto.put("numeroLote", lote.getNumeroLote());
    //             dto.put("fechaIngreso", lote.getFechaIngreso());
    //             dto.put("fechaVencimiento", lote.getFechaVencimiento());
    //             dto.put("nombreInsumo", lote.getInsumo().getNombreInsumo());
    //             dto.put("cantidadInicial", lote.getCantidadInicial());
    //             dto.put("cantidadDisponible", lote.getCantidadDisponible());
    //             dto.put("cantidadEmpaque", lote.getCantidadEmpaque());
    //             dto.put("unidadPorEmpaque", lote.getUnidadPorEmpaque());
    //             dto.put("estadoLote", lote.getEstadoLote());
    //             return dto;
    //         }).collect(Collectors.toList());
            
    //         return ResponseEntity.ok(response);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
    //     }
    // }

    // ================================================================
    // 7. MÉTODOS AUXILIARES
    // ================================================================
    
    private String generarNumeroLote(Insumo insumo) {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String secuencia = String.format("%04d", (int)(Math.random() * 10000));
        String idInsumo = String.format("%03d", insumo.getIdInsumo());
        return "LOT-" + fecha + "-" + idInsumo + "-" + secuencia;
    }
}