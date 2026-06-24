package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.PedidoRepository;
import proyect_final.clinica.Model.Entity.Pedido;
import proyect_final.clinica.Service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public List<Pedido> findByEncargadoId(Long idEncargadoInsumo) {
        return pedidoRepository.findByEncargadoInsumo_IdEncargadoInsumo(idEncargadoInsumo);
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Override
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }
@Override
public List<Pedido> findByEstado(String estado) {
    return pedidoRepository.findByEstadoPedido(estado);
}
}