package proyect_final.clinica.Model.Entity;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
@Setter
@Getter
@Entity
@Table(name="rote")



public class Rote  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rote")
    private Long idRote; 
    
    @Column(name="nombre_rote", length = 100)
    private String nombreRote;

    @Column(name = "usu_reg_rot")
    private Integer usuRegRot;

    @Column(name = "usu_mod_rot")
    private Integer usuModRot;

    @CreationTimestamp
    @Column(name = "fech_reg_rot", updatable = false)
    private LocalDateTime fechRegRot;

    @UpdateTimestamp
    @Column(name = "fech_mod_rot")
    private LocalDateTime fechModRot;
}