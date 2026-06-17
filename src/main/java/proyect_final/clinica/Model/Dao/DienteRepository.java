package proyect_final.clinica.Model.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import proyect_final.clinica.Model.Entity.Diente;
import java.util.Optional;

public interface DienteRepository extends JpaRepository<Diente, Long> {
    Optional<Diente> findByNumeroDiente(Integer numeroDiente);
}