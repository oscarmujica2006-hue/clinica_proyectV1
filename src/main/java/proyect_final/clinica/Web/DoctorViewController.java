package proyect_final.clinica.Web;

import  proyect_final.clinica.Model.Entity.Estudiante;
import  proyect_final.clinica.Model.Entity.PrestamoActual;
import proyect_final.clinica.Model.Dao.EstudianteRepository;
import proyect_final.clinica.Service.PrestamoActualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
public class DoctorViewController {

    @Autowired
    private PrestamoActualService prestamoActualService;

    @Autowired
    private EstudianteRepository estudianteRepository; // ← AGREGAR ESTO

    @GetMapping
    public String paginaDoctor() {
        return "doctor/doctor";
    }   
    


    @GetMapping("/prestamo/prestamo-vista")
    public String mostrarRegistro() {
        return "doctor/prestamo/prestamo-vista"; 
    }



    @GetMapping("/historial_clinico/historial_clinico_ENPROCESO")
    public String mostrarDoctor() {
        return "doctor/historial_clinico/historial_clinico_ENPROCESO"; 
    }    

    @GetMapping("/estado_consentimiento/estadoConsentimiento")
    public String mostrarConsentimientoEstado() {
        return "doctor/estado_consentimiento/estadoConsentimiento"; 
    }   

    @GetMapping("/desbloqueoEstudiante/desbloqueo")
    public String mostrarDesbloqueoEstudiante(
            @RequestParam(required = false) Long estudiante,
            @RequestParam(required = false) Long archivo,
            @RequestParam(required = false) String estado,
            Model model) {
        
        List<PrestamoActual> prestamos;
        
        // Aplicar filtros si existen
        if (estudiante != null) {
            prestamos = prestamoActualService.buscarPorIdEstudiante(estudiante);
        } else if (archivo != null) {
            prestamos = prestamoActualService.buscarPorIdArchivo(archivo);
        } else if (estado != null && !estado.isEmpty()) {
            prestamos = prestamoActualService.buscarPorEstado(estado);
        } else {
            prestamos = prestamoActualService.obtenerTodos();
        }
        
        // 🔹 NUEVO: Crear un mapa de estudiantes bloqueados
        Map<Long, Boolean> estudiantesBloqueados = new HashMap<>();
        for (PrestamoActual p : prestamos) {
            if (!estudiantesBloqueados.containsKey(p.getIdEstudiante())) {
                Optional<Estudiante> estudianteOpt = estudianteRepository.findById(p.getIdEstudiante());
                estudianteOpt.ifPresent(e -> estudiantesBloqueados.put(p.getIdEstudiante(), e.getBloqueado()));
            }
        }
        
        // Calcular estadísticas
        long totalPrestamos = prestamos.size();
        long prestamosActivos = prestamos.stream()
                .filter(p -> "ACTIVO".equals(p.getEstadoPrestamo()))
                .count();
        long prestamosVencidos = prestamos.stream()
                .filter(p -> "VENCIDO".equals(p.getEstadoPrestamo()))
                .count();
        long prestamosDevueltos = prestamos.stream()
                .filter(p -> "DEVUELTO".equals(p.getEstadoPrestamo()))
                .count();
        
        model.addAttribute("prestamos", prestamos);
        model.addAttribute("estudiantesBloqueados", estudiantesBloqueados); // ← AGREGAR ESTO
        model.addAttribute("totalPrestamos", totalPrestamos);
        model.addAttribute("prestamosActivos", prestamosActivos);
        model.addAttribute("prestamosVencidos", prestamosVencidos);
        model.addAttribute("prestamosDevueltos", prestamosDevueltos);
        
        return "doctor/desbloqueoEstudiante/desbloqueo";
    }


    @GetMapping("/solicitud-estudiante/solicitud_det-vista")
    public String mostrarSolicitudVista() {
        return "doctor/solicitud-estudiante/solicitud_det-vista";
    }


    @GetMapping("/Registro-Estudiante/estudiante-vista")
    public String mostrarRegistroEstudiante() {
        return "doctor/Registro-Estudiante/estudiante-vista";
    }


    @GetMapping("/Archivo/archivo-vista")
    public String mostrarArchivoVista() {
        return "doctor/Archivo/archivo-vista";
    }


    @GetMapping("cupo-estudiante/cupo-vista")
    public String mostrarVistaCupo() {
        return "doctor/cupo-estudiante/cupo-vista";
    }
}