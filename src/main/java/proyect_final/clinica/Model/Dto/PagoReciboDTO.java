package proyect_final.clinica.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagoReciboDTO {
    private Long idPagoRecibo;
    private Double monto;
    private String metodoPago;
    private LocalDateTime fechaPago;
    private String observaciones;
}