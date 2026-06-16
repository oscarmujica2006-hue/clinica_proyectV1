package  proyect_final.clinica.Model.Dto;

public class DocenteDTO {
    private Long idDocente;
    private String nombreCompleto;
    private String especialidad;
    private Integer codigoDocente;
    private String contrato;
    private Boolean estado;
    private Long idClinica;
    private String nombreClinica;
    
    // Datos de Usuario y Persona
    private Long idUsuario;
    private String nombreUsuario;
    private Long idPersona;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Integer edad;
    private Character sexo;

    // Constructores
    public DocenteDTO() {}

    public DocenteDTO(Long idDocente, String nombreCompleto, String especialidad, Boolean estado) {
        this.idDocente = idDocente;
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
        this.estado = estado;
    }

    // Constructor completo
    public DocenteDTO(Long idDocente, String nombreCompleto, String especialidad, Integer codigoDocente, 
                     String contrato, Boolean estado, Long idClinica, String nombreClinica,
                     Long idUsuario, String nombreUsuario, Long idPersona, String nombre,
                     String apellidoPaterno, String apellidoMaterno, Integer edad, Character sexo) {
        this.idDocente = idDocente;
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
        this.codigoDocente = codigoDocente;
        this.contrato = contrato;
        this.estado = estado;
        this.idClinica = idClinica;
        this.nombreClinica = nombreClinica;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idPersona = idPersona;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.edad = edad;
        this.sexo = sexo;
    }

    // Getters y Setters
    public Long getIdDocente() { return idDocente; }
    public void setIdDocente(Long idDocente) { this.idDocente = idDocente; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public Integer getCodigoDocente() { return codigoDocente; }
    public void setCodigoDocente(Integer codigoDocente) { this.codigoDocente = codigoDocente; }

    public String getContrato() { return contrato; }
    public void setContrato(String contrato) { this.contrato = contrato; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public Long getIdClinica() { return idClinica; }
    public void setIdClinica(Long idClinica) { this.idClinica = idClinica; }

    public String getNombreClinica() { return nombreClinica; }
    public void setNombreClinica(String nombreClinica) { this.nombreClinica = nombreClinica; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public Long getIdPersona() { return idPersona; }
    public void setIdPersona(Long idPersona) { this.idPersona = idPersona; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public Character getSexo() { return sexo; }
    public void setSexo(Character sexo) { this.sexo = sexo; }
}