package proyect_final.clinica.Service;
import proyect_final.clinica.Model.Dto.InsumoConsumidoDTO;
import proyect_final.clinica.Model.Dto.StockBajoDTO;
import proyect_final.clinica.Model.Entity.Insumo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InsumoService {
    List<Insumo> obtenerTodos();
    Optional<Insumo> obtenerPorId(Long id);
    Insumo guardar(Insumo insumo);
    void eliminar(Long id);
    List<InsumoConsumidoDTO> obtenerInsumosMasConsumidos(LocalDate fechaInicio, LocalDate fechaFin);


    List<StockBajoDTO> obtenerStockBajo();
}