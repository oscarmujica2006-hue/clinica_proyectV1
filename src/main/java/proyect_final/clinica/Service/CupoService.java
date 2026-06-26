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
    
    // ==================== NUEVOS MÉTODOS ====================
    
    /** Buscar cupo por ID de tratamiento (retorna null si no existe) */
    Cupo findByTratamientoId(Long idTratamiento);
    
    /** Buscar cupo por ID de tratamiento y ID de materia */
    Optional<Cupo> findByTratamientoIdAndMateriaId(Long idTratamiento, Long idMateria);
    
    /** Buscar cupo por ID de tratamiento (versión Optional) */
    Optional<Cupo> findOptionalByTratamientoId(Long idTratamiento);
    
    /** Verificar si existe cupo para un tratamiento */
    boolean existeCupoParaTratamiento(Long idTratamiento);
    
    /** Descontar un cupo de un tratamiento */
    Cupo descontarCupo(Long idTratamiento);
    
    /** Incrementar un cupo de un tratamiento (para reversiones) */
    Cupo incrementarCupo(Long idTratamiento);
    
    /** Obtener total de cupos disponibles sumando todos */
    int obtenerTotalCuposDisponibles();
}