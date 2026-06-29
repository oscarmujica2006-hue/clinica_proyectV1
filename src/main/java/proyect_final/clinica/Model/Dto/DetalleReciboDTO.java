package proyect_final.clinica.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleReciboDTO {
    private Long idDetalleRecibo;
    private String tipoItem;
    private String concepto;
    private String tratamientoNombre;  // ✅ NUEVO
    private Integer diente;             // ✅ NUEVO
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}