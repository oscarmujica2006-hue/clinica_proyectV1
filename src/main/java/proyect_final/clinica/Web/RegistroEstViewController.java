package proyect_final.clinica.Web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.RequestMapping;




@Controller
@RequestMapping("/registro-est")

public class RegistroEstViewController {
  
    @GetMapping
    public String paginaRegistroEst() {
        return "Registro-Usuarios/Registro-Estudiante/estudiante-vista";
    }   
}
