package proyect_final.clinica.Model.Dto;

import lombok.Data;
import java.util.List;

@Data
public class SolicitudRadiografiaDTO {
    private Long idDiagnosticoTratamiento;
    private Long idRadiografia;
    private String motivo;
    private List<Long> idsDientesPlan;
    private Long idEstudiante;  // ✅ AGREGAR
}