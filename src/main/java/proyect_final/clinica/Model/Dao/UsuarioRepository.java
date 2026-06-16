package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // ✅ Búsquedas básicas
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    
    Boolean existsByNombreUsuario(String nombreUsuario);
    
    List<Usuario> findByEstado(Boolean estado);
    Optional<Usuario> findByEmail(String email);
    // ✅ ESTE MÉTODO SÍ FUNCIONA CON @Query
    @Query("SELECT u FROM Usuario u WHERE u.persona.id_persona = :idPersona")
    List<Usuario> buscarPorIdPersona(@Param("idPersona") Long idPersona);
    
    // ✅ Método adicional útil
    @Query("SELECT u FROM Usuario u WHERE u.persona.nombre LIKE %:nombre%")
    List<Usuario> buscarPorNombrePersona(@Param("nombre") String nombre);
}