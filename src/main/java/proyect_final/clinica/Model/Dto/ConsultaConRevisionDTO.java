package proyect_final.clinica.Model.Dto;

import java.util.List;
import java.util.Map;

public class ConsultaConRevisionDTO extends ConsultaCompletaDTO {
    
    // Datos de la Revisión
    private String observacionesGenerales;
    
    // ===== UN SOLO DIAGNÓSTICO CON TODOS LOS CAMPOS =====
    private Map<String, Object> diagnostico;
    
    // ===== ODONTOGRAMA COMPLETO =====
    // Mapa de dientes: { "11": { "center": "sano", "top": "cariado" } }
    private Map<String, Map<String, String>> odontograma;
    
    // Dientes de AMBOS tipos (para el historial)
    private List<Map<String, Object>> dientesPermanentes;
    private List<Map<String, Object>> dientesTemporales;
    
    // Tipo de dentición activo
    private String tipoDenticionActivo;

    // ===== ÍNDICES CPO/CEO =====
    private Integer cpoCariados;
    private Integer cpoPerdidos;
    private Integer cpoObturados;
    private Integer cpoTotal;
    private Integer piezasSanash4Permanentes;
    private Integer totalPiezasPermanentes;
    
    private Integer ceoCariados;
    private Integer ceoExtraidos;
    private Integer ceoObturados;
    private Integer ceoTotal;
    private Integer piezasSanash4Temporales;
    private Integer totalPiezasTemporales;

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

    public Map<String, Map<String, String>> getOdontograma() {
        return odontograma;
    }

    public void setOdontograma(Map<String, Map<String, String>> odontograma) {
        this.odontograma = odontograma;
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

    public Integer getCpoCariados() { return cpoCariados; }
    public void setCpoCariados(Integer cpoCariados) { this.cpoCariados = cpoCariados; }

    public Integer getCpoPerdidos() { return cpoPerdidos; }
    public void setCpoPerdidos(Integer cpoPerdidos) { this.cpoPerdidos = cpoPerdidos; }

    public Integer getCpoObturados() { return cpoObturados; }
    public void setCpoObturados(Integer cpoObturados) { this.cpoObturados = cpoObturados; }

    public Integer getCpoTotal() { return cpoTotal; }
    public void setCpoTotal(Integer cpoTotal) { this.cpoTotal = cpoTotal; }

    public Integer getPiezasSanash4Permanentes() { return piezasSanash4Permanentes; }
    public void setPiezasSanash4Permanentes(Integer piezasSanash4Permanentes) { this.piezasSanash4Permanentes = piezasSanash4Permanentes; }

    public Integer getTotalPiezasPermanentes() { return totalPiezasPermanentes; }
    public void setTotalPiezasPermanentes(Integer totalPiezasPermanentes) { this.totalPiezasPermanentes = totalPiezasPermanentes; }

    public Integer getCeoCariados() { return ceoCariados; }
    public void setCeoCariados(Integer ceoCariados) { this.ceoCariados = ceoCariados; }

    public Integer getCeoExtraidos() { return ceoExtraidos; }
    public void setCeoExtraidos(Integer ceoExtraidos) { this.ceoExtraidos = ceoExtraidos; }

    public Integer getCeoObturados() { return ceoObturados; }
    public void setCeoObturados(Integer ceoObturados) { this.ceoObturados = ceoObturados; }

    public Integer getCeoTotal() { return ceoTotal; }
    public void setCeoTotal(Integer ceoTotal) { this.ceoTotal = ceoTotal; }

    public Integer getPiezasSanash4Temporales() { return piezasSanash4Temporales; }
    public void setPiezasSanash4Temporales(Integer piezasSanash4Temporales) { this.piezasSanash4Temporales = piezasSanash4Temporales; }

    public Integer getTotalPiezasTemporales() { return totalPiezasTemporales; }
    public void setTotalPiezasTemporales(Integer totalPiezasTemporales) { this.totalPiezasTemporales = totalPiezasTemporales; }
}