package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Dto.TratamientoCupoDTO;
import proyect_final.clinica.Model.Entity.Cupo;
import proyect_final.clinica.Model.Entity.Materia;
import proyect_final.clinica.Model.Entity.Tratamiento;
import java.util.List;
import java.util.Optional;

public interface CupoService {
    List<Cupo> listarTodos();
    Optional<Cupo> obtenerPorId(Long id);
    Cupo guardar(Cupo cupo);
    void eliminar(Long id);
    List<Cupo> obtenerPorMateria(Materia materia);
    Optional<Cupo> obtenerPorMateriaYTratamiento(Materia materia, Tratamiento tratamiento);
    List<TratamientoCupoDTO> obtenerTratamientosConCuposPorMateria(Long idMateria);
}