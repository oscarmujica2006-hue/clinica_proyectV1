// package com.clinica_odontologica.V1.Controller;

// import com.clinica_odontologica.V1.Model.Entity.*;
// import com.clinica_odontologica.V1.Model.Dao.EncargadoInsumoRepository;
// import com.clinica_odontologica.V1.Model.Dao.DirectorRepository;
// import com.clinica_odontologica.V1.Service.*;
// import org.springframework.beans.factory.annotation.Autowired;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import java.time.LocalDate;
// import java.util.*;
// import java.util.stream.Collectors;

// @RestController
// @RequestMapping("/api/pedidos")
// public class PedidoController {

//     @Autowired
//     private PedidoService pedidoService;
    
//     @Autowired
//     private DetallePedidoService detallePedidoService;
    
//     @Autowired
//     private InsumoService insumoService;
    
//     @Autowired
//     private EncargadoInsumoRepository encargadoInsumoRepository;

//     @Autowired
//     private UnidadMedidaService unidadMedidaService;

//     @Autowired
//     private UsuarioService usuarioService;

//     @Autowired
//     private PedidoLoteService pedidoLoteService;
    
//     @Autowired
//     private DetPedidoLoteService detPedidoLoteService;
    
//     @Autowired
//     private DirectorRepository directorRepository;

//     // 1. Obtener todos los insumos (para el select)
//     @GetMapping("/insumos")
//     public ResponseEntity<?> getInsumos() {
//         try {
//             List<Insumo> insumos = insumoService.obtenerTodos();
            
//             List<Map<String, Object>> insumosDTO = insumos.stream().map(insumo -> {
//                 Map<String, Object> dto = new HashMap<>();
//                 dto.put("idInsumo", insumo.getIdInsumo());
//                 dto.put("nombreInsumo", insumo.getNombreInsumo());
//                 dto.put("unidadBase", insumo.getUnidadBase());
//                 dto.put("concentracion", insumo.getConcentracion());
//                 dto.put("stockTotal", insumo.getStockTotal());
//                 return dto;
//             }).collect(Collectors.toList());
            
//             return ResponseEntity.ok(insumosDTO);
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//         }
//     }

//     // 2. Obtener pedidos por encargado
//     @GetMapping("/encargado/{idEncargado}")
//     public ResponseEntity<?> getPedidosByEncargado(@PathVariable Long idEncargado) {
//         try {
//             List<Pedido> pedidos = pedidoService.findByEncargadoId(idEncargado);
            
//             List<Map<String, Object>> pedidosDTO = pedidos.stream().map(p -> {
//                 Map<String, Object> dto = new HashMap<>();
//                 dto.put("idPedido", p.getIdPedido());
//                 dto.put("fechaPedido", p.getFechaPedido());
//                 dto.put("estadoPedido", p.getEstadoPedido());
                
//                 int cantidadInsumos = 0;
//                 int totalUnidades = 0;
//                 if (p.getDetalles() != null) {
//                     cantidadInsumos = p.getDetalles().size();
//                     totalUnidades = p.getDetalles().stream()
//                         .mapToInt(DetallePedido::getCantidadPedida)
//                         .sum();
//                 }
//                 dto.put("cantidadInsumos", cantidadInsumos);
//                 dto.put("totalUnidades", totalUnidades);
                
//                 return dto;
//             }).collect(Collectors.toList());
            
//             return ResponseEntity.ok(pedidosDTO);
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//         }
//     }

//     // 3. Crear nuevo pedido
//     @PostMapping
//     public ResponseEntity<?> crearPedido(@RequestBody Map<String, Object> body) {
//         try {
//             if (!body.containsKey("idEncargado") || body.get("idEncargado") == null) {
//                 return ResponseEntity.badRequest().body(Map.of("error", "El idEncargado es requerido"));
//             }
            
//             Long idEncargado = Long.valueOf(body.get("idEncargado").toString());
            
//             if (!body.containsKey("insumos") || body.get("insumos") == null) {
//                 return ResponseEntity.badRequest().body(Map.of("error", "La lista de insumos es requerida"));
//             }
            
//             @SuppressWarnings("unchecked")
//             List<Map<String, Object>> insumos = (List<Map<String, Object>>) body.get("insumos");
            
//             if (insumos.isEmpty()) {
//                 return ResponseEntity.badRequest().body(Map.of("error", "Debe incluir al menos un insumo"));
//             }
            
//             EncargadoInsumo encargado = encargadoInsumoRepository.findById(idEncargado)
//                 .orElseThrow(() -> new RuntimeException("Encargado no encontrado"));
            
//             Pedido pedido = new Pedido();
//             pedido.setEncargadoInsumo(encargado);
//             pedido.setFechaPedido(LocalDate.now());
//             pedido.setEstadoPedido("PENDIENTE");
//             pedido = pedidoService.guardar(pedido);
            
//             for (Map<String, Object> item : insumos) {
//                 if (!item.containsKey("idInsumo") || !item.containsKey("cantidadPedida")) {
//                     return ResponseEntity.badRequest().body(Map.of("error", "Cada insumo debe tener idInsumo y cantidadPedida"));
//                 }
                
//                 if (!item.containsKey("idUnidadMedida")) {
//                     return ResponseEntity.badRequest().body(Map.of("error", "Cada insumo debe tener idUnidadMedida"));
//                 }
                
//                 Long idInsumo = Long.valueOf(item.get("idInsumo").toString());
//                 Long idUnidadMedida = Long.valueOf(item.get("idUnidadMedida").toString());
//                 Integer cantidadPedida;
                
//                 Object cantidadObj = item.get("cantidadPedida");
//                 if (cantidadObj instanceof Integer) {
//                     cantidadPedida = (Integer) cantidadObj;
//                 } else if (cantidadObj instanceof Long) {
//                     cantidadPedida = ((Long) cantidadObj).intValue();
//                 } else if (cantidadObj instanceof Double) {
//                     cantidadPedida = ((Double) cantidadObj).intValue();
//                 } else {
//                     cantidadPedida = Integer.parseInt(cantidadObj.toString());
//                 }
                
//                 Insumo insumo = insumoService.obtenerPorId(idInsumo)
//                     .orElseThrow(() -> new RuntimeException("Insumo no encontrado: " + idInsumo));
                
//                 UnidadMedida unidadMedida = unidadMedidaService.findById(idUnidadMedida)
//                     .orElseThrow(() -> new RuntimeException("Unidad de medida no encontrada con ID: " + idUnidadMedida));
                
//                 DetallePedido detalle = new DetallePedido();
//                 detalle.setPedido(pedido);
//                 detalle.setInsumo(insumo);
//                 detalle.setUnidadMedida(unidadMedida);
//                 detalle.setCantidadPedida(cantidadPedida);
//                 detalle.setEstadoDetPedido("PENDIENTE");
                
//                 detallePedidoService.guardar(detalle);
//             }
            
//             return ResponseEntity.ok(Map.of(
//                 "mensaje", "Pedido creado exitosamente",
//                 "idPedido", pedido.getIdPedido()
//             ));
            
//         } catch (NumberFormatException e) {
//             return ResponseEntity.badRequest().body(Map.of("error", "Formato de número inválido: " + e.getMessage()));
//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//         }
//     }

//     // 4. Obtener detalle de un pedido
//     @GetMapping("/{idPedido}/detalle")
//     public ResponseEntity<?> getDetallePedido(@PathVariable Long idPedido) {
//         try {
//             Pedido pedido = pedidoService.obtenerPorId(idPedido)
//                 .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
//             Map<String, Object> detalle = new HashMap<>();
//             detalle.put("idPedido", pedido.getIdPedido());
//             detalle.put("fechaPedido", pedido.getFechaPedido());
//             detalle.put("estadoPedido", pedido.getEstadoPedido());
            
//             if (pedido.getEncargadoInsumo() != null && 
//                 pedido.getEncargadoInsumo().getUsuario() != null &&
//                 pedido.getEncargadoInsumo().getUsuario().getPersona() != null) {
//                 detalle.put("encargadoNombre", 
//                     pedido.getEncargadoInsumo().getUsuario().getPersona().getNombre());
//             }
            
//             List<Map<String, Object>> insumos = new ArrayList<>();
//             if (pedido.getDetalles() != null) {
//                 for (DetallePedido det : pedido.getDetalles()) {
//                     Map<String, Object> insumoDTO = new HashMap<>();
//                     insumoDTO.put("idInsumo", det.getInsumo().getIdInsumo());
//                     insumoDTO.put("nombreInsumo", det.getInsumo().getNombreInsumo());
//                     insumoDTO.put("cantidadPedida", det.getCantidadPedida());
//                     insumoDTO.put("estadoDetPedido", det.getEstadoDetPedido());
//                     insumoDTO.put("unidadMedida", det.getInsumo().getUnidadBase());
//                     insumos.add(insumoDTO);
//                 }
//             }
//             detalle.put("insumos", insumos);
            
//             return ResponseEntity.ok(detalle);
            
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//         }
//     }

//     // 5. Recibir pedido (actualiza stock)
//     @PutMapping("/{idPedido}/recibir")
//     public ResponseEntity<?> recibirPedido(@PathVariable Long idPedido,
//                                            @RequestBody Map<String, Object> body) {
//         try {
//             Pedido pedido = pedidoService.obtenerPorId(idPedido)
//                 .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
//             if (!"APROBADO".equals(pedido.getEstadoPedido())) {
//                 return ResponseEntity.badRequest().body(Map.of("error", 
//                     "Solo se pueden recibir pedidos aprobados. Estado actual: " + pedido.getEstadoPedido()));
//             }
            
//             for (DetallePedido detalle : pedido.getDetalles()) {
//                 Insumo insumo = detalle.getInsumo();
//                 int nuevoStock = insumo.getStockTotal() + detalle.getCantidadPedida();
//                 insumo.setStockTotal(nuevoStock);
//                 insumo.setUltimaActualizacion(LocalDate.now());
//                 insumoService.guardar(insumo);
                
//                 detalle.setEstadoDetPedido("RECIBIDO");
//                 detallePedidoService.guardar(detalle);
//             }
            
//             pedido.setEstadoPedido("RECIBIDO");
//             pedidoService.guardar(pedido);
            
//             return ResponseEntity.ok(Map.of("mensaje", "Pedido recibido y stock actualizado correctamente"));
            
//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//         }
//     }

//     // 6. Cancelar pedido
//     @PutMapping("/{idPedido}/cancelar")
//     public ResponseEntity<?> cancelarPedido(@PathVariable Long idPedido,
//                                             @RequestBody Map<String, Object> body) {
//         try {
//             Pedido pedido = pedidoService.obtenerPorId(idPedido)
//                 .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
//             if (!"PENDIENTE".equals(pedido.getEstadoPedido())) {
//                 return ResponseEntity.badRequest().body(Map.of("error", 
//                     "Solo se pueden cancelar pedidos pendientes. Estado actual: " + pedido.getEstadoPedido()));
//             }
            
//             for (DetallePedido detalle : pedido.getDetalles()) {
//                 detalle.setEstadoDetPedido("CANCELADO");
//                 detallePedidoService.guardar(detalle);
//             }
            
//             pedido.setEstadoPedido("CANCELADO");
//             pedidoService.guardar(pedido);
            
//             return ResponseEntity.ok(Map.of("mensaje", "Pedido cancelado correctamente"));
            
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//         }
//     }
    
//     // 7. Obtener solicitudes para el director (todos los pedidos PENDIENTE)
//     @GetMapping("/solicitudes")
//     public ResponseEntity<?> getSolicitudes() {
//         try {
//             List<Pedido> solicitudes = pedidoService.findByEstado("PENDIENTE");
            
//             List<Map<String, Object>> solicitudesDTO = solicitudes.stream().map(p -> {
//                 Map<String, Object> dto = new HashMap<>();
//                 dto.put("idPedido", p.getIdPedido());
//                 dto.put("fechaPedido", p.getFechaPedido());
//                 dto.put("estadoPedido", p.getEstadoPedido());
                
//                 String encargadoNombre = "N/A";
//                 if (p.getEncargadoInsumo() != null && 
//                     p.getEncargadoInsumo().getUsuario() != null &&
//                     p.getEncargadoInsumo().getUsuario().getPersona() != null) {
//                     encargadoNombre = p.getEncargadoInsumo().getUsuario().getPersona().getNombre();
//                 }
//                 dto.put("encargadoNombre", encargadoNombre);
                
//                 int cantidadInsumos = 0;
//                 int totalUnidades = 0;
//                 if (p.getDetalles() != null) {
//                     cantidadInsumos = p.getDetalles().size();
//                     totalUnidades = p.getDetalles().stream()
//                         .mapToInt(DetallePedido::getCantidadPedida)
//                         .sum();
//                 }
//                 dto.put("cantidadInsumos", cantidadInsumos);
//                 dto.put("totalUnidades", totalUnidades);
                
//                 return dto;
//             }).collect(Collectors.toList());
            
//             return ResponseEntity.ok(solicitudesDTO);
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//         }
//     }

//     // 8. Abastecer pedido (cambiar estado y crear lotes)
//     @PutMapping("/{idPedido}/abastecer")
//     public ResponseEntity<?> abastecerPedido(@PathVariable Long idPedido,
//                                             @RequestBody Map<String, Object> body) {
//         try {
//             Long idUsuario = Long.valueOf(body.get("idUsuario").toString());
//             @SuppressWarnings("unchecked")
//             List<Map<String, Object>> insumosAbastecidos = (List<Map<String, Object>>) body.get("insumos");
            
//             Pedido pedido = pedidoService.obtenerPorId(idPedido)
//                 .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
//             if (!"PENDIENTE".equals(pedido.getEstadoPedido())) {
//                 return ResponseEntity.badRequest().body(Map.of("error", 
//                     "Solo se pueden abastecer pedidos pendientes"));
//             }
            
//             Usuario abastecedor = usuarioService.obtenerPorId(idUsuario)
//                 .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
            
//             // ✅ CORREGIDO: Obtener el EncargadoInsumo del pedido original
//             EncargadoInsumo encargadoInsumo = pedido.getEncargadoInsumo();
//             if (encargadoInsumo == null) {
//                 return ResponseEntity.badRequest().body(Map.of("error", 
//                     "El pedido no tiene un encargado de insumo asociado"));
//             }
            
//             // Crear un lote principal para el pedido
//             PedidoLote pedidoLote = new PedidoLote();
//             pedidoLote.setPedido(pedido);
//             pedidoLote.setAbastecedor(abastecedor);
//             pedidoLote.setNumeroLote(generarNumeroLote());
//             pedidoLote.setFechaSoliEnviada(LocalDate.now());
//             pedidoLote.setEstadoLote("ENVIADO");
//             pedidoLote = pedidoLoteService.save(pedidoLote);
            
//             // Crear detalles del lote para cada insumo abastecido
//             for (Map<String, Object> item : insumosAbastecidos) {
//                 Long idInsumo = Long.valueOf(item.get("idInsumo").toString());
//                 Integer cantidadAbastecida = ((Number) item.get("cantidadAbastecida")).intValue();
//                 String fechaVencimientoStr = item.get("fechaVencimiento").toString();
//                 LocalDate fechaVencimiento = LocalDate.parse(fechaVencimientoStr);
                
//                 Insumo insumo = insumoService.obtenerPorId(idInsumo)
//                     .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));
                
//                 DetallePedido detallePedido = detallePedidoService.findByPedidoAndInsumo(pedido, insumo)
//                     .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));
                
//                 DetPedidoLote detLote = new DetPedidoLote();
//                 detLote.setPedidoLote(pedidoLote);
//                 detLote.setInsumo(insumo);
//                 detLote.setUnidadMedida(detallePedido.getUnidadMedida());
//                 detLote.setDetallePedido(detallePedido);
//                 detLote.setCantidadAbastecida(cantidadAbastecida);
//                 detLote.setSaldo_actual(cantidadAbastecida);
//                 detLote.setFechaVencimiento(fechaVencimiento);
//                 detLote.setEstadoDetPedidoLote("ABASTECIDO");
//                 detLote.setFechaRecepcion(LocalDate.now());
//                 detLote.setIdRecepcion(encargadoInsumo);
//                 detLote.setNombreDetLote(pedidoLote.getNumeroLote() + "-" + insumo.getNombreInsumo());
//                 detPedidoLoteService.save(detLote);
//             }
            
//             pedido.setEstadoPedido("ABASTECIDO");
//             pedidoService.guardar(pedido);
            
//             return ResponseEntity.ok(Map.of("mensaje", "Pedido abastecido correctamente"));
            
//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//         }
//     }

//     // Método auxiliar para generar número de lote
//     private String generarNumeroLote() {
//         return "LOT-" + System.currentTimeMillis();
//     }

//     // 9. Obtener lotes por abastecedor
//     @GetMapping("/lotes/abastecedor/{idUsuario}")
//     public ResponseEntity<?> getLotesByAbastecedor(@PathVariable Long idUsuario) {
//         try {
//             List<PedidoLote> lotes = pedidoLoteService.findByAbastecedorId(idUsuario);
            
//             List<Map<String, Object>> lotesDTO = lotes.stream().map(lote -> {
//                 Map<String, Object> dto = new HashMap<>();
//                 dto.put("idPedidoLote", lote.getIdPedidoLote());
//                 dto.put("numeroLote", lote.getNumeroLote());
//                 dto.put("fechaSoliEnviada", lote.getFechaSoliEnviada());
//                 dto.put("estadoLote", lote.getEstadoLote());
//                 dto.put("idPedido", lote.getPedido().getIdPedido());
//                 return dto;
//             }).collect(Collectors.toList());
            
//             return ResponseEntity.ok(lotesDTO);
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//         }
//     }

//     // 10. Obtener pedidos pendientes para abastecer
//     @GetMapping("/pendientes")
//     public ResponseEntity<?> getPedidosPendientes() {
//         try {
//             List<Pedido> pedidos = pedidoService.findByEstado("PENDIENTE");
            
//             List<Map<String, Object>> pedidosDTO = pedidos.stream().map(p -> {
//                 Map<String, Object> dto = new HashMap<>();
//                 dto.put("idPedido", p.getIdPedido());
//                 dto.put("fechaPedido", p.getFechaPedido());
                
//                 String encargadoNombre = "N/A";
//                 if (p.getEncargadoInsumo() != null && 
//                     p.getEncargadoInsumo().getUsuario() != null &&
//                     p.getEncargadoInsumo().getUsuario().getPersona() != null) {
//                     encargadoNombre = p.getEncargadoInsumo().getUsuario().getPersona().getNombre();
//                 }
//                 dto.put("encargadoNombre", encargadoNombre);
                
//                 return dto;
//             }).collect(Collectors.toList());
            
//             return ResponseEntity.ok(pedidosDTO);
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//         }
//     }
// }