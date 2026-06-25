package proyect_final.clinica.Web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/archivos")
public class ArchivosViewController {
    @GetMapping
    public String paginagestion() {
        return "archivos_encargado/dasboard-archivo"; 
    }   
    
    @GetMapping("/Registro-paciente/paciente-vista")
    public String mostrarRegistroPaciente() {
        return "archivos_encargado/Registro-paciente/paciente-vista"; 
    }  

    // @GetMapping("/gestion/gestion-vista")
    // public String mostrarGestion() {
    //     return "archivos_encargado/gestion/gestion-vista"; 
    // }    

    @GetMapping("/prestamo/prestamo-vista")
    public String mostrarRegistro() {
        return "archivos_encargado/prestamo/prestamo-vista"; 
    }
    @GetMapping("/Archivo/archivo-vista")
    public String mostrarBusquedaArchivo() {
        return "archivos_encargado/Archivo/archivo-vista"; 
    }

        @GetMapping("historial_clinico/historial_clinico_ENPROCESO")
    public String mostrarBusquedaHistorial() {
        return "doctor/historial_clinico/historial_clinico_ENPROCESO"; 
    }

}
