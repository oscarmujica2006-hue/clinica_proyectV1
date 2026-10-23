package proyect_final.clinica.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReciboDTO {
    private Long idRecibo;
    private String pacienteNombre;
    private String ciPaciente;
    private String estudianteNombre;  // ✅ NUEVO
    private Long idEstudiante;        // estudiante real del recibo (cadena de consulta)
    private String estudianteCodigo;  // código (R.U.) para el impreso
    private LocalDateTime fechaPago;
    private String estadoPago;
    private Double subtotalTratamientos;
    private Double subtotalRadiografias;
    private Double montoTotal;
    private Double montoPagado;
    private Double saldoPendiente;
    private List<DetalleReciboDTO> detalles;
    private List<PagoReciboDTO> pagos;
}