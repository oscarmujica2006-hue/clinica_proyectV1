package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Rote;
import java.util.List;
import java.util.Optional;

public interface RoteService {
    List<Rote> obtenerTodos();
    Optional<Rote> obtenerPorId(Long id);
    Rote guardar(Rote rote);
    void eliminar(Long id);
}