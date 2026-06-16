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
    
    // Buscar por ID de usuario
    Optional<Docente> findByUsuarioIdUsuario(Long idUsuario);
    
    // Buscar docentes por clínica
    List<Docente> findByClinicaIdClinica(Long idClinica);
    
    // Buscar docentes activos por clínica
    List<Docente> findByClinicaIdClinicaAndEstadoTrue(Long idClinica);
    
    // Buscar por especialidad
    List<Docente> findByEspecialidadContainingIgnoreCase(String especialidad);
    
    // Buscar por nombre de persona (JPQL)
    @Query("SELECT d FROM Docente d JOIN d.usuario u JOIN u.persona p " +
           "WHERE LOWER(CONCAT(p.nombre, ' ', p.apellidoPaterno, ' ', p.apellidoMaterno)) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Docente> buscarPorNombreCompleto(@Param("nombre") String nombre);
    
    // Buscar por nombre de usuario
    @Query("SELECT d FROM Docente d JOIN d.usuario u WHERE LOWER(u.nombreUsuario) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<Docente> buscarPorNombreUsuario(@Param("username") String username);
    
    // Buscar docentes con todos sus datos
    @Query("SELECT d FROM Docente d " +
           "LEFT JOIN FETCH d.usuario u " +
           "LEFT JOIN FETCH u.persona p " +
           "LEFT JOIN FETCH d.clinica c " +
           "WHERE d.estado = true")
    List<Docente> findAllActivosWithDetails();
    
    // Contar docentes por clínica
    @Query("SELECT COUNT(d) FROM Docente d WHERE d.clinica.idClinica = :idClinica AND d.estado = true")
    Long countByClinicaAndEstadoTrue(@Param("idClinica") Long idClinica);


    Optional<Docente> findByUsuario(Usuario usuario);
    
}