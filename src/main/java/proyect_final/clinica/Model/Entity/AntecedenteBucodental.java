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
@Table(name = "antecedente_bucodental")
public class AntecedenteBucodental {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_antecedente_bucodental")
    private Long idAntecedenteBucodentale;

    @Column(name = "fecha_revision")
    private LocalDate fechaRevision;

    @Column(length = 100)
    private Boolean Fuma;

    @Column(length = 100)        
    private Boolean Bebe;

    @Column(length=100)
    private Boolean Coca;

    @Column(length = 100)
    private String otrosHabitos;

    @Column(name = "usu_reg_antBuc", length = 100)
    private String usuRegAntBuc;

    @Column(name = "usu_mod_antBuc", length = 100)
    private String usuModAntBuc;

    @CreationTimestamp
    @Column(name = "fech_reg_antBuc", updatable = false)
    private LocalDateTime fechRegAntBuc;

    @UpdateTimestamp
    @Column(name = "fech_mod_antBuc")
    private LocalDateTime fechModAntBuc;
    
}