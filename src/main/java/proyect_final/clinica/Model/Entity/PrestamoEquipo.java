package proyect_final.clinica.Model.Entity;
import lombok.*;

import java.sql.Time;
import java.time.LocalDate;

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
    @JoinColumn(name = "id_herramienta")
    private Long idHerramienta;

    @Column(name = "fech_entre_prestamo")
    private LocalDate fechaEntrePrestamo;
    
    @Column(name="hora_entre_equipo")
    private Time horaEntreEquipo;


    @Column(name="hora_devol_equipo")
    private Time horaDevolEquipo;

    @Column(name="estado_devolucion")
    private String estadoDevolucion;
}
