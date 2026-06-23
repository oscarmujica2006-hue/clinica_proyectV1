package proyect_final.clinica.Model.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter

@Setter

@Entity
@Table(name = "antecedente_higiene_oral")
public class AntecedenteHigieneOral  {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_antecedente_higiene_oral")
    private Long idAntecedenteHigieneOral;

    @Column(name = "utiliza_cepillo_dental")
    private Boolean utilizaCepilloDental;

    @Column(name = "utiliza_hilo_dental")
    private Boolean utilizaHiloDental;

    @Column(name = "utiliza_enjuague_bucal")
    private Boolean utilizaEnjuagueBucal;

    @Column(name = "frecuencia_cepillo", length = 50)
    private String frecuenciaCepillo;

    @Column(name = "durante_el_cepillado", length = 100)
    private String duranteElCepillado;

    @Column(name = "higiene_bucal", length = 20)
    private String higieneBucal;

    @Column(name = "usu_reg_antHig", length = 100)
    private String usuRegAntHig;

    @Column(name = "usu_mod_antHig", length = 100)
    private String usuModAntHig;

    @CreationTimestamp
    @Column(name = "fech_reg_antHig", updatable = false)
    private LocalDateTime fechRegAntHig;

    @UpdateTimestamp
    @Column(name = "fech_mod_antHig")
    private LocalDateTime fechModAntHig;
}