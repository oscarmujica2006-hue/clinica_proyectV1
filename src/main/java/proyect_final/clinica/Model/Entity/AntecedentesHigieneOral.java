package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter

@Setter

@Entity
@Table(name = "antecedentes_higiene_oral")
public class AntecedentesHigieneOral {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_antecedentes_higiene_oral")
    private Long idAntecedentesHigieneOral;

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


}