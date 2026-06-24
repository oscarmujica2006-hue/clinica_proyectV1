package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    

    List<Pedido> findByEncargadoInsumo_IdEncargadoInsumo(Long idEncargadoInsumo);

    List<Pedido> findByEstadoPedido(String estadoPedido);
    
}