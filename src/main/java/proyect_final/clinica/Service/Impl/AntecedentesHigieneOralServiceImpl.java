package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.AntecedentesHigieneOral;
import proyect_final.clinica.Model.Dao.AntecedentesHigieneOralRepository;
import proyect_final.clinica.Service.AntecedentesHigieneOralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AntecedentesHigieneOralServiceImpl implements AntecedentesHigieneOralService {

    @Autowired
    private AntecedentesHigieneOralRepository antecedentesHigieneOralRepository;

    @Override
    public List<AntecedentesHigieneOral> obtenerTodos() {
        return antecedentesHigieneOralRepository.findAll();
    }

    @Override
    public Optional<AntecedentesHigieneOral> obtenerPorId(Long id) {
        return antecedentesHigieneOralRepository.findById(id);
    }

    @Override
    public AntecedentesHigieneOral guardar(AntecedentesHigieneOral antecedentesHigieneOral) {
        return antecedentesHigieneOralRepository.save(antecedentesHigieneOral);
    }

    @Override
    public void eliminar(Long id) {
        antecedentesHigieneOralRepository.deleteById(id);
    }
}