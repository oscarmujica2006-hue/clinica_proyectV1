package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.RadiografiaRepository;
import proyect_final.clinica.Model.Entity.Radiografia;
import proyect_final.clinica.Service.RadiografiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RadiografiaServiceImpl implements RadiografiaService {

    @Autowired
    private RadiografiaRepository radiografiaRepository;

    @Override
    public List<Radiografia> listarTodos() {
        return radiografiaRepository.findAll();
    }

    @Override
    public Optional<Radiografia> obtenerPorId(Long id) {
        return radiografiaRepository.findById(id);
    }

    @Override
    public Radiografia guardar(Radiografia radiografia) {
        return radiografiaRepository.save(radiografia);
    }

    @Override
    public void eliminar(Long id) {
        radiografiaRepository.deleteById(id);
    }

    @Override
    public List<Radiografia> buscarPorNombre(String nombre) {
        return radiografiaRepository.findByNombreRayoContainingIgnoreCase(nombre);
    }
}