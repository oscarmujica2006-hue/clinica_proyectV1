package proyect_final.clinica.Model.Dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PrestamoEquipoDTO {

    private Long idPrestamoEquipo;

    // Equipo
    private Long idEquipo;
    private String nombreEquipo;
    private String modeloEquipo;
    private String codigoPatrimonial;

    // Docente
    private Long idDocente;
    private String nombreDocente;

    // Estudiante
    private Long idEstudiante;
    private String nombreEstudiante;

    // Encargado
    private Long idEncargadoEquipo;
    private String nombreEncargado;

    // Fechas
    private LocalDate fechaSolicitud;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEsperada;
    private LocalDate fechaDevolucionReal;

    // Estados
    private String estadoSolicitud; // PENDIENTE_DOCENTE, APROBADO, RECHAZADO, ENTREGADO
    private String estadoDevolucion; // PENDIENTE, DEVUELTO, ATRASADO

    private String observaciones;
    private String motivoRechazo;
}