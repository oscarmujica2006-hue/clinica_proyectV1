package proyect_final.clinica.Model.Entity;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name="encargado_radiografia")
public class EncargadoRadiografia {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encargado_radiografia")
    private Long idEncargadoRadiografia;


    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)  
    private Usuario usuario;
    

    @Column(name="codigo_encar_radiografia",nullable=false,length = 43)
    private Integer codigoEncarRadiografia;
    
    @Column(name = "usu_reg_usuRad")
    private Integer usuRegUsuRad;

    @Column(name = "usu_mod_usuRad")
    private Integer usuModUsuRad;

    @CreationTimestamp
    @Column(name = "fech_reg_usuRad", updatable = false)
    private LocalDateTime fechRegUsuRad;

    @UpdateTimestamp
    @Column(name = "fech_mod_usuRad")
    private LocalDateTime fechModUsuRad;
}