package proyect_final.clinica.Model.Entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@Entity
@Table(name = "examen_intra_oral")
public class ExamenIntraOral {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_examen_intra_oral")
    private Long idExamenIntraOral;

    @Column(length = 100)
    private String labios;

    @Column(length = 100)
    private String lengua;

    @Column(length = 100)
    private String paladar;

    @Column(name = "piso_de_la_boca", length = 100)
    private String pisoDeLaBoca;

    @Column(name = "mucosa_yugal", length = 100)
    private String mucosaYugal;

    @Column(length = 100)
    private String encias;

    @Column(name = "utiliza_protesis_dental")
    private Boolean utilizaProtesisDental;
    @Column(name = "usu_reg_exaInt")
    private Integer usuRegExaInt;

    @Column(name = "usu_mod_exaInt")
    private Integer usuModExaInt;

    @CreationTimestamp
    @Column(name = "fech_reg_exaInt", updatable = false)
    private LocalDateTime fechRegExaInt;

    @UpdateTimestamp
    @Column(name = "fech_mod_exaInt")
    private LocalDateTime fechModExaInt;
}