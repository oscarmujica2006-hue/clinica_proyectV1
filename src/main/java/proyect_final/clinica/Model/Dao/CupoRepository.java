package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Dto.TratamientoCupoDTO;
import proyect_final.clinica.Model.Entity.Cupo;
import proyect_final.clinica.Model.Entity.Materia;
import proyect_final.clinica.Model.Entity.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CupoRepository extends JpaRepository<Cupo, Long> {
    
    // ==================== MÉTODOS EXISTENTES ====================
    
    List<Cupo> findByMateria(Materia materia);
    
    Optional<Cupo> findByMateriaAndTratamiento(Materia materia, Tratamiento tratamiento);
    
    // ==================== CONSULTA PARA DTO (CORREGIDA) ====================
    
    @Query("SELECT new proyect_final.clinica.Model.Dto.TratamientoCupoDTO(" +
           "c.tratamiento.idTratamiento, " +
           "c.tratamiento.nombreTratamiento, " +
           "c.tratamiento.precioTratamiento, " +
           "c.cuposDisponibles) " +
           "FROM Cupo c " +
           "WHERE c.materia.id_materia = :idMateria")  
    List<TratamientoCupoDTO> findTratamientosWithCuposByMateriaId(@Param("idMateria") Long idMateria);
    
    // ==================== MÉTODOS PARA EL SERVICIO (CORREGIDOS) ====================
    
    @Query("SELECT c FROM Cupo c WHERE c.tratamiento.idTratamiento = :idTratamiento")
    List<Cupo> findCuposByTratamientoId(@Param("idTratamiento") Long idTratamiento);
    
    @Query("SELECT c FROM Cupo c WHERE c.tratamiento.idTratamiento = :idTratamiento AND c.materia.id_materia = :idMateria")
    Optional<Cupo> findCupoByTratamientoIdAndMateriaId(@Param("idTratamiento") Long idTratamiento, @Param("idMateria") Long idMateria);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cupo c WHERE c.tratamiento.idTratamiento = :idTratamiento")
    boolean existsCupoByTratamientoId(@Param("idTratamiento") Long idTratamiento);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cupo c WHERE c.tratamiento.idTratamiento = :idTratamiento AND c.materia.id_materia = :idMateria")
    boolean existsCupoByTratamientoIdAndMateriaId(@Param("idTratamiento") Long idTratamiento, @Param("idMateria") Long idMateria);
}