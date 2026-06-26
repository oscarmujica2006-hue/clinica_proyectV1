package proyect_final.clinica.Web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/estudiantes")
public class EstudianteViewController {
    
     
    @GetMapping
    public String paginaEstudiantes() {
        return "estudiantes/estudiantes_pg";
    }   


    @GetMapping("/Registro-paciente/paciente-vista")
    public String mostrarRegistroPaciente() {
        return "estudiantes/Registro-paciente/paciente-vista";
    }

    @GetMapping("/Revision/revision")
    public String mostrarRevision() {
        return "estudiantes/Revision/revision";
    }    
    


    @GetMapping("/Revision/evolucion-clinica")
    public String mostrarEvolucionClinica(@RequestParam(required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("idDiagnostico", id);
        }
        return "estudiantes/Revision/evolucion-clinica";
    }



    @GetMapping("/consentimiento/consentimiento-vista")
    public String mostrarConsentimiento() {
        return "estudiantes/consentimiento/consentimiento-vista";
    }


    @GetMapping("/solicitud/insumos")
    public String mostrarInsumos() {
        return "estudiantes/solicitud/insumos";
    }
    

    @GetMapping("/seguimiento/seguimiento-vista")
    public String mostrarSeguimiento() {
        return "estudiantes/seguimiento/seguimiento-vista";
    }
    
    @GetMapping("/cupos-progreso/cupo-vista")
    public String mostrarCupo() {
        return "estudiantes/cupos-progreso/cupo-vista";
    }
    @GetMapping("/soli-equipo/solicitud_equipo-vista")
    public String mostrarSoliEquipo() {
        return "estudiantes/soli-equipo/solicitud_equipo-vista";
    }
    

}