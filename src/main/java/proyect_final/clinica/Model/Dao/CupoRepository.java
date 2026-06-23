package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Cupo;
import proyect_final.clinica.Model.Entity.Materia;
import proyect_final.clinica.Model.Entity.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import proyect_final.clinica.Model.Dto.TratamientoCupoDTO;

import java.util.List;
import java.util.Optional;

public interface CupoRepository extends JpaRepository<Cupo, Long> {
    List<Cupo> findByMateria(Materia materia);
    Optional<Cupo> findByMateriaAndTratamiento(Materia materia, Tratamiento tratamiento);

    // Método que devuelve una lista de objetos con la información que necesitas
    @Query("SELECT new proyect_final.clinica.Model.Dto.TratamientoCupoDTO(" +
           "t.idTratamiento, t.nombreTratamiento, t.precioTratamiento, c.cuposDisponibles) " +
           "FROM Cupo c JOIN c.tratamiento t WHERE c.materia.id_materia = :idMateria")
    List<TratamientoCupoDTO> findTratamientosWithCuposByMateriaId(@Param("idMateria") Long idMateria);
}