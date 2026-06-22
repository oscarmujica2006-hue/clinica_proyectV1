package proyect_final.clinica.Model.Dto;

import proyect_final.clinica.Model.Entity.Paciente;
import proyect_final.clinica.Model.Entity.Persona;

public class PacienteBusquedaDTO {
    // Campos principales
    private Long idPaciente;
    private String historialClinico;
    private Integer ci;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Integer edad;
    private Character sexo;
    private String nombreCompleto;
    
    // Campos adicionales del paciente
    private String lugarNacimiento;
    private String ocupacion;
    private String estadoCivil;
    private String direccion;
    private String telefono;
    private String gradoInstruccion;
    private String idioma;
    private String nacionesOriginarias;
    private String fechaNacimiento;

    public PacienteBusquedaDTO() {}

    public PacienteBusquedaDTO(Paciente paciente) {
        if (paciente == null) return;
        
        // Datos del paciente
        this.idPaciente = paciente.getIdPaciente();
        this.historialClinico = paciente.getHistorialClinico();
        this.ci = paciente.getCi();
        
        // Datos adicionales del paciente
        this.lugarNacimiento = paciente.getLugarNacimiento();
        this.ocupacion = paciente.getOcupacion();
        this.estadoCivil = paciente.getEstadoCivil();
        this.direccion = paciente.getDireccion();
        this.telefono = paciente.getTelefono();
        this.gradoInstruccion = paciente.getGradoInstruccion();
        this.idioma = paciente.getIdioma();
        this.nacionesOriginarias = paciente.getNacionesOriginarias();
        
        if (paciente.getFechaNacimiento() != null) {
            this.fechaNacimiento = paciente.getFechaNacimiento().toString();
        }
        
        // Datos de la persona
        Persona persona = paciente.getPersona();
        if (persona != null) {
            this.nombre = persona.getNombre();
            this.apellidoPaterno = persona.getApellidoPaterno();
            this.apellidoMaterno = persona.getApellidoMaterno();
            this.edad = persona.getEdad();
            this.sexo = persona.getSexo();
            
            // Construir nombre completo
            StringBuilder nombreCompletoBuilder = new StringBuilder();
            if (persona.getNombre() != null) nombreCompletoBuilder.append(persona.getNombre()).append(" ");
            if (persona.getApellidoPaterno() != null) nombreCompletoBuilder.append(persona.getApellidoPaterno()).append(" ");
            if (persona.getApellidoMaterno() != null) nombreCompletoBuilder.append(persona.getApellidoMaterno());
            this.nombreCompleto = nombreCompletoBuilder.toString().trim();
        }
    }

    // Getters y Setters completos
    public Long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }

    public String getHistorialClinico() { return historialClinico; }
    public void setHistorialClinico(String historialClinico) { this.historialClinico = historialClinico; }

    public Integer getCi() { return ci; }
    public void setCi(Integer ci) { this.ci = ci; }

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

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getLugarNacimiento() { return lugarNacimiento; }
    public void setLugarNacimiento(String lugarNacimiento) { this.lugarNacimiento = lugarNacimiento; }

    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }

    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getGradoInstruccion() { return gradoInstruccion; }
    public void setGradoInstruccion(String gradoInstruccion) { this.gradoInstruccion = gradoInstruccion; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getNacionesOriginarias() { return nacionesOriginarias; }
    public void setNacionesOriginarias(String nacionesOriginarias) { this.nacionesOriginarias = nacionesOriginarias; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
}