package proyect_final.clinica.Model.Entity;
import lombok.*;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "recuperacion_password")
public class RecuperacionPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recuperacion_password")
    private Long idRecuperacionPassword;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)  
    private Usuario usuario;

    @Column(name="token_usuario",nullable=false,length = 255)
    private String token;

    @Column(name="fecha_creacion",nullable=false)
    private java.time.LocalDateTime fechaCreacion;
    @Column(name="fecha_expiracion",nullable=false)
    private java.time.LocalDateTime fechaExpiracion;
    @Column(name="usado",nullable=false)
    private Boolean usado;

}
