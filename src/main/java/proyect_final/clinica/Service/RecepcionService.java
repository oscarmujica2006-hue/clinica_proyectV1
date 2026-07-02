package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Recepcion;
import java.util.List;

public interface RecepcionService {
    Recepcion guardar(Recepcion recepcion);
    List<Recepcion> obtenerTodos();
    boolean existePorCodigo(Integer codigo);
}