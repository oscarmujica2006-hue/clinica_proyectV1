package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
    

    @Query(value = "SELECT * FROM buscar_archivo_por_paciente(:ci, :nombre)", nativeQuery = true)
    Optional<Archivo> buscarArchivoPorCiONombre(@Param("ci") Integer ci, 
                                                @Param("nombre") String nombre);
    
   
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END " +
           "FROM Archivo a WHERE a.paciente.idPaciente = :idPaciente")
    boolean existsByPacienteId(@Param("idPaciente") Long idPaciente);
}