package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Abastecimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AbastecimientoRepository extends JpaRepository<Abastecimiento, Long> {
    // CORREGIDO: usar director en lugar de directora
    List<Abastecimiento> findByDirector_IdUsuarioClinica(Long idUsuarioClinica);
    List<Abastecimiento> findByEstadoAbastecimiento(String estado);
    List<Abastecimiento> findByPedido_IdPedido(Long idPedido);
}