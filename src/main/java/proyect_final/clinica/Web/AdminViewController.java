package proyect_final.clinica.Web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {
    @GetMapping
    public String paginaAdmin() {
        return "admin/admin-panel"; 

    }   

    @GetMapping("/historial-clinico")
    public String paginaHistorialClinico() {
        return "admin/historial-clinico"; 
    }

    
    @GetMapping("/tipos-tratamiento/vista-tipo")
    public String paginaTipoTratamiento() {
        return "admin/tipos-tratamiento/vista-tipo"; 
    }

    @GetMapping("/tratamientos/formulario")
    public String paginaTratamientos() {
        return "admin/tratamientos/formulario"; 
    }

    @GetMapping("/inscripcion/vista-materia")
    public String paginaInscripcion() {
        return "admin/inscripcion/vista-materia"; 
    }
    @GetMapping("/aprobacion/vista-aprobacion")
    public String paginaAprobacion() {
        return "admin/aprobacion/vista-aprobacion"; 
    }

    @GetMapping("/cupo/vista-cupo")
    public String paginaCupo() {
        return "admin/cupo/vista-cupo"; 
    }
}