package proyect_final.clinica.Model.Dto;

import lombok.Data;

@Data

public class StockBajoDTO {
    private Long idInsumo;
    private String nombreInsumo;
    private Integer stockTotal;
    private Integer stockMinimo;
    private Integer faltante;
    private String estado;  

}