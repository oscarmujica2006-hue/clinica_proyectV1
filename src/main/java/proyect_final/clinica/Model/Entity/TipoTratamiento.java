package proyect_final.clinica.Model.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
@Getter
@Setter

@Entity
@Table(name = "tipo_tratamiento")


public class TipoTratamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_tratamiento")
    private Long idTipoTratamiento;


    @OneToOne
    @JoinColumn(name = "id_clinica", nullable = false)
    private Clinica clinica;
    @Column(name = "nombre_tipo", nullable = false, length = 100)
    private String nombreTipo;

    @Column(name = "descripcion_tipo", length = 255)
    private String descripcionTipo;
    
    @Column(name = "usu_reg_tipTra", length = 100)
    private String usuRegTipTra;

    @Column(name = "usu_mod_tipTra", length = 100)
    private String usuModTipTra;

    @CreationTimestamp
    @Column(name = "fech_reg_tipTra", updatable = false)
    private LocalDateTime fechRegTipTra;

    @UpdateTimestamp
    @Column(name = "fech_mod_tipTra")
    private LocalDateTime fechModTipTra;
}