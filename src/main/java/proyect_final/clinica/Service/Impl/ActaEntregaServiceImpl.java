package proyect_final.clinica.Service.Impl;


import proyect_final.clinica.Model.Dao.ActaEntregaRepository;
import proyect_final.clinica.Model.Entity.ActaEntrega;
import proyect_final.clinica.Service.ActaEntregaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ActaEntregaServiceImpl implements ActaEntregaService {

    @Autowired
    private ActaEntregaRepository actaEntregaRepository;

    @Override
    public List<ActaEntrega> findAll() {
        return actaEntregaRepository.findAll();
    }

    @Override
    public Optional<ActaEntrega> findById(Long id) {
        return actaEntregaRepository.findById(id);
    }

    @Override
    public ActaEntrega save(ActaEntrega acta) {
        return actaEntregaRepository.save(acta);
    }

    @Override
    public void deleteById(Long id) {
        actaEntregaRepository.deleteById(id);
    }

    @Override
    public List<ActaEntrega> findByAbastecimientoId(Long idAbastecimiento) {
        return actaEntregaRepository.findByAbastecimiento_IdAbastecimiento(idAbastecimiento);
    }
}