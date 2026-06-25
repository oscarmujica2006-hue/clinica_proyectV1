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
@Table(name = "odontograma_foto")
public class OdontogramaFoto  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_odontograma_foto")
    private Long id_odontogramaFoto;

    @ManyToOne
    @JoinColumn(name = "id_consulta", nullable = false)
    private Consulta consulta;

    private String rutaArchivo;      // Ruta pública para acceder a la imagen
    private String nombreOriginal;    // Nombre original del archivo
    private String tipoContenido;     // image/jpeg, etc.
    private Long tamano;             
    @Column(name = "usu_reg_odoFot")
    private Integer usuRegOdoFot;

    @Column(name = "usu_mod_odoFot")
    private Integer usuModOdoFot;

    @CreationTimestamp
    @Column(name = "fech_reg_odoFot", updatable = false)
    private LocalDateTime fechRegOdoFot;

    @UpdateTimestamp
    @Column(name = "fech_mod_odoFot")
    private LocalDateTime fechModOdoFot;
}