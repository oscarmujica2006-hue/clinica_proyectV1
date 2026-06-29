package proyect_final.clinica.Model.Dto;

import java.util.List;

public class SolicitudRadiografiaDTO {
    private Long idDiagnosticoTratamiento;
    private Long idRadiografia;
    private String motivo;
    private List<Long> idsDientesPlan; // IDs de DiagnosticoTratamientoDiente
    
    // Getters y Setters
    public Long getIdDiagnosticoTratamiento() { return idDiagnosticoTratamiento; }
    public void setIdDiagnosticoTratamiento(Long idDiagnosticoTratamiento) { 
        this.idDiagnosticoTratamiento = idDiagnosticoTratamiento; 
    }
    
    public Long getIdRadiografia() { return idRadiografia; }
    public void setIdRadiografia(Long idRadiografia) { 
        this.idRadiografia = idRadiografia; 
    }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { 
        this.motivo = motivo; 
    }
    
    public List<Long> getIdsDientesPlan() { return idsDientesPlan; }
    public void setIdsDientesPlan(List<Long> idsDientesPlan) { 
        this.idsDientesPlan = idsDientesPlan; 
    }
}