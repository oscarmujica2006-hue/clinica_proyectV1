package proyect_final.clinica.Web;
import org. springframework.stereotype.Controller   ;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/registro-doc")
public class RegistroDocViewController {
    @GetMapping 
    public String paginaRegistroDoc() {
        return "Registro-Usuarios/Registro-Docentes/registro_docente";
    }   

    
}
