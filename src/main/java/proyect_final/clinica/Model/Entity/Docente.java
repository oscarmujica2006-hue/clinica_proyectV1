package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

@Entity
@Table(name = "usuariodocente")
public class Docente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_docente")
    private Long idDocente;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name="codigo_doc",nullable=false)
    private Integer codigoDocente;

    @Column(name = "especialidad", length = 100)
    private String especialidad;

    @Column(name = "contrato", length = 100)
    private String contrato;

    @Column(name = "estado_doc", nullable = false )
    private    Boolean estado;
    @ManyToOne
    @JoinColumn(name = "id_clinica")
    private Clinica clinica;

    @OneToMany(mappedBy = "docente")
    private List<Consentimiento> consentimientos;
}