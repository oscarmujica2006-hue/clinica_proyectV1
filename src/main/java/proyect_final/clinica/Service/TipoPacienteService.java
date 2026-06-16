package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.TipoPaciente;
import java.util.List;
import java.util.Optional;

public interface TipoPacienteService {
    List<TipoPaciente> obtenerTodos();
    Optional<TipoPaciente> obtenerPorId(Long id);
    TipoPaciente guardar(TipoPaciente tipoPaciente);
    void eliminar(Long id);
}