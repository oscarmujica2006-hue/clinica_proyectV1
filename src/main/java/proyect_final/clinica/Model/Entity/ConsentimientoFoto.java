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
@Table(name = "consentimiento_foto")
public class ConsentimientoFoto  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consentimiento_foto")
    private Long id_consentimientoFoto;

    @ManyToOne
    @JoinColumn(name = "id_consentimiento", nullable = false)
    private Consentimiento consentimiento;

    private String rutaArchivoConsentimiento;      
    private String nombreConsentimientoOriginal;   
    private String tipoContenidoConsentimiento ;    
    private Long tamanoConsentimiento;             

    @Column(name = "usu_reg_conFot", length = 100)
    private String usuRegConFot;

    @Column(name = "usu_mod_conFot", length = 100)
    private String usuModConFot;

    @CreationTimestamp
    @Column(name = "fech_reg_conFot", updatable = false)
    private LocalDateTime fechRegConFot;

    @UpdateTimestamp
    @Column(name = "fech_mod_conFot")
    private LocalDateTime fechModConFot;
}