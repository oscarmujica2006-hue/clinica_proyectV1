package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.DetallePedidoRepository;
import proyect_final.clinica.Model.Entity.DetallePedido;
import proyect_final.clinica.Model.Entity.Insumo;
import proyect_final.clinica.Model.Entity.Pedido;
import proyect_final.clinica.Service.DetallePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DetallePedidoServiceImpl implements DetallePedidoService {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Override
    public Optional<DetallePedido> obtenerPorId(Long id) {
        return detallePedidoRepository.findById(id);
    }

    @Override
    public DetallePedido guardar(DetallePedido detalle) {
        return detallePedidoRepository.save(detalle);
    }

    @Override
    public List<DetallePedido> findByPedidoId(Long idPedido) {
        return detallePedidoRepository.findByPedido_IdPedido(idPedido);
    }
    @Override
    public void eliminar(Long id) {
        detallePedidoRepository.deleteById(id);
    }

    @Override
    public Optional<DetallePedido> findByPedidoAndInsumo(Pedido pedido, Insumo insumo) {
        return detallePedidoRepository.findByPedidoAndInsumo(pedido, insumo);
    }
}