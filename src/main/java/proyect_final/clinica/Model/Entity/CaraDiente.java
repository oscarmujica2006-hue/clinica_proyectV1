package proyect_final.clinica.Model.Entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "cara_diente")
public class CaraDiente  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cara_diente")
    private Long idCaraDiente;

    @Column(name = "nombre_cara", nullable = false, unique = true, length = 30)
    private String nombreCara;
    @Column(name = "usu_reg_carDie")
    private Integer usuRegCarDie;

    @Column(name = "usu_mod_carDie")
    private Integer usuModCarDie;

    @CreationTimestamp
    @Column(name = "fech_reg_carDie", updatable = false)
    private LocalDateTime fechRegCarDie;

    @UpdateTimestamp
    @Column(name = "fech_mod_carDie")
    private LocalDateTime fechModCarDie;

    @OneToMany(mappedBy = "caraDiente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CaraDetRevi> carasDetRevi = new ArrayList<>();
}