package proyect_final.clinica.Model.Dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyect_final.clinica.Model.Entity.Director;
import proyect_final.clinica.Model.Entity.Usuario;
import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<Director ,Long> {
    Optional<Director> findByUsuario(Usuario usuario);

    Optional<Director> findByUsuarioIdUsuario(Long idUsuario);
    
}
    