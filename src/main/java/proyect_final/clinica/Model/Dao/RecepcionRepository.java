package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Recepcion;
import proyect_final.clinica.Model.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RecepcionRepository extends JpaRepository<Recepcion, Long> {
    Optional<Recepcion> findByUsuario(Usuario usuario);
    Optional<Recepcion> findByCodigoRecepcion(Integer codigoRecepcion);
}