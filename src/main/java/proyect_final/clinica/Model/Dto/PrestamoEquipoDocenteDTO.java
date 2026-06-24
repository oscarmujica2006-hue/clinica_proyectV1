package proyect_final.clinica.Model.Dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Time;

@Data
public class PrestamoEquipoDocenteDTO {
    private Long idPrestamoEquipo;
    private Long idEquipo;
    private String nombreEquipo;
    private String codigoEquipo;
    private String estadoEquipo;
    private Long idEstudiante;
    private String nombreEstudiante;
    private Long idDocente;
    private String nombreDocente;
    private LocalDate fechaEntrePrestamo;
    private Time horaEntreEquipo;
    private Time horaDevolEquipo;
    private String estadoDevolucion;
    private String observacionPrestamo;
    private LocalDateTime fechRegPreEqu;
}