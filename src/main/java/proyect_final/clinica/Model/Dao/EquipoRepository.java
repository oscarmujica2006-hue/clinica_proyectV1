package proyect_final.clinica.Model.Dao;
import proyect_final.clinica.Model.Entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    
    @Query(value = "SELECT * FROM crear_equipo(:codigo, :nombre, :estado, :usuario)", nativeQuery = true)
    Object[] crearEquipoConUsuario(
        @Param("codigo") String codigo,
        @Param("nombre") String nombre,
        @Param("estado") String estado,
        @Param("usuario") Integer usuario
    );

}