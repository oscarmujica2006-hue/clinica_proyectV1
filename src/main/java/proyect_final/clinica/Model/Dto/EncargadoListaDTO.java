package proyect_final.clinica.Model.Dto;

import lombok.Data;

@Data
public class EncargadoListaDTO {
    private Long idEncargado;
    private String rol;
    private String nombreCompleto;
    private String nombreUsuario;
    private String email;
    private Boolean estado;
    
    // Campos específicos de INSUMO
    private String areaResponsabilidad;
    private String codigoAlmacen;
    
    // Campos específicos de RADIOGRAFIA
    private Integer codigoRadiografia;
    
    // Campos específicos de RECEPCION
    private Integer codigoRecepcion;
}