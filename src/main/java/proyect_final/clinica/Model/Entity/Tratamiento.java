package proyect_final.clinica.Model.Entity;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
@Getter
@Setter

@Entity
@Table(name = "tratamiento")
public class Tratamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamiento")
    private Long idTratamiento;


    @Column(name = "nombre_tratamiento", nullable = false, length = 100)
    private String nombreTratamiento;

    @Column(name="precio_tratamiento", nullable=false)
    private Double precioTratamiento;

    @Column(name = "descripcion_tratamiento", length = 255)
    private String descripcionTratamiento;

    @Column(name = "usu_reg_tra")
    private Integer usuRegTra;

    @Column(name = "usu_mod_tra")
    private Integer usuModTra;

    @CreationTimestamp
    @Column(name = "fech_reg_tra", updatable = false)
    private LocalDateTime fechRegTra;

    @UpdateTimestamp
    @Column(name = "fech_mod_tra")
    private LocalDateTime fechModTra;
}