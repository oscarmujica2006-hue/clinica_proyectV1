package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.UnidadMedidaRepository;
import proyect_final.clinica.Model.Entity.UnidadMedida;
import proyect_final.clinica.Service.UnidadMedidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnidadMedidaServiceImpl implements UnidadMedidaService {

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    @Override
    public List<UnidadMedida> findAll() {
        return unidadMedidaRepository.findAll();
    }

    @Override
    public Optional<UnidadMedida> findById(Long id) {
        return unidadMedidaRepository.findById(id);
    }

    @Override
    public UnidadMedida save(UnidadMedida unidadMedida) {
        return unidadMedidaRepository.save(unidadMedida);
    }

    @Override
    public void deleteById(Long id) {
        unidadMedidaRepository.deleteById(id);
    }
}