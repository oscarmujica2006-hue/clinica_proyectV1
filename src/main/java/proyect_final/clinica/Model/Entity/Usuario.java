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
@Table(name = "usuario")

public class Usuario  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @ManyToOne
    @JoinColumn(name = "id_persona", nullable = false)  
    private Persona persona;
    
    @Column(name="user_name",nullable=false,length = 100)
    private String nombreUsuario;

    @Column(name="password",nullable=false,length = 100)
    private String contraseña;      

    @Column(name="email",nullable=false,length = 100)
    private String email;
    @Column(name="estado",nullable=false)
    private Boolean estado;
    @Column(name = "usu_reg_usu", length = 100)
    private String usuRegUsu;

    @Column(name = "usu_mod_usu", length = 100)
    private String usuModUsu;

    @CreationTimestamp
    @Column(name = "fech_reg_usu", updatable = false)
    private LocalDateTime fechRegUsu;

    @UpdateTimestamp
    @Column(name = "fech_mod_usu")
    private LocalDateTime fechModUsu;
}
