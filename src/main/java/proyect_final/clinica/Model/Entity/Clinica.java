package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    // Constructores
    public Clinica() {}

    public Clinica(String nombreClinica, String turno, Integer capacidadMaxima) {
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

    public String getNombreClinica() {
        return nombreClinica;
    }

    public void setNombreClinica(String nombreClinica) {
        this.nombreClinica = nombreClinica;
    }
    public Rote getRote() {
        return rote;
    }

    public void setRote(Rote rote) {
        this.rote = rote;
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
}