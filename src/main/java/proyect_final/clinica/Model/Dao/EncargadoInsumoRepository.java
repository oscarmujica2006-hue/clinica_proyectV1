// EncargadoInsumoRepository.java
package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.EncargadoInsumo;
import proyect_final.clinica.Model.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EncargadoInsumoRepository extends JpaRepository<EncargadoInsumo, Long> {
    Optional<EncargadoInsumo> findByUsuario(Usuario usuario);
    Optional<EncargadoInsumo> findByIdEncargadoInsumo(Long id);
    Optional<EncargadoInsumo> findByUsuarioIdUsuario(Long idUsuario);
    @Query("SELECT e FROM EncargadoInsumo e WHERE e.codigo_almacen = :codigo")
    Optional<EncargadoInsumo> findByCodigoAlmacen(@Param("codigo") String codigoAlmacen);

    
}