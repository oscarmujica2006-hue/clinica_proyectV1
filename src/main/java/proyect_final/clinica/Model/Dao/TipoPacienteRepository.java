package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.TipoPaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPacienteRepository extends JpaRepository<TipoPaciente, Long> {
}