package proyect_final.clinica.Model.Dto;

public class EquipoDTO {
    private String codigo;
    private String nombre;
    private String estado;
    private Integer usuario;

    public EquipoDTO() {}

    public EquipoDTO(String codigo, String nombre, String estado, Integer usuario) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.estado = estado;
        this.usuario = usuario;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getUsuario() { return usuario; }
    public void setUsuario(Integer usuario) { this.usuario = usuario; }
}