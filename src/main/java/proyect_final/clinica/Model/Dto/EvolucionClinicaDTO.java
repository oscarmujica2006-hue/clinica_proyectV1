package proyect_final.clinica.Model.Dto;

import java.time.LocalDateTime;

public class EvolucionClinicaDTO {
    
    private Long idDiagnostico;
    private LocalDateTime fechaHora;
    private String tipoRegistro;  
    private String subjetivo;
    private String objetivo;
    private String analisis;
    private String planAccion;
    private String presionArterial;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaRespiratoria;
    private Double temperatura;
    private Double peso;
    
    // Getters y Setters
    public Long getIdDiagnostico() {
        return idDiagnostico;
    }
    
    public void setIdDiagnostico(Long idDiagnostico) {
        this.idDiagnostico = idDiagnostico;
    }
    
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public String getTipoRegistro() {
        return tipoRegistro;
    }
    
    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }
    
    public String getSubjetivo() {
        return subjetivo;
    }
    
    public void setSubjetivo(String subjetivo) {
        this.subjetivo = subjetivo;
    }
    
    public String getObjetivo() {
        return objetivo;
    }
    
    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }
    
    public String getAnalisis() {
        return analisis;
    }
    
    public void setAnalisis(String analisis) {
        this.analisis = analisis;
    }
    
    public String getPlanAccion() {
        return planAccion;
    }
    
    public void setPlanAccion(String planAccion) {
        this.planAccion = planAccion;
    }
    
    public String getPresionArterial() {
        return presionArterial;
    }
    
    public void setPresionArterial(String presionArterial) {
        this.presionArterial = presionArterial;
    }
    
    public Integer getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }
    
    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }
    
    public Integer getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria;
    }
    
    public void setFrecuenciaRespiratoria(Integer frecuenciaRespiratoria) {
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
    }
    
    public Double getTemperatura() {
        return temperatura;
    }
    
    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }
    
    public Double getPeso() {
        return peso;
    }
    
    public void setPeso(Double peso) {
        this.peso = peso;
    }
}