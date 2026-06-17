package proyect_final.clinica.Model.Dto;

import java.util.List;
import java.util.Map;

public class ConsultaConRevisionDTO extends ConsultaCompletaDTO {
    
    // Datos de la Revisión
    private String observacionesGenerales;
    
    // ===== UN SOLO DIAGNÓSTICO CON TODOS LOS CAMPOS =====
    private Map<String, Object> diagnostico;
    
    // Dientes de AMBOS tipos
    private List<Map<String, Object>> dientesPermanentes;
    private List<Map<String, Object>> dientesTemporales;
    
    // Tipo de dentición activo
    private String tipoDenticionActivo;

    // Getters y Setters
    public String getObservacionesGenerales() {
        return observacionesGenerales;
    }

    public void setObservacionesGenerales(String observacionesGenerales) {
        this.observacionesGenerales = observacionesGenerales;
    }

    public Map<String, Object> getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(Map<String, Object> diagnostico) {
        this.diagnostico = diagnostico;
    }

    public List<Map<String, Object>> getDientesPermanentes() {
        return dientesPermanentes;
    }

    public void setDientesPermanentes(List<Map<String, Object>> dientesPermanentes) {
        this.dientesPermanentes = dientesPermanentes;
    }

    public List<Map<String, Object>> getDientesTemporales() {
        return dientesTemporales;
    }

    public void setDientesTemporales(List<Map<String, Object>> dientesTemporales) {
        this.dientesTemporales = dientesTemporales;
    }

    public String getTipoDenticionActivo() {
        return tipoDenticionActivo;
    }

    public void setTipoDenticionActivo(String tipoDenticionActivo) {
        this.tipoDenticionActivo = tipoDenticionActivo;
    }
}