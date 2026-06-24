package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.DetallePedido;
import proyect_final.clinica.Model.Entity.Insumo;
import proyect_final.clinica.Model.Entity.Pedido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    List<DetallePedido> findByPedido_IdPedido(Long idPedido);


    Optional<DetallePedido> findByPedidoAndInsumo(Pedido pedido, Insumo insumo);
}