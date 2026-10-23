package proyect_final.clinica.Model.Dto;
import proyect_final.clinica.Model.Entity.PrestamoActual;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PrestamoActualDTO {
    private Long idPrestamo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaLimitePrestamo;  // ← Cambiado
    private String tipoPrestamo;
    private String motivoPrestamo;          
    private String estadoPrestamo;           // ← Cambiado
    private LocalDate fechaDevolucion;        // Fecha real de devolución
    private Integer diasRetraso;              // Días de retraso calculados al devolver/vencer

    // Datos del estudiante (solo IDs porque tu entidad guarda IDs)
    private Long idEstudiante;
    private Integer codigoEstudiante;
    private String nombreEstudiante;
    private String emailEstudiante;
    private Boolean estudianteBloqueado;      // Para saber si el docente debe desbloquearlo

    // Datos del archivo (solo IDs porque tu entidad guarda IDs)
    private Long idArchivo;
    private String codigoArchivo;

    // Datos del paciente dueño del archivo prestado
    private String pacienteNombre;
    private String pacienteCi;
    
    // Constructor desde entidad (ahora CORREGIDO)
    public PrestamoActualDTO(PrestamoActual prestamo) {
        if (prestamo != null) {
            this.idPrestamo = prestamo.getId();
            this.fechaPrestamo = prestamo.getFechaPrestamo();
            this.fechaLimitePrestamo = prestamo.getFechaLimitePrestamo();
            this.tipoPrestamo = prestamo.getTipoPrestamo();
            this.motivoPrestamo = prestamo.getMotivoPrestamo();
            this.estadoPrestamo = prestamo.getEstadoPrestamo();
            this.fechaDevolucion = prestamo.getFechaDevolucion();
            this.diasRetraso = prestamo.getDiasRetraso();
            
            // Datos del estudiante (solo IDs)
            this.idEstudiante = prestamo.getIdEstudiante();
            
            // Datos del archivo (solo IDs)
            this.idArchivo = prestamo.getIdArchivo();
        }
    }
}