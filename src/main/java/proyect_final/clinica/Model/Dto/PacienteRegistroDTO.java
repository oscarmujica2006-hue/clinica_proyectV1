package proyect_final.clinica.Model.Dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PacienteRegistroDTO {
    // Datos de Persona
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Character sexo;
    private Integer usuRegPer;
    
    // Datos de Paciente
    private String historialClinico;
    private String lugarNacimiento;
    private LocalDate fechaNacimiento;
    private String ocupacion;
    private String direccion;
    private String telefono;
    private String gradoInstruccion;
    private String estadoCivil;
    private Integer ci;
    private Long idTipoPaciente;
    private Integer usuRegPac;
    
    // Datos de Archivo
    private String codigoArchivo;
    private String ubicacionFisica;
    private Integer usuRegArc;
    
    // Datos Opcionales
    private String nacionesOriginarias;
    private String idioma;
    private String patologiaFamiliar;
}