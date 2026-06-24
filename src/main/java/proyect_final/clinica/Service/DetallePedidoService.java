package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.DetallePedido;
import proyect_final.clinica.Model.Entity.Insumo;
import proyect_final.clinica.Model.Entity.Pedido;

import java.util.List;
import java.util.Optional;

public interface DetallePedidoService {
    Optional<DetallePedido> obtenerPorId(Long id);
    DetallePedido guardar(DetallePedido detalle);
    List<DetallePedido> findByPedidoId(Long idPedido);
    void eliminar(Long id);
    Optional<DetallePedido> findByPedidoAndInsumo(Pedido pedido, Insumo insumo);
}