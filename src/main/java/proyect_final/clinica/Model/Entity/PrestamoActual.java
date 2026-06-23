package proyect_final.clinica.Model.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Entity
@Getter


@Table(name = "prestamo_actual")
public class PrestamoActual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo_actual")
    private Long id;

    @Column(name = "id_archivo", nullable = false)
    private Long idArchivo;
    @Column(name = "id_estudiante", nullable = false)
    private Long idEstudiante;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;


    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimitePrestamo;
    @Column(name = "tipo_prestamo", nullable = false)
    private String tipoPrestamo;
    @Column(name = "encargado_prestamo", nullable = false)
    private String encargadoPrestamo;
    @Column(name = "fecha_devolucion", nullable = true)
    private LocalDate fechaDevolucion;
    
    @Column(name="motivo_prestamo", nullable = true )
    private String motivoPrestamo;

    @Column (name="estado_prestamo", nullable = false)
    private String estadoPrestamo;

    @Column(name="dias_retraso" , nullable = true)
    private Integer diasRetraso;

    @Column(name = "usu_reg_preAct", length = 100)
    private String usuRegPreAct;

    @Column(name = "usu_mod_preAct", length = 100)
    private String usuModPreAct;

    @CreationTimestamp
    @Column(name = "fech_reg_preAct", updatable = false)
    private LocalDateTime fechRegPreAct;

    @UpdateTimestamp
    @Column(name = "fech_mod_preAct")
    private LocalDateTime fechModPreAct;
}