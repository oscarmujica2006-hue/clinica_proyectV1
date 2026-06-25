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
    
    // Datos del estudiante (solo IDs porque tu entidad guarda IDs)
    private Long idEstudiante;
    private Integer codigoEstudiante;
    private String nombreEstudiante;
    private String emailEstudiante;
    
    // Datos del archivo (solo IDs porque tu entidad guarda IDs)
    private Long idArchivo;
    private String codigoArchivo;
    
    // Constructor desde entidad (ahora CORREGIDO)
    public PrestamoActualDTO(PrestamoActual prestamo) {
        if (prestamo != null) {
            this.idPrestamo = prestamo.getId();
            this.fechaPrestamo = prestamo.getFechaPrestamo();
            this.fechaLimitePrestamo = prestamo.getFechaLimitePrestamo();
            this.tipoPrestamo = prestamo.getTipoPrestamo();
            this.motivoPrestamo = prestamo.getMotivoPrestamo();
            this.estadoPrestamo = prestamo.getEstadoPrestamo();
            
            // Datos del estudiante (solo IDs)
            this.idEstudiante = prestamo.getIdEstudiante();
            
            // Datos del archivo (solo IDs)
            this.idArchivo = prestamo.getIdArchivo();
        }
    }
}