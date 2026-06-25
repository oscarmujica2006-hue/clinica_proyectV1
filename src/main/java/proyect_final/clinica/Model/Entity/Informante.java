package proyect_final.clinica.Model.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@Entity
@Table(name = "informante")
public class Informante{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_informante")
    private Long idInformante;

    @Column(length = 100)
    private String nombres;

    @Column(name = "apellido_paterno", length = 100)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 100)
    private String apellidoMaterno;

    @Column(length = 200)
    private String direccion;

    @Column(length = 20)
    private String telefono;

     @Column(name = "usu_reg_inf")
    private Integer usuRegInf;

    @Column(name = "usu_mod_inf")
    private Integer usuModInf;

    @CreationTimestamp
    @Column(name = "fech_reg_inf", updatable = false)
    private LocalDateTime fechRegInf;

    @UpdateTimestamp
    @Column(name = "fech_mod_inf")
    private LocalDateTime fechModInf;
}
