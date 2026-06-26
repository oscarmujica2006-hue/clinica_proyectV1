package proyect_final.clinica.Model.Entity;
import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinica")
public class Clinica {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_clinica")
    private Long idClinica;

    @ManyToOne
    @JoinColumn(name = "id_rote", nullable = false)
    private Rote rote;

    @Column(name = "nombre_clinica", length = 100, nullable = false)
    private String nombreClinica;

    @ManyToOne
    @JoinColumn(name = "id_turno", nullable = false)
    private Turno turno;

    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima;

    @OneToMany(mappedBy = "clinica")
    @JsonIgnore
    private List<Docente> docentes;

    @OneToMany(mappedBy = "clinica")
    @JsonIgnore
    private List<Materia> materias;


    @Column(name = "usu_reg_cli")
    private Integer usuRegCli;

    @Column(name = "usu_mod_cli")
    private Integer usuModCli;

    @CreationTimestamp
    @Column(name = "fech_reg_cli", updatable = false)
    private LocalDateTime fechRegCli;

    @UpdateTimestamp
    @Column(name = "fech_mod_cli")
    private LocalDateTime fechModCli;

    // Constructores
    public Clinica() {}

    public Clinica(String nombreClinica, Integer capacidadMaxima) {
        this.nombreClinica = nombreClinica;
        this.capacidadMaxima = capacidadMaxima;
    }

    // Getters y Setters
    public Long getIdClinica() {
        return idClinica;
    }

    public void setIdClinica(Long idClinica) {
        this.idClinica = idClinica;
    }

    public Rote getRote() {
        return rote;
    }

    public void setRote(Rote rote) {
        this.rote = rote;
    }

    public String getNombreClinica() {
        return nombreClinica;
    }

    public void setNombreClinica(String nombreClinica) {
        this.nombreClinica = nombreClinica;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public List<Docente> getDocentes() {
        return docentes;
    }

    public void setDocentes(List<Docente> docentes) {
        this.docentes = docentes;
    }

    public List<Materia> getMaterias() {
        return materias;
    }

    public void setMaterias(List<Materia> materias) {
        this.materias = materias;
    }
    public Integer getUsuRegCli() {
        return usuRegCli;
    }

    public void setUsuRegCli(Integer usuRegCli) {
        this.usuRegCli = usuRegCli;
    }

    public Integer getUsuModCli() {
        return usuModCli;
    }

    public void setUsuModCli(Integer usuModCli) {
        this.usuModCli = usuModCli;
    }

    public LocalDateTime getFechRegCli() {
        return fechRegCli;
    }

    public void setFechRegCli(LocalDateTime fechRegCli) {
        this.fechRegCli = fechRegCli;
    }

    public LocalDateTime getFechModCli() {
        return fechModCli;
    }

    public void setFechModCli(LocalDateTime fechModCli) {
        this.fechModCli = fechModCli;
    }
}