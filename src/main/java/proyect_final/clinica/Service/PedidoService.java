package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    Optional<Pedido> obtenerPorId(Long id);
    List<Pedido> findByEncargadoId(Long idEncargadoInsumo);
    Pedido guardar(Pedido pedido);
    void eliminar(Long id);
    List<Pedido> findAll();
    List<Pedido> findByEstado(String estado);

}