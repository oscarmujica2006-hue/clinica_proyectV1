package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.*;
import proyect_final.clinica.Model.Dao.EncargadoInsumoRepository;
import proyect_final.clinica.Model.Dao.DirectorRepository;
import proyect_final.clinica.Service.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private DetallePedidoService detallePedidoService;
    
    @Autowired
    private InsumoService insumoService;
    
    @Autowired
    private EncargadoInsumoRepository encargadoInsumoRepository;

    @Autowired
    private UnidadMedidaService unidadMedidaService;

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
    private DirectorRepository directorRepository;

    // ==================== ENDPOINTS DE PEDIDOS ====================
    
    // 1. Obtener todos los insumos (para el select)
    @GetMapping("/insumos")
    public ResponseEntity<?> getInsumos() {
        try {
            List<Insumo> insumos = insumoService.obtenerTodos();
            
            List<Map<String, Object>> insumosDTO = insumos.stream().map(insumo -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("idInsumo", insumo.getIdInsumo());
                dto.put("nombreInsumo", insumo.getNombreInsumo());
                dto.put("unidadBase", insumo.getUnidadBase());
                dto.put("concentracion", insumo.getConcentracion());
                dto.put("stockTotal", insumo.getStockTotal());
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(insumosDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. Obtener pedidos por encargado
    @GetMapping("/encargado/{idEncargado}")
    public ResponseEntity<?> getPedidosByEncargado(@PathVariable Long idEncargado) {
        try {
            List<Pedido> pedidos = pedidoService.findByEncargadoId(idEncargado);
            
            List<Map<String, Object>> pedidosDTO = pedidos.stream().map(p -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("idPedido", p.getIdPedido());
                dto.put("fechaPedido", p.getFechaPedido());
                dto.put("estadoPedido", p.getEstadoPedido());
                
                int cantidadInsumos = 0;
                int totalUnidades = 0;
                if (p.getDetalles() != null) {
                    cantidadInsumos = p.getDetalles().size();
                    totalUnidades = p.getDetalles().stream()
                        .mapToInt(DetallePedido::getCantidadPedida)
                        .sum();
                }
                dto.put("cantidadInsumos", cantidadInsumos);
                dto.put("totalUnidades", totalUnidades);
                
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(pedidosDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. Crear nuevo pedido
    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody Map<String, Object> body) {
        try {
            if (!body.containsKey("idEncargado") || body.get("idEncargado") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El idEncargado es requerido"));
            }
            
            Long idEncargado = Long.valueOf(body.get("idEncargado").toString());
            
            if (!body.containsKey("insumos") || body.get("insumos") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "La lista de insumos es requerida"));
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> insumos = (List<Map<String, Object>>) body.get("insumos");
            
            if (insumos.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe incluir al menos un insumo"));
            }
            
            EncargadoInsumo encargado = encargadoInsumoRepository.findById(idEncargado)
                .orElseThrow(() -> new RuntimeException("Encargado no encontrado"));
            
            Pedido pedido = new Pedido();
            pedido.setEncargadoInsumo(encargado);
            pedido.setFechaPedido(LocalDate.now());
            pedido.setEstadoPedido("PENDIENTE");
            pedido = pedidoService.guardar(pedido);
            
            for (Map<String, Object> item : insumos) {
                if (!item.containsKey("idInsumo") || !item.containsKey("cantidadPedida")) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Cada insumo debe tener idInsumo y cantidadPedida"));
                }
                
                if (!item.containsKey("idUnidadMedida")) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Cada insumo debe tener idUnidadMedida"));
                }
                
                Long idInsumo = Long.valueOf(item.get("idInsumo").toString());
                Long idUnidadMedida = Long.valueOf(item.get("idUnidadMedida").toString());
                Integer cantidadPedida;
                
                Object cantidadObj = item.get("cantidadPedida");
                if (cantidadObj instanceof Integer) {
                    cantidadPedida = (Integer) cantidadObj;
                } else if (cantidadObj instanceof Long) {
                    cantidadPedida = ((Long) cantidadObj).intValue();
                } else if (cantidadObj instanceof Double) {
                    cantidadPedida = ((Double) cantidadObj).intValue();
                } else {
                    cantidadPedida = Integer.parseInt(cantidadObj.toString());
                }
                
                Insumo insumo = insumoService.obtenerPorId(idInsumo)
                    .orElseThrow(() -> new RuntimeException("Insumo no encontrado: " + idInsumo));
                
                UnidadMedida unidadMedida = unidadMedidaService.findById(idUnidadMedida)
                    .orElseThrow(() -> new RuntimeException("Unidad de medida no encontrada con ID: " + idUnidadMedida));
                
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedido);
                detalle.setInsumo(insumo);
                detalle.setUnidadMedida(unidadMedida);
                detalle.setCantidadPedida(cantidadPedida);
                detalle.setEstadoDetPedido("PENDIENTE");
                
                detallePedidoService.guardar(detalle);
            }
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Pedido creado exitosamente",
                "idPedido", pedido.getIdPedido()
            ));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Formato de número inválido: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 4. Obtener detalle de un pedido
    @GetMapping("/{idPedido}/detalle")
    public ResponseEntity<?> getDetallePedido(@PathVariable Long idPedido) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
            Map<String, Object> detalle = new HashMap<>();
            detalle.put("idPedido", pedido.getIdPedido());
            detalle.put("fechaPedido", pedido.getFechaPedido());
            detalle.put("estadoPedido", pedido.getEstadoPedido());
            
            if (pedido.getEncargadoInsumo() != null && 
                pedido.getEncargadoInsumo().getUsuario() != null &&
                pedido.getEncargadoInsumo().getUsuario().getPersona() != null) {
                detalle.put("encargadoNombre", 
                    pedido.getEncargadoInsumo().getUsuario().getPersona().getNombre());
            }
            
            List<Map<String, Object>> insumos = new ArrayList<>();
            if (pedido.getDetalles() != null) {
                for (DetallePedido det : pedido.getDetalles()) {
                    Map<String, Object> insumoDTO = new HashMap<>();
                    insumoDTO.put("idInsumo", det.getInsumo().getIdInsumo());
                    insumoDTO.put("nombreInsumo", det.getInsumo().getNombreInsumo());
                    insumoDTO.put("cantidadPedida", det.getCantidadPedida());
                    insumoDTO.put("estadoDetPedido", det.getEstadoDetPedido());
                    insumoDTO.put("unidadMedida", det.getInsumo().getUnidadBase());
                    insumos.add(insumoDTO);
                }
            }
            detalle.put("insumos", insumos);
            
            return ResponseEntity.ok(detalle);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 5. Recibir pedido (actualiza stock) - Para encargado de insumos
    @PutMapping("/{idPedido}/recibir")
    public ResponseEntity<?> recibirPedido(@PathVariable Long idPedido,
                                           @RequestBody Map<String, Object> body) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
            if (!"APROBADO".equals(pedido.getEstadoPedido())) {
                return ResponseEntity.badRequest().body(Map.of("error", 
                    "Solo se pueden recibir pedidos aprobados. Estado actual: " + pedido.getEstadoPedido()));
            }
            
            for (DetallePedido detalle : pedido.getDetalles()) {
                Insumo insumo = detalle.getInsumo();
                int nuevoStock = insumo.getStockTotal() + detalle.getCantidadPedida();
                insumo.setStockTotal(nuevoStock);
                insumo.setUltimaActualizacion(LocalDate.now());
                insumoService.guardar(insumo);
                
                detalle.setEstadoDetPedido("RECIBIDO");
                detallePedidoService.guardar(detalle);
            }
            
            pedido.setEstadoPedido("RECIBIDO");
            pedidoService.guardar(pedido);
            
            return ResponseEntity.ok(Map.of("mensaje", "Pedido recibido y stock actualizado correctamente"));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 6. Cancelar pedido
    @PutMapping("/{idPedido}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long idPedido,
                                            @RequestBody Map<String, Object> body) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
            if (!"PENDIENTE".equals(pedido.getEstadoPedido())) {
                return ResponseEntity.badRequest().body(Map.of("error", 
                    "Solo se pueden cancelar pedidos pendientes. Estado actual: " + pedido.getEstadoPedido()));
            }
            
            for (DetallePedido detalle : pedido.getDetalles()) {
                detalle.setEstadoDetPedido("CANCELADO");
                detallePedidoService.guardar(detalle);
            }
            
            pedido.setEstadoPedido("CANCELADO");
            pedidoService.guardar(pedido);
            
            return ResponseEntity.ok(Map.of("mensaje", "Pedido cancelado correctamente"));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 7. Obtener solicitudes para el director (todos los pedidos PENDIENTE)
    @GetMapping("/solicitudes")
    public ResponseEntity<?> getSolicitudes() {
        try {
            List<Pedido> solicitudes = pedidoService.findByEstado("PENDIENTE");
            
            List<Map<String, Object>> solicitudesDTO = solicitudes.stream().map(p -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("idPedido", p.getIdPedido());
                dto.put("fechaPedido", p.getFechaPedido());
                dto.put("estadoPedido", p.getEstadoPedido());
                
                String encargadoNombre = "N/A";
                if (p.getEncargadoInsumo() != null && 
                    p.getEncargadoInsumo().getUsuario() != null &&
                    p.getEncargadoInsumo().getUsuario().getPersona() != null) {
                    encargadoNombre = p.getEncargadoInsumo().getUsuario().getPersona().getNombre();
                }
                dto.put("encargadoNombre", encargadoNombre);
                
                int cantidadInsumos = 0;
                int totalUnidades = 0;
                if (p.getDetalles() != null) {
                    cantidadInsumos = p.getDetalles().size();
                    totalUnidades = p.getDetalles().stream()
                        .mapToInt(DetallePedido::getCantidadPedida)
                        .sum();
                }
                dto.put("cantidadInsumos", cantidadInsumos);
                dto.put("totalUnidades", totalUnidades);
                
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(solicitudesDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ENDPOINTS DE ABASTECIMIENTO ====================
    // 8. Abastecer pedido - Director aprueba y crea abastecimiento
    @PutMapping("/{idPedido}/abastecer")
    public ResponseEntity<?> abastecerPedido(@PathVariable Long idPedido,
                                            @RequestBody Map<String, Object> body) {
        try {
            Long idDirector = Long.valueOf(body.get("idDirector").toString());
            String codigoAbastecimiento = (String) body.get("codigoAbastecimiento");
            
            // ✅ Validar que el código no esté vacío
            if (codigoAbastecimiento == null || codigoAbastecimiento.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El código de abastecimiento es requerido"));
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> insumosAbastecidos = (List<Map<String, Object>>) body.get("insumos");
            
            Pedido pedido = pedidoService.obtenerPorId(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
            if (!"PENDIENTE".equals(pedido.getEstadoPedido())) {
                return ResponseEntity.badRequest().body(Map.of("error", 
                    "Solo se pueden abastecer pedidos pendientes. Estado actual: " + pedido.getEstadoPedido()));
            }
            
            Director director = directorRepository.findById(idDirector)
                .orElseThrow(() -> new RuntimeException("Director no encontrado con ID: " + idDirector));
            
            // 1. Crear el abastecimiento con estado EN_PROCESO y el código ingresado
            Abastecimiento abastecimiento = new Abastecimiento();
            abastecimiento.setDirector(director);
            abastecimiento.setPedido(pedido);
            abastecimiento.setFechaAbastecimiento(LocalDate.now());
            abastecimiento.setEstadoAbastecimiento("EN_PROCESO");
            abastecimiento.setCodigoAbastecimiento(codigoAbastecimiento); // ✅ Usar el código ingresado
            abastecimiento = abastecimientoService.save(abastecimiento);
            
            // 2. Crear detalles de abastecimiento SOLO para los insumos que tienen cantidad > 0
            for (Map<String, Object> item : insumosAbastecidos) {
                Long idInsumo = Long.valueOf(item.get("idInsumo").toString());
                Integer cantidadComprometida = ((Number) item.get("cantidadAbastecida")).intValue();
                
                // ✅ Si la cantidad es 0, no crear el detalle
                if (cantidadComprometida <= 0) continue;
                
                BigDecimal costoUnitario = BigDecimal.valueOf(
                    item.containsKey("costoUnitario") ? 
                    Double.parseDouble(item.get("costoUnitario").toString()) : 0
                );
                
                Insumo insumo = insumoService.obtenerPorId(idInsumo)
                    .orElseThrow(() -> new RuntimeException("Insumo no encontrado con ID: " + idInsumo));
                
                DetallePedido detallePedido = detallePedidoService.findByPedidoAndInsumo(pedido, insumo)
                    .orElseThrow(() -> new RuntimeException("Detalle de pedido no encontrado para el insumo: " + insumo.getNombreInsumo()));
                
                DetalleAbastecimiento detalleAbast = new DetalleAbastecimiento();
                detalleAbast.setAbastecimiento(abastecimiento);
                detalleAbast.setDetallePedido(detallePedido);
                detalleAbast.setCantidadComprometida(cantidadComprometida);
                detalleAbast.setCantidadRecibida(0);
                detalleAbast.setCantidadRestante(cantidadComprometida);
                detalleAbast.setCostoUnitario(costoUnitario);
                detalleAbast.setCostoTotal(costoUnitario.multiply(BigDecimal.valueOf(cantidadComprometida)));
                detalleAbast.setEstadoDetalle("PENDIENTE");
                detalleAbastecimientoService.save(detalleAbast);
            }
            
            // 3. Actualizar estado del pedido a ABASTECIDO
            pedido.setEstadoPedido("ABASTECIDO");
            pedidoService.guardar(pedido);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Pedido abastecido correctamente",
                "idAbastecimiento", abastecimiento.getIdAbastecimiento(),
                "codigoAbastecimiento", abastecimiento.getCodigoAbastecimiento(),
                "estado", "EN_PROCESO"
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }  
    // 9. Obtener abastecimientos por director
    @GetMapping("/abastecimientos/director/{idDirector}")
    public ResponseEntity<?> getAbastecimientosByDirector(@PathVariable Long idDirector) {
        try {
            List<Abastecimiento> abastecimientos = abastecimientoService.findByDirectorId(idDirector);
            
            List<Map<String, Object>> abastecimientosDTO = abastecimientos.stream().map(a -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("idAbastecimiento", a.getIdAbastecimiento());
                dto.put("codigoAbastecimiento", a.getCodigoAbastecimiento());
                dto.put("fechaAbastecimiento", a.getFechaAbastecimiento());
                dto.put("estadoAbastecimiento", a.getEstadoAbastecimiento());
                dto.put("idPedido", a.getPedido().getIdPedido());
                
                if (a.getDirector() != null) {
                    String nombreDirector = "N/A";
                    if (a.getDirector().getUsuario() != null) {
                        if (a.getDirector().getUsuario().getPersona() != null) {
                            nombreDirector = a.getDirector().getUsuario().getPersona().getNombre();
                        } else {
                            nombreDirector = a.getDirector().getUsuario().getNombreUsuario();
                        }
                    }
                    dto.put("directorNombre", nombreDirector);
                }
                
                int cantidadInsumos = 0;
                int totalComprometido = 0;
                int totalRecibido = 0;
                BigDecimal costoTotal = BigDecimal.ZERO;
                
                if (a.getDetalles() != null) {
                    cantidadInsumos = a.getDetalles().size();
                    totalComprometido = a.getDetalles().stream()
                        .mapToInt(DetalleAbastecimiento::getCantidadComprometida)
                        .sum();
                    totalRecibido = a.getDetalles().stream()
                        .mapToInt(DetalleAbastecimiento::getCantidadRecibida)
                        .sum();
                    costoTotal = a.getDetalles().stream()
                        .map(DetalleAbastecimiento::getCostoTotal)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                dto.put("cantidadInsumos", cantidadInsumos);
                dto.put("totalComprometido", totalComprometido);
                dto.put("totalRecibido", totalRecibido);
                dto.put("costoTotal", costoTotal);
                
                int progreso = totalComprometido > 0 ? (totalRecibido * 100 / totalComprometido) : 0;
                dto.put("progreso", progreso);
                
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(abastecimientosDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 10. Obtener detalle de un abastecimiento
    @GetMapping("/abastecimientos/{idAbastecimiento}/detalle")
    public ResponseEntity<?> getDetalleAbastecimiento(@PathVariable Long idAbastecimiento) {
        try {
            Abastecimiento abastecimiento = abastecimientoService.findById(idAbastecimiento)
                .orElseThrow(() -> new RuntimeException("Abastecimiento no encontrado"));
            
            Map<String, Object> detalle = new HashMap<>();
            detalle.put("idAbastecimiento", abastecimiento.getIdAbastecimiento());
            detalle.put("codigoAbastecimiento", abastecimiento.getCodigoAbastecimiento());
            detalle.put("fechaAbastecimiento", abastecimiento.getFechaAbastecimiento());
            detalle.put("estadoAbastecimiento", abastecimiento.getEstadoAbastecimiento());
            detalle.put("idPedido", abastecimiento.getPedido().getIdPedido());
            
            if (abastecimiento.getDirector() != null) {
                String nombreDirector = "N/A";
                Director director = abastecimiento.getDirector();
                if (director.getUsuario() != null) {
                    Usuario usuario = director.getUsuario();
                    if (usuario.getPersona() != null) {
                        nombreDirector = usuario.getPersona().getNombre();
                    } else {
                        nombreDirector = usuario.getNombreUsuario();
                    }
                }
                detalle.put("directorNombre", nombreDirector);
            }
            
            List<Map<String, Object>> detallesDTO = new ArrayList<>();
            if (abastecimiento.getDetalles() != null) {
                for (DetalleAbastecimiento da : abastecimiento.getDetalles()) {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("idDetalleAbastecimiento", da.getIdDetalleAbastecimiento());
                    dto.put("idDetallePedido", da.getDetallePedido().getIdDetallePedidoInsumo());
                    dto.put("nombreInsumo", da.getDetallePedido().getInsumo().getNombreInsumo());
                    dto.put("cantidadComprometida", da.getCantidadComprometida());
                    dto.put("cantidadRecibida", da.getCantidadRecibida());
                    dto.put("cantidadRestante", da.getCantidadRestante());
                    dto.put("costoUnitario", da.getCostoUnitario());
                    dto.put("costoTotal", da.getCostoTotal());
                    dto.put("estadoDetalle", da.getEstadoDetalle());
                    
                    
                    
                    detallesDTO.add(dto);
                }
            }
            detalle.put("detalles", detallesDTO);
            
            return ResponseEntity.ok(detalle);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ENDPOINTS DE ACTAS DE ENTREGA ====================
    
    // 11. Registrar Acta de Entrega (Director) - SOLO GUARDA EL ACTA, SIN LOTES
    @PostMapping("/actas-entrega")
    public ResponseEntity<?> registrarActaEntrega(@RequestBody Map<String, Object> body) {
        try {
            Long idAbastecimiento = Long.valueOf(body.get("idAbastecimiento").toString());
            String observacion = body.containsKey("observacion") ? (String) body.get("observacion") : null;
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> detalles = (List<Map<String, Object>>) body.get("detalles");
            
            Abastecimiento abastecimiento = abastecimientoService.findById(idAbastecimiento)
                .orElseThrow(() -> new RuntimeException("Abastecimiento no encontrado"));
            
            // Validar que el abastecimiento esté en proceso
            if (!"EN_PROCESO".equals(abastecimiento.getEstadoAbastecimiento()) && 
                !"PARCIAL".equals(abastecimiento.getEstadoAbastecimiento())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "El abastecimiento debe estar en estado EN_PROCESO o PARCIAL. Estado actual: " + 
                    abastecimiento.getEstadoAbastecimiento()
                ));
            }
            
            // Crear Acta de Entrega
            ActaEntrega acta = new ActaEntrega();
            acta.setAbastecimiento(abastecimiento);
            acta.setFechaEntrega(LocalDate.now());
            acta.setObservacion(observacion);
            acta.setNumeroActa("ACTA-" + System.currentTimeMillis());
            acta = actaEntregaService.save(acta);
            
            // Crear detalles del acta (SOLO GUARDAR, SIN LOTES)
            for (Map<String, Object> item : detalles) {
                Long idDetalleAbastecimiento = Long.valueOf(item.get("idDetalleAbastecimiento").toString());
                Integer cantidadEntregada = ((Number) item.get("cantidadEntregada")).intValue();
                
                DetalleAbastecimiento detalleAbast = detalleAbastecimientoService.findById(idDetalleAbastecimiento)
                    .orElseThrow(() -> new RuntimeException("Detalle de abastecimiento no encontrado"));
                
                // Validar que no se entregue más de lo comprometido
                if (cantidadEntregada > detalleAbast.getCantidadRestante()) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "La cantidad entregada (" + cantidadEntregada + 
                        ") excede la cantidad restante (" + detalleAbast.getCantidadRestante() + 
                        ") para el insumo: " + detalleAbast.getDetallePedido().getInsumo().getNombreInsumo()
                    ));
                }
                
                // Crear DetalleActaEntrega (SIN LOTE AÚN)
                DetalleActaEntrega detalleActa = new DetalleActaEntrega();
                detalleActa.setActaEntrega(acta);
                detalleActa.setDetalleAbastecimiento(detalleAbast);
                detalleActa.setCantidadEntregada(cantidadEntregada);
                detalleActa = detalleActaEntregaService.save(detalleActa);
            }
            
            // Actualizar estado del Abastecimiento a PARCIAL (tiene actas pendientes)
            abastecimiento.setEstadoAbastecimiento("PARCIAL");
            abastecimientoService.save(abastecimiento);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Acta de entrega registrada exitosamente",
                "idActa", acta.getIdActaEntrega(),
                "numeroActa", acta.getNumeroActa(),
                "estadoAbastecimiento", "PARCIAL"
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 12. Obtener actas pendientes de crear lote (Encargado de Insumos)
    @GetMapping("/actas-entrega/pendientes")
    public ResponseEntity<?> getActasPendientesLote() {
        try {
            List<ActaEntrega> actas = actaEntregaService.findAll();
            
            List<Map<String, Object>> actasPendientes = new ArrayList<>();
            
            for (ActaEntrega acta : actas) {
                // Verificar si tiene detalles sin lote
                List<DetalleActaEntrega> detallesSinLote = acta.getDetalles().stream()
                    .filter(d -> d.getLote() == null)
                    .collect(Collectors.toList());
                
                if (!detallesSinLote.isEmpty()) {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("idActa", acta.getIdActaEntrega());
                    dto.put("numeroActa", acta.getNumeroActa());
                    dto.put("fechaEntrega", acta.getFechaEntrega());
                    dto.put("codigoAbastecimiento", acta.getAbastecimiento().getCodigoAbastecimiento());
                    
                    List<Map<String, Object>> detallesDTO = detallesSinLote.stream().map(d -> {
                        Map<String, Object> detDTO = new HashMap<>();
                        detDTO.put("idDetalleActa", d.getIdDetalleActa());
                        detDTO.put("nombreInsumo", d.getDetalleAbastecimiento().getDetallePedido().getInsumo().getNombreInsumo());
                        detDTO.put("cantidadEntregada", d.getCantidadEntregada());
                        detDTO.put("unidadMedida", d.getDetalleAbastecimiento().getDetallePedido().getUnidadMedida().getNombreUnidad());
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

    // 13. Obtener detalles de un acta sin lote
    @GetMapping("/actas-entrega/{idActa}/detalles-sin-lote")
    public ResponseEntity<?> getDetallesSinLote(@PathVariable Long idActa) {
        try {
            ActaEntrega acta = actaEntregaService.findById(idActa)
                .orElseThrow(() -> new RuntimeException("Acta no encontrada"));
            
            List<Map<String, Object>> detallesSinLote = acta.getDetalles().stream()
                .filter(d -> d.getLote() == null)
                .map(d -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("idDetalleActa", d.getIdDetalleActa());
                    dto.put("nombreInsumo", d.getDetalleAbastecimiento().getDetallePedido().getInsumo().getNombreInsumo());
                    dto.put("cantidadEntregada", d.getCantidadEntregada());
                    dto.put("unidadMedida", d.getDetalleAbastecimiento().getDetallePedido().getUnidadMedida().getNombreUnidad());
                    return dto;
                }).collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("idActa", acta.getIdActaEntrega());
            response.put("numeroActa", acta.getNumeroActa());
            response.put("fechaEntrega", acta.getFechaEntrega());
            response.put("detalles", detallesSinLote);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 14. Crear Lote desde un Acta de Entrega (Encargado de Insumos)
    @PostMapping("/actas-entrega/{idActa}/crear-lote")
    public ResponseEntity<?> crearLoteDesdeActa(@PathVariable Long idActa,
                                                @RequestBody Map<String, Object> body) {
        try {
            Long idDetalleActa = Long.valueOf(body.get("idDetalleActa").toString());
            String fechaVencimientoStr = body.get("fechaVencimiento").toString();
            LocalDate fechaVencimiento = LocalDate.parse(fechaVencimientoStr);
            
            // Obtener el detalle del acta
            DetalleActaEntrega detalleActa = detalleActaEntregaService.findById(idDetalleActa)
                .orElseThrow(() -> new RuntimeException("Detalle de acta no encontrado"));
            
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
            lote.setNumeroLote("LOT-" + System.currentTimeMillis() + "-" + insumo.getIdInsumo());
            lote.setInsumo(insumo);
            lote.setDetalleActaEntrega(detalleActa);
            lote.setFechaIngreso(LocalDate.now());
            lote.setFechaVencimiento(fechaVencimiento);
            lote.setCantidadInicial(cantidadEntregada);
            lote.setCantidadDisponible(cantidadEntregada);
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
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 15. Obtener todas las actas de entrega
    @GetMapping("/actas-entrega")
    public ResponseEntity<?> getActasEntrega() {
        try {
            List<ActaEntrega> actas = actaEntregaService.findAll();
            
            List<Map<String, Object>> actasDTO = actas.stream().map(a -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("idActaEntrega", a.getIdActaEntrega());
                dto.put("numeroActa", a.getNumeroActa());
                dto.put("fechaEntrega", a.getFechaEntrega());
                dto.put("observacion", a.getObservacion());
                dto.put("codigoAbastecimiento", a.getAbastecimiento().getCodigoAbastecimiento());
                dto.put("idAbastecimiento", a.getAbastecimiento().getIdAbastecimiento());
                
                int cantidadInsumos = 0;
                int totalEntregado = 0;
                if (a.getDetalles() != null) {
                    cantidadInsumos = a.getDetalles().size();
                    totalEntregado = a.getDetalles().stream()
                        .mapToInt(DetalleActaEntrega::getCantidadEntregada)
                        .sum();
                }
                dto.put("cantidadInsumos", cantidadInsumos);
                dto.put("totalEntregado", totalEntregado);
                
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(actasDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 16. Obtener detalle de un acta de entrega
    @GetMapping("/actas-entrega/{idActa}")
    public ResponseEntity<?> getDetalleActa(@PathVariable Long idActa) {
        try {
            ActaEntrega acta = actaEntregaService.findById(idActa)
                .orElseThrow(() -> new RuntimeException("Acta no encontrada"));
            
            Map<String, Object> detalle = new HashMap<>();
            detalle.put("idActaEntrega", acta.getIdActaEntrega());
            detalle.put("numeroActa", acta.getNumeroActa());
            detalle.put("fechaEntrega", acta.getFechaEntrega());
            detalle.put("observacion", acta.getObservacion());
            detalle.put("codigoAbastecimiento", acta.getAbastecimiento().getCodigoAbastecimiento());
            
            List<Map<String, Object>> detallesDTO = new ArrayList<>();
            if (acta.getDetalles() != null) {
                for (DetalleActaEntrega da : acta.getDetalles()) {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("idDetalleActa", da.getIdDetalleActa());
                    dto.put("nombreInsumo", da.getDetalleAbastecimiento().getDetallePedido().getInsumo().getNombreInsumo());
                    dto.put("cantidadEntregada", da.getCantidadEntregada());
                    dto.put("unidadMedida", da.getDetalleAbastecimiento().getDetallePedido().getUnidadMedida().getNombreUnidad());
                    
                    if (da.getLote() != null) {
                        dto.put("idLote", da.getLote().getIdLote());
                        dto.put("numeroLote", da.getLote().getNumeroLote());
                        dto.put("tieneLote", true);
                    } else {
                        dto.put("tieneLote", false);
                    }
                    
                    detallesDTO.add(dto);
                }
            }
            detalle.put("detalles", detallesDTO);
            
            return ResponseEntity.ok(detalle);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

}