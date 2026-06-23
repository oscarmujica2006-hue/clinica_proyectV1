package proyect_final.clinica.Model.Entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "diagnostico")
public class Diagnostico  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diagnostico")
    private Long idDiagnostico;

    @ManyToOne
    @JoinColumn(name = "id_consulta", nullable = false)
    private Consulta consulta;

    @ManyToOne
    @JoinColumn(name = "id_revision")
    private Revision revision;

    @Column(name = "descripcion")
    private String descripcion;

    // CPO
    @Column(name = "cpo_cariados")
    private Integer cpoCariados;

    @Column(name = "cpo_perdidos")
    private Integer cpoPerdidos;

    @Column(name = "cpo_obturados")
    private Integer cpoObturados;

    @Column(name = "cpo_total")
    private Integer cpoTotal;

    @Column(name = "piezas_sanas_permanentes")
    private Integer piezasSanash4Permanentes;

    @Column(name = "total_piezas_permanentes")
    private Integer totalPiezasPermanentes;

    // CEO
    @Column(name = "ceo_cariados")
    private Integer ceoCariados;

    @Column(name = "ceo_extraidos")
    private Integer ceoExtraidos;

    @Column(name = "ceo_obturados")
    private Integer ceoObturados;

    @Column(name = "ceo_total")
    private Integer ceoTotal;

    @Column(name = "piezas_sanas_temporales")
    private Integer piezasSanash4Temporales;

    @Column(name = "total_piezas_temporales")
    private Integer totalPiezasTemporales;
    @Column(name = "usu_reg_dia", length = 100)
    private String usuRegDia;

    @Column(name = "usu_mod_dia", length = 100)
    private String usuModDia;

    @CreationTimestamp
    @Column(name = "fech_reg_dia", updatable = false)
    private LocalDateTime fechRegDia;

    @UpdateTimestamp
    @Column(name = "fech_mod_dia")
    private LocalDateTime fechModDia;

    @OneToMany(mappedBy = "diagnostico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiagnosticoTratamiento> diagnosticosTratamientos = new ArrayList<>();

}