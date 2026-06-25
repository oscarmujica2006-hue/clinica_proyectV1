package proyect_final.clinica.Model.Entity;
import lombok.Setter;
import lombok.Getter;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;


@Getter
@Setter

@Entity
@Table(name = "radiografia")

public class Radiografia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_radiografia")
    private Long idRadiografia;

    @Column(name = "nombre_rayo", nullable = false, length = 255)
    private String nombreRayo;

    @Column(name = "precio_rayo", nullable = false, length = 500)
    private Double precioRayo;
    
    @Column(name = "usu_reg_rad")
    private Integer usuRegRad;

    @Column(name = "usu_mod_rad")
    private Integer usuModRad;

    @CreationTimestamp
    @Column(name = "fech_reg_rad", updatable = false)
    private LocalDateTime fechRegRad;

    @UpdateTimestamp
    @Column(name = "fech_mod_rad")
    private LocalDateTime fechModRad;
}