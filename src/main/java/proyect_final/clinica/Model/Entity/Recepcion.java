package proyect_final.clinica.Model.Entity;

import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
@Getter
@Setter
@Entity
@Table(name="usuario_recepcion")
public class Recepcion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recepcion")
    private Long idRecepcion;


    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)  
    private Usuario usuario;
    

    @Column(name="codigo_recepcion",nullable=false,length = 43)
    private Integer codigoRecepcion;
    
    @Column(name = "usu_reg_usuRecp", length = 100)
    private Integer usuRegUsuRecp;

    @Column(name = "usu_mod_usuRecp", length = 100)
    private Integer usuModUsuRecp;

    @CreationTimestamp
    @Column(name = "fech_reg_usuRecp", updatable = false)
    private LocalDateTime fechRegUsuRecp;

    @UpdateTimestamp
    @Column(name = "fech_mod_usuRecp")
    private LocalDateTime fechModUsuRecp;
}