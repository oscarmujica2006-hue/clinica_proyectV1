package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.EncargadoRadiografia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EncargadoRadiografiaRepository extends JpaRepository<EncargadoRadiografia, Long> {
    Optional<EncargadoRadiografia> findByCodigoEncarRadiografia(Integer codigo);
}