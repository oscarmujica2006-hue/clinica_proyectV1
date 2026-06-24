package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.ActaEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActaEntregaRepository extends JpaRepository<ActaEntrega, Long> {
    List<ActaEntrega> findByAbastecimiento_IdAbastecimiento(Long idAbastecimiento);
}