package proyect_final.clinica.Model.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Getter
@Setter

@Entity
@Table(name = "usuario_clinica")
public class Director {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_clinica")
    private Long idUsuarioClinica;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name="fecha_ini",nullable=false)
    private LocalDate fechaInicio;


    @Column(name = "fecha_fin", nullable = false )
    private LocalDate fechaFin;
    @Column(name = "usu_reg_usuCli", length = 100)
    private String usuRegUsuCli;

    @Column(name = "usu_mod_usuCli", length = 100)
    private String usuModUsuCli;

    @CreationTimestamp
    @Column(name = "fech_reg_usuCli", updatable = false)
    private LocalDateTime fechRegUsuCli;

    @UpdateTimestamp
    @Column(name = "fech_mod_usuCli")
    private LocalDateTime fechModUsuCli;
}