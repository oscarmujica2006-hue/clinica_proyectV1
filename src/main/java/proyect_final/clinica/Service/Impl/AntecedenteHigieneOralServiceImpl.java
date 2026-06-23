package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.AntecedenteHigieneOral;
import proyect_final.clinica.Model.Dao.AntecedentesHigieneOralRepository;
import proyect_final.clinica.Service.AntecedenteHigieneOralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AntecedenteHigieneOralServiceImpl implements AntecedenteHigieneOralService {

    @Autowired
    private AntecedentesHigieneOralRepository antecedentesHigieneOralRepository;

    @Override
    public List<AntecedenteHigieneOral> obtenerTodos() {
        return antecedentesHigieneOralRepository.findAll();
    }

    @Override
    public Optional<AntecedenteHigieneOral> obtenerPorId(Long id) {
        return antecedentesHigieneOralRepository.findById(id);
    }

    @Override
    public AntecedenteHigieneOral guardar(AntecedenteHigieneOral antecedentesHigieneOral) {
        return antecedentesHigieneOralRepository.save(antecedentesHigieneOral);
    }

    @Override
    public void eliminar(Long id) {
        antecedentesHigieneOralRepository.deleteById(id);
    }
}