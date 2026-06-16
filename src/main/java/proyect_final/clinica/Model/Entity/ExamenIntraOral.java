package proyect_final.clinica.Model.Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "examen_intra_oral")
public class ExamenIntraOral {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_examen_intra_oral")
    private Long idExamenIntraOral;

    @Column(length = 100)
    private String labios;

    @Column(length = 100)
    private String lengua;

    @Column(length = 100)
    private String paladar;

    @Column(name = "piso_de_la_boca", length = 100)
    private String pisoDeLaBoca;

    @Column(name = "mucosa_yugal", length = 100)
    private String mucosaYugal;

    @Column(length = 100)
    private String encias;

    @Column(name = "utiliza_protesis_dental")
    private Boolean utilizaProtesisDental;

    // Constructores
    public ExamenIntraOral() {
    }

    public ExamenIntraOral(String labios, String lengua, String paladar, String pisoDeLaBoca, String mucosaYugal, String encias, Boolean utilizaProtesisDental) {
        this.labios = labios;
        this.lengua = lengua;
        this.paladar = paladar;
        this.pisoDeLaBoca = pisoDeLaBoca;
        this.mucosaYugal = mucosaYugal;
        this.encias = encias;
        this.utilizaProtesisDental = utilizaProtesisDental;
    }

    // Getters y Setters
    public Long getIdExamenIntraOral() {
        return idExamenIntraOral;
    }

    public void setIdExamenIntraOral(Long idExamenIntraOral) {
        this.idExamenIntraOral = idExamenIntraOral;
    }

    public String getLabios() {
        return labios;
    }

    public void setLabios(String labios) {
        this.labios = labios;
    }

    public String getLengua() {
        return lengua;
    }

    public void setLengua(String lengua) {
        this.lengua = lengua;
    }

    public String getPaladar() {
        return paladar;
    }

    public void setPaladar(String paladar) {
        this.paladar = paladar;
    }

    public String getPisoDeLaBoca() {
        return pisoDeLaBoca;
    }

    public void setPisoDeLaBoca(String pisoDeLaBoca) {
        this.pisoDeLaBoca = pisoDeLaBoca;
    }

    public String getMucosaYugal() {
        return mucosaYugal;
    }

    public void setMucosaYugal(String mucosaYugal) {
        this.mucosaYugal = mucosaYugal;
    }

    public String getEncias() {
        return encias;
    }

    public void setEncias(String encias) {
        this.encias = encias;
    }

    public Boolean getUtilizaProtesisDental() {
        return utilizaProtesisDental;
    }

    public void setUtilizaProtesisDental(Boolean utilizaProtesisDental) {
        this.utilizaProtesisDental = utilizaProtesisDental;
    }
}