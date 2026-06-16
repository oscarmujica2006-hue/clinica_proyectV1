package proyect_final.clinica.Model.Dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String nombre;
    private String apellidos;
    private String semestre;
    private Long idUsuario;
    private String tipoUsuario;
    private Long idEspecifico;
    private String email;
    private Integer codigoEstudiante;

    public LoginResponse() {}

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // ⭐ CONSTRUCTOR PARA ESTUDIANTES (CORREGIDO)
    public LoginResponse(boolean success, String message, String nombre, 
                        String apellidos, String semestre, Long idUsuario, 
                        Long idEstudiante, String email, Integer codigoEstudiante) {
        this.success = success;
        this.message = message;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.semestre = semestre;
        this.idUsuario = idUsuario;
        this.idEspecifico = idEstudiante;
        this.tipoUsuario = "ESTUDIANTE";
        this.email = email;                    // ⭐ AGREGADO
        this.codigoEstudiante = codigoEstudiante;  // ⭐ AGREGADO
    }

    // Constructor para docentes
    public LoginResponse(boolean success, String message, String nombre, 
                        String apellidos, Long idUsuario, Long idDocente) {
        this.success = success;
        this.message = message;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.idUsuario = idUsuario;
        this.idEspecifico = idDocente;
        this.tipoUsuario = "DOCENTE";
    }

    // Getters y Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public Long getIdEspecifico() { return idEspecifico; }
    public void setIdEspecifico(Long idEspecifico) { this.idEspecifico = idEspecifico; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getCodigoEstudiante() { return codigoEstudiante; }
    public void setCodigoEstudiante(Integer codigoEstudiante) { this.codigoEstudiante = codigoEstudiante; }
}