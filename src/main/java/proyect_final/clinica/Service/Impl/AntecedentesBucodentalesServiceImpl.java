package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.AntecedentesBucodentales;
import proyect_final.clinica.Model.Dao.AntecedentesBucodentalesRepository;
import proyect_final.clinica.Service.AntecedentesBucodentalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AntecedentesBucodentalesServiceImpl implements AntecedentesBucodentalesService {

    @Autowired
    private AntecedentesBucodentalesRepository antecedentesBucodentalesRepository;

    @Override
    public List<AntecedentesBucodentales> obtenerTodos() {
        return antecedentesBucodentalesRepository.findAll();
    }

    @Override
    public Optional<AntecedentesBucodentales> obtenerPorId(Long id) {
        return antecedentesBucodentalesRepository.findById(id);
    }

    @Override
    public AntecedentesBucodentales guardar(AntecedentesBucodentales antecedentesBucodentales) {
        return antecedentesBucodentalesRepository.save(antecedentesBucodentales);
    }

    @Override
    public void eliminar(Long id) {
        antecedentesBucodentalesRepository.deleteById(id);
    }
}