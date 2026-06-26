package proyect_final.clinica.Model.Dto;
import lombok.*;


@Getter
@Setter

public class ProgresoTratamientoDTO {
    private Long idTratamiento;
    private String nombreTratamiento;
    private Integer requerido;    // cuposDisponibles
    private Integer realizado;    // cantidad validada
    private Integer faltante;     // requerido - realizado

    // constructores, getters y setters
    public ProgresoTratamientoDTO() {}

    public ProgresoTratamientoDTO(Long idTratamiento, String nombreTratamiento, Integer requerido, Integer realizado) {
        this.idTratamiento = idTratamiento;
        this.nombreTratamiento = nombreTratamiento;
        this.requerido = requerido;
        this.realizado = realizado;
        this.faltante = requerido - realizado;
    }

}