package proyect_final.clinica.Model.Entity;
import lombok.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name="prestamo_equipo")
public class PrestamoEquipo {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo_equipo")
    private Long idPrestamoEquipo;


    @ManyToOne
    @JoinColumn(name = "id_equipo")
    private Equipo idEquipo;


    @ManyToOne
    @JoinColumn(name="id_estudiante")
    private Estudiante idEstudiante;

    @ManyToOne
    @JoinColumn(name="id_docente")
    private Docente idDocente;
    @Column(name = "fech_entre_prestamo")
    private LocalDate fechaEntrePrestamo;

    @ManyToOne
    @JoinColumn(name="id_encargado_insumo")
    private EncargadoInsumo idEncargadoInsumo;
    
    @Column(name="hora_entre_equipo")
    private Time horaEntreEquipo;

    @Column(name="hora_solicitud")
    private Time horaSolicitud;
    @Column(name="hora_devol_equipo")
    private Time horaDevolEquipo;

    @Column(name="esta_prest_equipo")
    private String estadoDevolucion;

    @Column(name="observacion_prestamo")
    private String observacionPrestamo;


    @Column(name = "usu_reg_preEqu", length = 100)
    private String usuRegPreEqu;

    @Column(name = "usu_mod_preEqu", length = 100)
    private String usuModPreEqu;

    @CreationTimestamp
    @Column(name = "fech_reg_preEqu", updatable = false)
    private LocalDateTime fechRegPreEqu;

    @UpdateTimestamp
    @Column(name = "fech_mod_preEqu")
    private LocalDateTime fechModPreEqu;
}