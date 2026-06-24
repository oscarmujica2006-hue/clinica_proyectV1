package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {
    // Puedes agregar métodos personalizados si los necesitas
    Optional<UnidadMedida> findByNombreUnidad(String nombreUnidad);
}