package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Periodo;
import java.util.List;

public interface PeriodoService {
    List<Periodo> listarTodos();
    Periodo obtenerPorId(Long id);
    Periodo guardar(Periodo periodo);

    List<Periodo> listarActivosPorFecha();
}