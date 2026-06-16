package proyect_final.clinica.Model.Entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "cara_diente")
public class CaraDiente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cara_diente")
    private Long idCaraDiente;

    @Column(name = "nombre_cara", nullable = false, unique = true, length = 30)
    private String nombreCara;

    @OneToMany(mappedBy = "caraDiente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CaraDetRevi> carasDetRevi = new ArrayList<>();
}