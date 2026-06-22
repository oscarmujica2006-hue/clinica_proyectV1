package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Rote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoteRepository extends JpaRepository<Rote, Long> {
}