package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Dto.PrestamoEquipoEncargadoDTO;
import proyect_final.clinica.Model.Entity.PrestamoEquipo;
import proyect_final.clinica.Model.Entity.EncargadoInsumo;
import proyect_final.clinica.Model.Dao.EncargadoInsumoRepository;
import proyect_final.clinica.Service.PrestamoEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyect_final.clinica.Service.EquipoService;

import java.sql.Time;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prestamos-equipo")
public class PrestamoEquipoController {

    @Autowired
    private PrestamoEquipoService prestamoEquipoService;

    @Autowired
    private EncargadoInsumoRepository encargadoInsumoRepository;

    @Autowired
    private EquipoService equipoService;  // ✅ Corregido: minúscula

    // ==================== OBTENER TODOS ====================
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            return ResponseEntity.ok(prestamoEquipoService.obtenerTodos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== OBTENER POR ID (CON DTO SIMPLE) ====================
    @GetMapping("/{idPrestamo}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long idPrestamo) {
        try {
            PrestamoEquipo prestamo = prestamoEquipoService.obtenerPorId(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
            
            Map<String, Object> dto = new HashMap<>();
            dto.put("idPrestamoEquipo", prestamo.getIdPrestamoEquipo());
            dto.put("fechaEntrePrestamo", prestamo.getFechaEntrePrestamo());
            dto.put("estadoDevolucion", prestamo.getEstadoDevolucion());
            dto.put("observacionPrestamo", prestamo.getObservacionPrestamo());
            dto.put("fechRegPreEqu", prestamo.getFechRegPreEqu());
            
            if (prestamo.getIdEquipo() != null) {
                dto.put("idEquipo", prestamo.getIdEquipo().getIdEquipo());
                dto.put("nombreEquipo", prestamo.getIdEquipo().getNombreEquipo());
                dto.put("codigoEquipo", prestamo.getIdEquipo().getCodigoEquipo());
                dto.put("estadoEquipo", prestamo.getIdEquipo().getEstadoEquipo());
            }
            
            if (prestamo.getIdEstudiante() != null) {
                dto.put("idEstudiante", prestamo.getIdEstudiante().getIdEstudiante());
                if (prestamo.getIdEstudiante().getUsuario() != null && 
                    prestamo.getIdEstudiante().getUsuario().getPersona() != null) {
                    dto.put("nombreEstudiante", prestamo.getIdEstudiante().getUsuario().getPersona().getNombre());
                    dto.put("apellidoEstudiante", prestamo.getIdEstudiante().getUsuario().getPersona().getApellidoPaterno());
                }
            }
            
            if (prestamo.getIdDocente() != null) {
                dto.put("idDocente", prestamo.getIdDocente().getIdDocente());
                if (prestamo.getIdDocente().getUsuario() != null && 
                    prestamo.getIdDocente().getUsuario().getPersona() != null) {
                    dto.put("nombreDocente", prestamo.getIdDocente().getUsuario().getPersona().getNombre());
                    dto.put("apellidoDocente", prestamo.getIdDocente().getUsuario().getPersona().getApellidoPaterno());
                }
            }
            
            if (prestamo.getIdEncargadoInsumo() != null) {
                dto.put("idEncargado", prestamo.getIdEncargadoInsumo().getIdEncargadoInsumo());
                if (prestamo.getIdEncargadoInsumo().getUsuario() != null && 
                    prestamo.getIdEncargadoInsumo().getUsuario().getPersona() != null) {
                    dto.put("nombreEncargado", prestamo.getIdEncargadoInsumo().getUsuario().getPersona().getNombre());
                }
            }
            
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== OBTENER POR DOCENTE ====================
    @GetMapping("/docente/{idDocente}")
    public ResponseEntity<?> getByDocente(@PathVariable Long idDocente) {
        try {
            List<PrestamoEquipo> prestamos = prestamoEquipoService.findByDocenteId(idDocente);
            
            List<Map<String, Object>> response = prestamos.stream().map(p -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("idPrestamoEquipo", p.getIdPrestamoEquipo());
                dto.put("fechaEntrePrestamo", p.getFechaEntrePrestamo());
                dto.put("estadoDevolucion", p.getEstadoDevolucion());
                dto.put("observacionPrestamo", p.getObservacionPrestamo());
                
                if (p.getIdEquipo() != null) {
                    Map<String, Object> equipo = new HashMap<>();
                    equipo.put("idEquipo", p.getIdEquipo().getIdEquipo());
                    equipo.put("nombreEquipo", p.getIdEquipo().getNombreEquipo());
                    equipo.put("codigoEquipo", p.getIdEquipo().getCodigoEquipo());
                    equipo.put("estadoEquipo", p.getIdEquipo().getEstadoEquipo());
                    dto.put("idEquipo", equipo);
                }
                
                if (p.getIdEstudiante() != null && p.getIdEstudiante().getUsuario() != null) {
                    Map<String, Object> estudiante = new HashMap<>();
                    estudiante.put("idEstudiante", p.getIdEstudiante().getIdEstudiante());
                    if (p.getIdEstudiante().getUsuario().getPersona() != null) {
                        Map<String, Object> persona = new HashMap<>();
                        persona.put("nombre", p.getIdEstudiante().getUsuario().getPersona().getNombre());
                        persona.put("apellidoPaterno", p.getIdEstudiante().getUsuario().getPersona().getApellidoPaterno());
                        estudiante.put("persona", persona);
                    }
                    dto.put("idEstudiante", estudiante);
                }
                
                if (p.getIdDocente() != null && p.getIdDocente().getUsuario() != null) {
                    Map<String, Object> docente = new HashMap<>();
                    docente.put("idDocente", p.getIdDocente().getIdDocente());
                    if (p.getIdDocente().getUsuario().getPersona() != null) {
                        Map<String, Object> persona = new HashMap<>();
                        persona.put("nombre", p.getIdDocente().getUsuario().getPersona().getNombre());
                        persona.put("apellidoPaterno", p.getIdDocente().getUsuario().getPersona().getApellidoPaterno());
                        docente.put("persona", persona);
                    }
                    dto.put("idDocente", docente);
                }
                
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== OBTENER POR ENCARGADO (CON DTO) ====================
    @GetMapping("/encargado/{idEncargado}")
    public ResponseEntity<?> getByEncargado(@PathVariable Long idEncargado) {
        try {
            List<PrestamoEquipo> todas = prestamoEquipoService.obtenerTodos();
            
            List<PrestamoEquipo> filtradas = todas.stream()
                .filter(p -> {
                    String estado = p.getEstadoDevolucion();
                    Long encargadoActual = p.getIdEncargadoInsumo() != null ? 
                        p.getIdEncargadoInsumo().getIdEncargadoInsumo() : null;
                    
                    return ("APROBADO".equals(estado) && encargadoActual == null) ||
                        (encargadoActual != null && encargadoActual.equals(idEncargado));
                })
                .collect(Collectors.toList());
            
            List<PrestamoEquipoEncargadoDTO> response = filtradas.stream().map(p -> {
                PrestamoEquipoEncargadoDTO dto = new PrestamoEquipoEncargadoDTO();
                dto.setIdPrestamoEquipo(p.getIdPrestamoEquipo());
                dto.setFechaEntrePrestamo(p.getFechaEntrePrestamo());
                dto.setEstadoDevolucion(p.getEstadoDevolucion());
                dto.setObservacionPrestamo(p.getObservacionPrestamo());
                dto.setFechRegPreEqu(p.getFechRegPreEqu());
                
                if (p.getIdEquipo() != null) {
                    dto.setIdEquipo(p.getIdEquipo().getIdEquipo());
                    dto.setNombreEquipo(p.getIdEquipo().getNombreEquipo());
                    dto.setCodigoEquipo(p.getIdEquipo().getCodigoEquipo());
                    dto.setEstadoEquipo(p.getIdEquipo().getEstadoEquipo());
                }
                
                if (p.getIdEstudiante() != null) {
                    dto.setIdEstudiante(p.getIdEstudiante().getIdEstudiante());
                    if (p.getIdEstudiante().getUsuario() != null && 
                        p.getIdEstudiante().getUsuario().getPersona() != null) {
                        dto.setNombreEstudiante(p.getIdEstudiante().getUsuario().getPersona().getNombre());
                        dto.setApellidoEstudiante(p.getIdEstudiante().getUsuario().getPersona().getApellidoPaterno());
                    }
                }
                
                if (p.getIdDocente() != null) {
                    dto.setIdDocente(p.getIdDocente().getIdDocente());
                    if (p.getIdDocente().getUsuario() != null && 
                        p.getIdDocente().getUsuario().getPersona() != null) {
                        dto.setNombreDocente(p.getIdDocente().getUsuario().getPersona().getNombre());
                        dto.setApellidoDocente(p.getIdDocente().getUsuario().getPersona().getApellidoPaterno());
                    }
                }
                
                if (p.getIdEncargadoInsumo() != null) {
                    dto.setIdEncargado(p.getIdEncargadoInsumo().getIdEncargadoInsumo());
                    if (p.getIdEncargadoInsumo().getUsuario() != null && 
                        p.getIdEncargadoInsumo().getUsuario().getPersona() != null) {
                        dto.setNombreEncargado(p.getIdEncargadoInsumo().getUsuario().getPersona().getNombre());
                    }
                }
                
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== CREAR PRÉSTAMO ====================
    @PostMapping("/crear")
    public ResponseEntity<?> crearPrestamo(@RequestBody PrestamoEquipo prestamo) {
        try {
            if (prestamo.getIdEquipo() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe especificar un equipo"));
            }

            if (prestamo.getIdDocente() == null && prestamo.getIdEstudiante() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe especificar un docente o estudiante"));
            }

            if (prestamo.getFechaEntrePrestamo() == null) {
                prestamo.setFechaEntrePrestamo(LocalDate.now());
            }
            if (prestamo.getHoraSolicitud() == null) {
                prestamo.setHoraSolicitud(new Time(System.currentTimeMillis()));
            }
            if (prestamo.getHoraEntreEquipo() == null) {
                prestamo.setHoraEntreEquipo(new Time(System.currentTimeMillis()));
            }
            if (prestamo.getEstadoDevolucion() == null) {
                prestamo.setEstadoDevolucion("PENDIENTE_DOCENTE");
            }

            PrestamoEquipo nuevo = prestamoEquipoService.guardar(prestamo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Préstamo registrado correctamente");
            response.put("idPrestamo", nuevo.getIdPrestamoEquipo());
            response.put("estado", nuevo.getEstadoDevolucion());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== APROBAR SOLICITUD ====================
    @PutMapping("/{idPrestamo}/aprobar")
    public ResponseEntity<?> aprobar(@PathVariable Long idPrestamo) {
        try {
            PrestamoEquipo prestamo = prestamoEquipoService.aprobarSolicitud(idPrestamo);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Solicitud aprobada correctamente",
                "idPrestamo", prestamo.getIdPrestamoEquipo(),
                "estado", prestamo.getEstadoDevolucion()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== RECHAZAR SOLICITUD ====================
    @PutMapping("/{idPrestamo}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable Long idPrestamo,
                                      @RequestBody Map<String, String> body) {
        try {
            String motivo = body.get("motivo");
            if (motivo == null || motivo.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe especificar un motivo"));
            }
            
            PrestamoEquipo prestamo = prestamoEquipoService.rechazarSolicitud(idPrestamo, motivo);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Solicitud rechazada",
                "idPrestamo", prestamo.getIdPrestamoEquipo(),
                "estado", prestamo.getEstadoDevolucion()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ENTREGAR EQUIPO ====================
    @PutMapping("/{idPrestamo}/entregar")
    public ResponseEntity<?> entregarEquipo(@PathVariable Long idPrestamo,
                                            @RequestBody Map<String, Object> body) {
        try {
            PrestamoEquipo prestamo = prestamoEquipoService.obtenerPorId(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

            if (!"APROBADO".equals(prestamo.getEstadoDevolucion())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Solo se pueden entregar equipos aprobados"));
            }

            Long idEncargado = Long.valueOf(body.get("idEncargado").toString());
            EncargadoInsumo encargado = encargadoInsumoRepository.findById(idEncargado)
                .orElseThrow(() -> new RuntimeException("Encargado no encontrado"));

            // ✅ Cambiar estado del equipo a "EN_USO"
            if (prestamo.getIdEquipo() != null) {
                equipoService.actualizarEstadoEquipo(
                    prestamo.getIdEquipo().getIdEquipo(), 
                    "EN_USO"
                );
                System.out.println("✅ Equipo #" + prestamo.getIdEquipo().getIdEquipo() + " cambiado a EN_USO");
            }

            prestamo.setIdEncargadoInsumo(encargado);
            prestamo.setFechaEntrePrestamo(LocalDate.now());
            prestamo.setHoraEntreEquipo(new Time(System.currentTimeMillis()));
            prestamo.setEstadoDevolucion("PRESTADO");

            PrestamoEquipo actualizado = prestamoEquipoService.guardar(prestamo);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Equipo entregado correctamente");
            response.put("idPrestamo", actualizado.getIdPrestamoEquipo());
            response.put("estado", actualizado.getEstadoDevolucion());
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== REGISTRAR DEVOLUCIÓN ====================
    @PutMapping("/{idPrestamo}/devolver")
    public ResponseEntity<?> devolver(@PathVariable Long idPrestamo,
                                      @RequestBody(required = false) Map<String, Object> body) {
        try {
            PrestamoEquipo prestamo = prestamoEquipoService.obtenerPorId(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

            if (!"PRESTADO".equals(prestamo.getEstadoDevolucion())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Este préstamo no está en estado PRESTADO"));
            }

            // ✅ Cambiar estado del equipo a "ACTIVO"
            if (prestamo.getIdEquipo() != null) {
                equipoService.actualizarEstadoEquipo(
                    prestamo.getIdEquipo().getIdEquipo(), 
                    "ACTIVO"
                );
                System.out.println("✅ Equipo #" + prestamo.getIdEquipo().getIdEquipo() + " cambiado a ACTIVO");
            }

            prestamo.setEstadoDevolucion("DEVUELTO");
            prestamo.setHoraDevolEquipo(new Time(System.currentTimeMillis()));

            PrestamoEquipo actualizado = prestamoEquipoService.guardar(prestamo);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Equipo devuelto correctamente");
            response.put("idPrestamo", actualizado.getIdPrestamoEquipo());
            response.put("estado", actualizado.getEstadoDevolucion());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ELIMINAR ====================
    @DeleteMapping("/{idPrestamo}")
    public ResponseEntity<?> eliminar(@PathVariable Long idPrestamo) {
        try {
            prestamoEquipoService.eliminar(idPrestamo);
            return ResponseEntity.ok(Map.of("mensaje", "Préstamo eliminado correctamente"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ESTADÍSTICAS ====================
    @GetMapping("/estadisticas")
    public ResponseEntity<?> getEstadisticas() {
        try {
            List<PrestamoEquipo> todos = prestamoEquipoService.obtenerTodos();
            
            long pendientes = todos.stream()
                .filter(p -> "PENDIENTE_DOCENTE".equals(p.getEstadoDevolucion()) || "PENDIENTE".equals(p.getEstadoDevolucion()))
                .count();
            
            long aprobados = todos.stream()
                .filter(p -> "APROBADO".equals(p.getEstadoDevolucion()))
                .count();
            
            long prestados = todos.stream()
                .filter(p -> "PRESTADO".equals(p.getEstadoDevolucion()))
                .count();
            
            long devueltos = todos.stream()
                .filter(p -> "DEVUELTO".equals(p.getEstadoDevolucion()))
                .count();
            
            long rechazados = todos.stream()
                .filter(p -> "RECHAZADO".equals(p.getEstadoDevolucion()))
                .count();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", todos.size());
            stats.put("pendientes", pendientes);
            stats.put("aprobados", aprobados);
            stats.put("prestados", prestados);
            stats.put("devueltos", devueltos);
            stats.put("rechazados", rechazados);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}