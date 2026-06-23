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


@Table(name = "archivo")
public class Archivo  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_archivo")

    private Long idArchivo;

    @Column(name = "codigo_archivo", nullable = false, length = 100)
    private String codigoArchivo;


    @Column(name = "ubicacion_fisica", nullable = false, length = 200)
    private String ubicacionFisica;     
    
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)  
    private Paciente paciente;

    @Column(name = "usu_reg_arc", length = 100)
    private String usuRegArc;

    @Column(name = "usu_mod_arc", length = 100)
    private String usuModArc;

    @CreationTimestamp
    @Column(name = "fech_reg_arc", updatable = false)
    private LocalDateTime fechRegArc;

    @UpdateTimestamp
    @Column(name = "fech_mod_arc")
    private LocalDateTime fechModArc;
}
