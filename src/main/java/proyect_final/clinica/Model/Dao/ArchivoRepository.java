package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
    
    // Métodos existentes...
    List<Archivo> findByPacienteIdPaciente(Long idPaciente);
    
    @Query("SELECT a FROM Archivo a WHERE a.paciente.idPaciente = :idPaciente ORDER BY a.idArchivo LIMIT 1")
    Optional<Archivo> findPrimerArchivoPorPaciente(@Param("idPaciente") Long idPaciente);
    
    // ⭐ ESTE MÉTODO LLAMA A LA FUNCIÓN DE POSTGRESQL - CORRECTO
    @Query(value = "SELECT * FROM buscar_archivo_por_paciente(:ci, :nombre)", nativeQuery = true)
    Optional<Archivo> buscarArchivoPorCiONombre(@Param("ci") Integer ci, 
                                                @Param("nombre") String nombre);
    
    // ✅ CORREGIDO JPQL - Sin REPLACE y con CAST correcto
    @Query("SELECT a.idArchivo FROM Archivo a " +
           "WHERE (:ci IS NOT NULL AND a.paciente.ci = :ci) " +
           "OR (:nombre IS NOT NULL AND LOWER(CONCAT(a.paciente.persona.nombre, ' ', " +
           "a.paciente.persona.apellidoPaterno, ' ', " +
           "a.paciente.persona.apellidoMaterno)) " +
           "LIKE LOWER(CONCAT('%', :nombre, '%')))")
    Optional<Long> findIdArchivoByPacienteCiOrNombre(@Param("ci") Integer ci, 
                                                     @Param("nombre") String nombre);
    
    // ✅ CORREGIDO JPQL - Verificar existencia
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END " +
           "FROM Archivo a WHERE a.paciente.idPaciente = :idPaciente")
    boolean existsByPacienteId(@Param("idPaciente") Long idPaciente);
}