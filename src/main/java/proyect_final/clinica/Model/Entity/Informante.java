package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "informante")
public class Informante {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_informante")
    private Long idInformante;

    @Column(length = 100)
    private String nombres;

    @Column(name = "apellido_paterno", length = 100)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 100)
    private String apellidoMaterno;

    @Column(length = 200)
    private String direccion;

    @Column(length = 20)
    private String telefono;

    // Constructor sin parámetros (OBLIGATORIO)
    public Informante() {
    }

    // Constructor con parámetros
    public Informante(String nombres, String apellidoPaterno, String apellidoMaterno, String direccion, String telefono) {
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    // Getters y Setters
    public Long getIdInformante() {
        return idInformante;
    }

    public void setIdInformante(Long idInformante) {
        this.idInformante = idInformante;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}