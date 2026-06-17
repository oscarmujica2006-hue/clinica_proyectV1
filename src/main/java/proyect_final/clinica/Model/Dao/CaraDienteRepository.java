package proyect_final.clinica.Model.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import proyect_final.clinica.Model.Entity.CaraDiente;
import java.util.Optional;

public interface CaraDienteRepository extends JpaRepository<CaraDiente, Long> {
    Optional<CaraDiente> findByNombreCara(String nombreCara);
}