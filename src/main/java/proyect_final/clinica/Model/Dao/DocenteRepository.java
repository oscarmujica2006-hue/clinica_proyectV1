package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Docente;
import proyect_final.clinica.Model.Entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
    
    // Buscar por estado
    List<Docente> findByEstadoTrue();
    
    // Buscar por código de docente
    Optional<Docente> findByCodigoDocente(Integer codigoDocente);
    
    boolean existsByCodigoDocente(Integer codigoDocente);
    

    List<Docente> findByClinicaIdClinica(Long idClinica);
    
    // Buscar docentes activos por clínica
    List<Docente> findByClinicaIdClinicaAndEstadoTrue(Long idClinica);
    
    // Buscar por nombre de persona (JPQL)
    @Query("SELECT d FROM Docente d JOIN d.usuario u JOIN u.persona p " +
           "WHERE LOWER(CONCAT(p.nombre, ' ', p.apellidoPaterno, ' ', p.apellidoMaterno)) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Docente> buscarPorNombreCompleto(@Param("nombre") String nombre);
    
  
    // Buscar docentes con todos sus datos
    @Query("SELECT d FROM Docente d " +
           "LEFT JOIN FETCH d.usuario u " +
           "LEFT JOIN FETCH u.persona p " +
           "LEFT JOIN FETCH d.clinica c " +
           "WHERE d.estado = true")
    List<Docente> findAllActivosWithDetails();
      


    
    Optional<Docente> findByUsuarioIdUsuario(Long idUsuario);
    


    Optional<Docente> findByUsuario(Usuario usuario);
    
}