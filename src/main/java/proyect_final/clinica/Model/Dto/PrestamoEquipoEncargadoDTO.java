package proyect_final.clinica.Model.Dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Time;

@Data
public class PrestamoEquipoEncargadoDTO {
    private Long idPrestamoEquipo;
    private LocalDate fechaEntrePrestamo;
    private String estadoDevolucion;
    private String observacionPrestamo;
    private LocalDateTime fechRegPreEqu;
    
    // Equipo
    private Long idEquipo;
    private String nombreEquipo;
    private String codigoEquipo;
    private String estadoEquipo;
    
    // Estudiante
    private Long idEstudiante;
    private String nombreEstudiante;
    private String apellidoEstudiante;
    
    // Docente
    private Long idDocente;
    private String nombreDocente;
    private String apellidoDocente;
    
    // Encargado (para mostrar quién lo entregó)
    private Long idEncargado;
    private String nombreEncargado;
}