package proyect_final.clinica.Web;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import proyect_final.clinica.Model.Dao.SolicitudRadiografiaRepository;
import proyect_final.clinica.Model.Dto.RadiografiaPorClinicaDTO;

@Controller
@RequestMapping("/director")
public class DirectorViewController {
    @Autowired
    private SolicitudRadiografiaRepository solicitudRadiografiaRepository;

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

    @GetMapping("/reporte-radiografias")
    public String mostrarReporteRadiografias(Model model) {
        List<RadiografiaPorClinicaDTO> reporte =
                solicitudRadiografiaRepository.resumirRadiografiasRealizadasPorClinica();
        Map<Long, String> nombresClinicas = new LinkedHashMap<>();
        Map<Long, Long> totalesPorClinica = new LinkedHashMap<>();

        for (RadiografiaPorClinicaDTO fila : reporte) {
            nombresClinicas.put(fila.getIdClinica(), fila.getNombreClinica());
            totalesPorClinica.merge(fila.getIdClinica(), fila.getCantidad(), Long::sum);
        }

        long mayorCantidad = totalesPorClinica.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        List<String> clinicasConMayorCantidad = totalesPorClinica.entrySet().stream()
                .filter(entry -> entry.getValue() == mayorCantidad)
                .map(entry -> nombresClinicas.get(entry.getKey()))
                .toList();

        model.addAttribute("reporte", reporte);
        model.addAttribute("totalRadiografias",
                totalesPorClinica.values().stream().mapToLong(Long::longValue).sum());
        model.addAttribute("mayorCantidad", mayorCantidad);
        model.addAttribute("clinicasConMayorCantidad", clinicasConMayorCantidad);
        return "director/reporte-radiografias";
    }
}
