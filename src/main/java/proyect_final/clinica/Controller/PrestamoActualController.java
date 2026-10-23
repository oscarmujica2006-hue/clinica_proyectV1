package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Dto.PrestamoActualDTO;
import proyect_final.clinica.Model.Entity.PrestamoActual;
import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Model.Entity.Archivo;
import proyect_final.clinica.Model.Entity.Paciente;
import proyect_final.clinica.Model.Entity.Persona;
import proyect_final.clinica.Model.Dao.EstudianteRepository;
import proyect_final.clinica.Model.Dao.ArchivoRepository;
import proyect_final.clinica.Service.PrestamoActualService;
import proyect_final.clinica.Service.EstudianteService;
import proyect_final.clinica.Service.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prestamos-actuales")
@CrossOrigin(origins = "*")
public class PrestamoActualController {

    @Autowired
    private PrestamoActualService prestamoActualService;
    
    @Autowired
    private EstudianteService estudianteService;
    
    @Autowired
    private ArchivoService archivoService;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ArchivoRepository archivoRepository;

    // ✅ CORREGIDO: Cambiar a Long
    private Long getIdUsuarioDeSesion(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object idUsuarioObj = session.getAttribute("idUsuario");
                if (idUsuarioObj != null) {
                    if (idUsuarioObj instanceof Long) {
                        return (Long) idUsuarioObj;
                    } else if (idUsuarioObj instanceof Integer) {
                        return ((Integer) idUsuarioObj).longValue();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener ID de usuario de sesión: " + e.getMessage());
        }
        return null;
    }

    // ✅ Método para obtener ID de recepción de la sesión
    private Long getIdRecepcionDeSesion(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object idRecepcionObj = session.getAttribute("idRecepcion");
                if (idRecepcionObj != null) {
                    if (idRecepcionObj instanceof Long) {
                        return (Long) idRecepcionObj;
                    } else if (idRecepcionObj instanceof Integer) {
                        return ((Integer) idRecepcionObj).longValue();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener ID de recepción de sesión: " + e.getMessage());
        }
        return null;
    }

    // ===== ENDPOINTS EXISTENTES =====
    
    @GetMapping
    public ResponseEntity<List<PrestamoActualDTO>> obtenerTodos() {
        prestamoActualService.marcarVencidosYBloquear();
        List<PrestamoActual> prestamos = prestamoActualService.obtenerTodos();
        return ResponseEntity.ok(convertirADTOs(prestamos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoActualDTO> obtenerPorId(@PathVariable Long id) {
        Optional<PrestamoActual> prestamo = prestamoActualService.obtenerPorId(id);
        return prestamo.map(p -> ResponseEntity.ok(convertirADTO(p)))
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crearPrestamo(
            @RequestBody PrestamoActualDTO prestamoDTO,
            HttpServletRequest request) {
        try {
            Long idUsuario = getIdUsuarioDeSesion(request);
            Long idRecepcion = getIdRecepcionDeSesion(request);
            
            if (idUsuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            
            if (idRecepcion == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            PrestamoActual prestamo = new PrestamoActual();
            prestamo.setIdArchivo(prestamoDTO.getIdArchivo());
            prestamo.setIdEstudiante(prestamoDTO.getIdEstudiante());
            prestamo.setFechaPrestamo(LocalDate.now());
            prestamo.setFechaLimitePrestamo(prestamoDTO.getFechaLimitePrestamo());
            prestamo.setTipoPrestamo(prestamoDTO.getTipoPrestamo());
            prestamo.setMotivoPrestamo(prestamoDTO.getMotivoPrestamo());
            prestamo.setEstadoPrestamo("ACTIVO");
            prestamo.setIdRecepcion(idRecepcion);
            prestamo.setUsuRegPreAct(idUsuario.intValue());
            prestamo.setUsuModPreAct(idUsuario.intValue());
            
            PrestamoActual nuevoPrestamo = prestamoActualService.registrarPrestamo(prestamo);
            return new ResponseEntity<>(convertirADTO(nuevoPrestamo), HttpStatus.CREATED);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Error interno del servidor"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrestamoActualDTO> actualizarPrestamo(
            @PathVariable Long id, 
            @RequestBody PrestamoActualDTO prestamoDTO,
            HttpServletRequest request) {
        try {
            Long idUsuario = getIdUsuarioDeSesion(request);
            if (idUsuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            PrestamoActual prestamo = prestamoActualService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
            
            prestamo.setFechaLimitePrestamo(prestamoDTO.getFechaLimitePrestamo());
            prestamo.setTipoPrestamo(prestamoDTO.getTipoPrestamo());
            prestamo.setMotivoPrestamo(prestamoDTO.getMotivoPrestamo());
            prestamo.setEstadoPrestamo(prestamoDTO.getEstadoPrestamo());
            prestamo.setUsuModPreAct(idUsuario.intValue());
            
            PrestamoActual prestamoActualizado = prestamoActualService.actualizar(id, prestamo);
            return ResponseEntity.ok(convertirADTO(prestamoActualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> eliminarPrestamo(@PathVariable Long id) {
        try {
            prestamoActualService.eliminar(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<PrestamoActualDTO>> buscarPorIdEstudiante(@PathVariable Long idEstudiante) {
        try {
            List<PrestamoActual> prestamos = prestamoActualService.buscarPorIdEstudiante(idEstudiante);
            if (prestamos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(convertirADTOs(prestamos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/archivo/{idArchivo}")
    public ResponseEntity<List<PrestamoActualDTO>> buscarPorIdArchivo(@PathVariable Long idArchivo) {
        try {
            List<PrestamoActual> prestamos = prestamoActualService.buscarPorIdArchivo(idArchivo);
            if (prestamos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(convertirADTOs(prestamos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== NUEVOS ENDPOINTS PARA LAS PESTAÑAS =====
    
    /**
     * Obtener todos los préstamos con filtros opcionales
     * GET /api/prestamos-actuales/todos?estado=ACTIVO&idEstudiante=1&idArchivo=1
     */
    @GetMapping("/todos")
    public ResponseEntity<List<PrestamoActualDTO>> obtenerTodosConFiltros(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long idEstudiante,
            @RequestParam(required = false) Long idArchivo) {
        
        // Evalúa vencimientos y bloqueos pendientes antes de listar
        prestamoActualService.marcarVencidosYBloquear();

        List<PrestamoActual> prestamos = prestamoActualService.obtenerTodos();
        
        // Aplicar filtros
        if (estado != null && !estado.isEmpty()) {
            prestamos = prestamos.stream()
                .filter(p -> p.getEstadoPrestamo() != null && p.getEstadoPrestamo().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
        }
        if (idEstudiante != null) {
            prestamos = prestamos.stream()
                .filter(p -> idEstudiante.equals(p.getIdEstudiante()))
                .collect(Collectors.toList());
        }
        if (idArchivo != null) {
            prestamos = prestamos.stream()
                .filter(p -> idArchivo.equals(p.getIdArchivo()))
                .collect(Collectors.toList());
        }
        
        // Ordenar por fecha de préstamo descendente
        prestamos.sort((p1, p2) -> p2.getFechaPrestamo().compareTo(p1.getFechaPrestamo()));
        
        return ResponseEntity.ok(convertirADTOs(prestamos));
    }

    /**
     * Registrar devolución con desbloqueo automático del estudiante
     * PUT /api/prestamos-actuales/{id}/devolver-con-desbloqueo
     */
    @PutMapping("/{id}/devolver-con-desbloqueo")
    public ResponseEntity<?> registrarDevolucionConDesbloqueo(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long idUsuario = getIdUsuarioDeSesion(request);
            if (idUsuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", "Usuario no autenticado"));
            }
            
            PrestamoActual prestamo = prestamoActualService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
            
            // Verificar que no esté ya devuelto
            if ("DEVUELTO".equals(prestamo.getEstadoPrestamo())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Este préstamo ya fue devuelto"));
            }
            
            // Registrar devolución
            prestamo.setFechaDevolucion(LocalDate.now());
            prestamo.setEstadoPrestamo("DEVUELTO");
            prestamo.setUsuModPreAct(idUsuario.intValue());
            
            // Calcular días de retraso
            if (prestamo.getFechaLimitePrestamo().isBefore(LocalDate.now())) {
                long dias = ChronoUnit.DAYS.between(prestamo.getFechaLimitePrestamo(), LocalDate.now());
                prestamo.setDiasRetraso((int) dias);
            } else {
                prestamo.setDiasRetraso(0);
            }
            
            PrestamoActual actualizado = prestamoActualService.actualizar(id, prestamo);
            
            // Desbloquear estudiante automáticamente solo si no le quedan préstamos vencidos
            Long idEstudiante = prestamo.getIdEstudiante();
            boolean desbloqueado = false;
            if (!prestamoActualService.tienePrestamosVencidos(idEstudiante)) {
                prestamoActualService.desbloquearEstudiante(idEstudiante);
                desbloqueado = true;
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", desbloqueado
                    ? "Devolución registrada y estudiante desbloqueado"
                    : "Devolución registrada. El estudiante sigue bloqueado por otro(s) préstamo(s) vencido(s)");
            response.put("idPrestamo", actualizado.getId());
            response.put("idEstudiante", idEstudiante);
            response.put("desbloqueado", desbloqueado);
            response.put("diasRetraso", actualizado.getDiasRetraso());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Error interno del servidor"));
        }
    }

    /**
     * Registrar devolución simple (sin desbloqueo automático)
     * PUT /api/prestamos-actuales/{id}/devolver
     */
    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> registrarDevolucion(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long idUsuario = getIdUsuarioDeSesion(request);
            if (idUsuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", "Usuario no autenticado"));
            }
            
            PrestamoActual prestamo = prestamoActualService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
            
            if ("DEVUELTO".equals(prestamo.getEstadoPrestamo())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Este préstamo ya fue devuelto"));
            }
            
            prestamo.setFechaDevolucion(LocalDate.now());
            prestamo.setEstadoPrestamo("DEVUELTO");
            prestamo.setUsuModPreAct(idUsuario.intValue());
            
            if (prestamo.getFechaLimitePrestamo().isBefore(LocalDate.now())) {
                long dias = ChronoUnit.DAYS.between(prestamo.getFechaLimitePrestamo(), LocalDate.now());
                prestamo.setDiasRetraso((int) dias);
            } else {
                prestamo.setDiasRetraso(0);
            }
            
            PrestamoActual actualizado = prestamoActualService.actualizar(id, prestamo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Devolución registrada correctamente");
            response.put("idPrestamo", actualizado.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Error interno del servidor"));
        }
    }

    /**
     * Desbloquear estudiante
     * POST /api/prestamos-actuales/desbloquear/{idEstudiante}
     */
    @PostMapping("/desbloquear/{idEstudiante}")
    public ResponseEntity<?> desbloquearEstudiante(
            @PathVariable Long idEstudiante,
            HttpServletRequest request) {
        try {
            Long idUsuario = getIdUsuarioDeSesion(request);
            if (idUsuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", "Usuario no autenticado"));
            }

            // No se desbloquea si el estudiante sigue con préstamos vencidos sin devolver
            if (prestamoActualService.tienePrestamosVencidos(idEstudiante)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "El estudiante tiene préstamo(s) vencido(s) pendiente(s) de devolución. " +
                             "Registre la devolución antes de desbloquearlo."
                ));
            }

            prestamoActualService.desbloquearEstudiante(idEstudiante);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estudiante desbloqueado correctamente");
            response.put("idEstudiante", idEstudiante);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Error interno del servidor"));
        }
    }

    /**
     * Obtener estadísticas de préstamos
     * GET /api/prestamos-actuales/estadisticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            prestamoActualService.marcarVencidosYBloquear();

            List<PrestamoActual> prestamos = prestamoActualService.obtenerTodos();
            
            long total = prestamos.size();
            long activos = prestamos.stream()
                .filter(p -> "ACTIVO".equals(p.getEstadoPrestamo()) || "VENCIDO".equals(p.getEstadoPrestamo()))
                .count();
            long vencidos = prestamos.stream()
                .filter(p -> "VENCIDO".equals(p.getEstadoPrestamo()))
                .count();
            long devueltos = prestamos.stream()
                .filter(p -> "DEVUELTO".equals(p.getEstadoPrestamo()))
                .count();
            
            // Contar estudiantes bloqueados (de cualquier tipo de bloqueo)
            List<Long> estudiantesConPrestamos = prestamos.stream()
                .filter(p -> "ACTIVO".equals(p.getEstadoPrestamo()) || "VENCIDO".equals(p.getEstadoPrestamo()))
                .map(PrestamoActual::getIdEstudiante)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
            
            long estudiantesBloqueados = 0;
            if (!estudiantesConPrestamos.isEmpty()) {
                estudiantesBloqueados = estudianteRepository.findAllById(estudiantesConPrestamos).stream()
                    .filter(e -> Boolean.TRUE.equals(e.getBloqueado()))
                    .count();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", total);
            response.put("activos", activos);
            response.put("vencidos", vencidos);
            response.put("devueltos", devueltos);
            response.put("estudiantesBloqueados", estudiantesBloqueados);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Error interno del servidor"));
        }
    }

    // ===== MÉTODO PRIVADO PARA CONVERTIR A DTO =====
    
    private PrestamoActualDTO convertirADTO(PrestamoActual prestamo) {
        return convertirADTOs(List.of(prestamo)).get(0);
    }
    
    /**
     * Convierte una lista de préstamos a DTO resolviendo estudiantes y archivos
     * en una sola carga por tabla (evita el N+1 de la versión anterior).
     */
    private List<PrestamoActualDTO> convertirADTOs(List<PrestamoActual> prestamos) {
        List<PrestamoActualDTO> dtos = new ArrayList<>();

        if (prestamos == null || prestamos.isEmpty()) {
            return dtos;
        }

        // Carga masiva de estudiantes
        List<Long> idsEstudiantes = prestamos.stream()
                .map(PrestamoActual::getIdEstudiante)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, Estudiante> estudiantes = idsEstudiantes.isEmpty()
                ? Map.of()
                : estudianteRepository.findAllById(idsEstudiantes).stream()
                    .collect(Collectors.toMap(Estudiante::getIdEstudiante, e -> e));

        // Carga masiva de archivos
        List<Long> idsArchivos = prestamos.stream()
                .map(PrestamoActual::getIdArchivo)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, Archivo> archivos = idsArchivos.isEmpty()
                ? Map.of()
                : archivoRepository.findAllById(idsArchivos).stream()
                    .collect(Collectors.toMap(Archivo::getIdArchivo, a -> a));

        for (PrestamoActual prestamo : prestamos) {
            PrestamoActualDTO dto = new PrestamoActualDTO(prestamo);

            // Datos del estudiante
            Estudiante estudiante = estudiantes.get(prestamo.getIdEstudiante());
            if (estudiante != null) {
                dto.setCodigoEstudiante(estudiante.getCodigoEstudiante());
                dto.setEstudianteBloqueado(Boolean.TRUE.equals(estudiante.getBloqueado()));

                if (estudiante.getUsuario() != null) {
                    dto.setEmailEstudiante(estudiante.getUsuario().getEmail());

                    if (estudiante.getUsuario().getPersona() != null) {
                        dto.setNombreEstudiante(nombreCompleto(estudiante.getUsuario().getPersona()));
                    }
                }
            } else {
                dto.setEstudianteBloqueado(false);
            }

            // Datos del archivo y del paciente
            Archivo archivo = archivos.get(prestamo.getIdArchivo());
            if (archivo != null) {
                dto.setCodigoArchivo(archivo.getCodigoArchivo());

                if (archivo.getPaciente() != null) {
                    Paciente paciente = archivo.getPaciente();
                    dto.setPacienteCi(paciente.getCi() != null ? paciente.getCi().toString() : null);

                    if (paciente.getPersona() != null) {
                        dto.setPacienteNombre(nombreCompleto(paciente.getPersona()));
                    }
                }
            } else {
                dto.setCodigoArchivo("No disponible");
            }

            dtos.add(dto);
        }

        return dtos;
    }

    private String nombreCompleto(Persona persona) {
        if (persona == null) {
            return null;
        }
        String nombre = (persona.getNombre() != null ? persona.getNombre() : "") + " " +
                        (persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "") + " " +
                        (persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : "");
        return nombre.trim().replaceAll("\\s+", " ");
    }
}
