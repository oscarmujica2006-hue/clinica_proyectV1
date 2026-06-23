package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.CupoRepository;
import proyect_final.clinica.Model.Dto.TratamientoCupoDTO;
import proyect_final.clinica.Model.Entity.Cupo;
import proyect_final.clinica.Model.Entity.Materia;
import proyect_final.clinica.Model.Entity.Tratamiento;
import proyect_final.clinica.Service.CupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CupoServiceImpl implements CupoService {

    @Autowired
    private CupoRepository cupoRepository;



    @Override
    public List<Cupo> listarTodos() {
        return cupoRepository.findAll();
    }

    @Override
    public Optional<Cupo> obtenerPorId(Long id) {
        return cupoRepository.findById(id);
    }

    @Override
    public Cupo guardar(Cupo cupo) {
        return cupoRepository.save(cupo);
    }

    @Override
    public void eliminar(Long id) {
        cupoRepository.deleteById(id);
    }

    @Override
    public List<Cupo> obtenerPorMateria(Materia materia) {
        return cupoRepository.findByMateria(materia);
    }

    @Override
    public Optional<Cupo> obtenerPorMateriaYTratamiento(Materia materia, Tratamiento tratamiento) {
        return cupoRepository.findByMateriaAndTratamiento(materia, tratamiento);
    }

    @Override
    public List<TratamientoCupoDTO> obtenerTratamientosConCuposPorMateria(Long idMateria) {
        return cupoRepository.findTratamientosWithCuposByMateriaId(idMateria);
    }
}