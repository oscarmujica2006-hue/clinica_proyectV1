
package proyect_final.clinica.Model.Dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
public class ProgresoMateriaDTO {
    private Long idMateria;
    private String nombreMateria;
    private List<ProgresoTratamientoDTO> tratamientos;

 
}