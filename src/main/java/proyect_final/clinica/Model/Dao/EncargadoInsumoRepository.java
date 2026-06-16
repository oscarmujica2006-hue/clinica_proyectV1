// EncargadoInsumoRepository.java
package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.EncargadoInsumo;
import proyect_final.clinica.Model.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EncargadoInsumoRepository extends JpaRepository<EncargadoInsumo, Long> {
    Optional<EncargadoInsumo> findByUsuario(Usuario usuario);
    Optional<EncargadoInsumo> findByIdEncargadoInsumo(Long id);
    Optional<EncargadoInsumo> findByUsuarioIdUsuario(Long idUsuario);

}