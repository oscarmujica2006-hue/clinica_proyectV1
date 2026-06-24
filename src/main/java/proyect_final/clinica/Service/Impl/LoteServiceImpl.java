package proyect_final.clinica.Service.Impl;


import proyect_final.clinica.Model.Dao.LoteRepository;
import proyect_final.clinica.Model.Entity.Lote;
import proyect_final.clinica.Service.LoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LoteServiceImpl implements LoteService {

    @Autowired
    private LoteRepository loteRepository;

    @Override
    public List<Lote> findAll() {
        return loteRepository.findAll();
    }

    @Override
    public Optional<Lote> findById(Long id) {
        return loteRepository.findById(id);
    }

    @Override
    public Lote save(Lote lote) {
        return loteRepository.save(lote);
    }

    @Override
    public void deleteById(Long id) {
        loteRepository.deleteById(id);
    }

    @Override
    public List<Lote> findByInsumoId(Long idInsumo) {
        return loteRepository.findByInsumo_IdInsumo(idInsumo);
    }

    @Override
    public List<Lote> findByDetalleActaId(Long idDetalleActa) {
        return loteRepository.findByDetalleActaEntrega_IdDetalleActa(idDetalleActa);
    }
}