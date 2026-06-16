package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Rote;
import proyect_final.clinica.Model.Dao.RoteRepository;
import proyect_final.clinica.Service.RoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RoteServiceImpl implements RoteService {

    @Autowired
    private RoteRepository roteRepository;

    @Override
    public List<Rote> obtenerTodos() {
        return roteRepository.findAll();
    }

    @Override
    public Optional<Rote> obtenerPorId(Long id) {
        return roteRepository.findById(id);
    }

    @Override
    public Rote guardar(Rote rote) {
        return roteRepository.save(rote);
    }

    @Override
    public void eliminar(Long id) {
        roteRepository.deleteById(id);
    }

}