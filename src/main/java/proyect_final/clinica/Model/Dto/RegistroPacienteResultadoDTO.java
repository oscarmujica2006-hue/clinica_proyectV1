package proyect_final.clinica.Model.Dto;

import lombok.Data;

@Data
public class RegistroPacienteResultadoDTO {
    private Boolean exito;
    private String mensaje;
    
    public RegistroPacienteResultadoDTO() {}
    
    public RegistroPacienteResultadoDTO(Boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
    }
}