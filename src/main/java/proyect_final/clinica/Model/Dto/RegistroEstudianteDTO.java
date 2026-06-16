package proyect_final.clinica.Model.Dto;

import lombok.Data;
@Data
public class RegistroEstudianteDTO {
    

    private String nombre;

    private String apellidoPaterno;
    

    private String apellidoMaterno;
    

    private Integer edad;
    

    private String sexo;
    
    private String nombreUsuario;
    private String contraseña;
    private Boolean estadoUsuario;
    
    private Integer codigoEstudiante;
    private String gestion;
    private String email;
    
    private Boolean bloqueado;
    private Long idRoteActual;
}