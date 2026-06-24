package proyect_final.clinica.Web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/encargado_insumo")
public class EncargadoViewController {
    
    @GetMapping()
    public String paginaEncargadoInsumo() {
        return "encargado_insumos/encargado-vista";
    }
    @GetMapping("/solicitud-estudiante/soli_estudiante-vista")
    public String mostrarSolicitudEstudiante() {
        return "encargado_insumos/solicitud-estudiante/soli_estudiante-vista";
    }
    @GetMapping("/solicitud-insumo/pedido-vista")
    public String mostrarSolicitudInsumo() {
        return "encargado_insumos/solicitud-insumo/pedido-vista";
    }

    @GetMapping("/solicitud-abastecimiento/abastecimiento-vista")
    public String mostrarSolicitudAbastecimiento() {
        return "encargado_insumos/solicitud-abastecimiento/abastecimiento-vista";
    }

    @GetMapping("/consumido_insumo/consumido-vista")
    public String mostrarInsumosConsumidos() {
        return "encargado_insumos/consumido_insumo/consumido-vista";
    }

    @GetMapping("/registro_equi_ins/equi_insu-vista")
    public String mostrarEquiposInsumos() {
        return "encargado_insumos/registro_equi_ins/equi_insu-vista";
    }
}   