package proyect_final.clinica.Web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/director")
public class DirectorViewController {
    @GetMapping
    public String paginaDirector() {
        return "director/director-vista";
    }

    @GetMapping("/solicitud-abastecimiento/abastecimiento-vista")
    public String mostrarReportes() {
        return "/director/solicitud-abastecimiento/abastecimiento-vista";
    }
    @GetMapping("/Registro-Docentes/registro_docente")
    public String mostrarRegistroDocente() {
        return "/director/Registro-Docentes/registro_docente";
    }
}
