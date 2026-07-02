package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.EncargadoRadiografia;
import java.util.List;

public interface EncargadoRadiografiaService {
    EncargadoRadiografia guardar(EncargadoRadiografia encargado);
    List<EncargadoRadiografia> obtenerTodos();
    boolean existePorCodigo(Integer codigo);
}