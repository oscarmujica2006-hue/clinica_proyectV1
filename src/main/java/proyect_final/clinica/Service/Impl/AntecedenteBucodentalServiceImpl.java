package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.AntecedenteBucodental;
import proyect_final.clinica.Model.Dao.AntecedentesBucodentalesRepository;
import proyect_final.clinica.Service.AntecedenteBucodentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AntecedenteBucodentalServiceImpl implements AntecedenteBucodentalService {

    @Autowired
    private AntecedentesBucodentalesRepository antecedentesBucodentalesRepository;

    @Override
    public List<AntecedenteBucodental> obtenerTodos() {
        return antecedentesBucodentalesRepository.findAll();
    }

    @Override
    public Optional<AntecedenteBucodental> obtenerPorId(Long id) {
        return antecedentesBucodentalesRepository.findById(id);
    }

    @Override
    public AntecedenteBucodental guardar(AntecedenteBucodental antecedentesBucodentales) {
        return antecedentesBucodentalesRepository.save(antecedentesBucodentales);
    }

    @Override
    public void eliminar(Long id) {
        antecedentesBucodentalesRepository.deleteById(id);
    }
}