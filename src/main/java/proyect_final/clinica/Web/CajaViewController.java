package proyect_final.clinica.Web;


    

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/caja")
public class CajaViewController {
    
     
    @GetMapping
    public String paginaCaja() {
        return "encargado-Caja/caja-vista";
    }   


    @GetMapping("/recibo/recibo-vista")
    public String mostrarRecibo() {
        return "encargado-Caja/recibo/recibo-vista";
    }

    @GetMapping("/recibos-estudiante/estudiante-recibo")
    public String mostrarRecibosPendientes() {
        return "encargado-Caja/recibos-estudiante/estudiante-recibo";
    }
 

  
}