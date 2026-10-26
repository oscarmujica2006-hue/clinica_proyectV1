package proyect_final.clinica.Model.Dto;

public class RadiografiaPorClinicaDTO {
    private final Long idClinica;
    private final String nombreClinica;
    private final String nombreRadiografia;
    private final Long cantidad;

    public RadiografiaPorClinicaDTO(
            Long idClinica,
            String nombreClinica,
            String nombreRadiografia,
            Long cantidad) {
        this.idClinica = idClinica;
        this.nombreClinica = nombreClinica;
        this.nombreRadiografia = nombreRadiografia;
        this.cantidad = cantidad;
    }

    public Long getIdClinica() {
        return idClinica;
    }

    public String getNombreClinica() {
        return nombreClinica;
    }

    public String getNombreRadiografia() {
        return nombreRadiografia;
    }

    public Long getCantidad() {
        return cantidad;
    }
}
