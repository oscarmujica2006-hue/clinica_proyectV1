package proyect_final.clinica.Web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/radiografia")
public class RadiografiaWiewController {
    

    @GetMapping
    public String paginaCaja() {
        return "encargado-Radiografia/radiografia-vista";
    }   


    @GetMapping("/soli-radiografia/soli_radiografia-vista")
    public String mostrarRecibo() {
        return "encargado-Radiografia/soli-radiografia/soli_radiografia-vista";
    }

    @GetMapping("/recibos-estudiante/estudiante-recibo")
    public String mostrarRecibosPendientes() {
        return "encargado-Caja/recibos-estudiante/estudiante-recibo";
    }
 

  
}