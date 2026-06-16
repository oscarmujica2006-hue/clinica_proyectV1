package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

@Entity
@Table(name = "consentimiento_foto")
public class ConsentimientoFoto {
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

}