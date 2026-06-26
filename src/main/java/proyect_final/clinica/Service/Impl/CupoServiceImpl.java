package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.CupoRepository;
import proyect_final.clinica.Model.Dto.TratamientoCupoDTO;
import proyect_final.clinica.Model.Entity.Cupo;
import proyect_final.clinica.Model.Entity.Materia;
import proyect_final.clinica.Model.Entity.Tratamiento;
import proyect_final.clinica.Service.CupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
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

    // ==================== NUEVOS MÉTODOS PARA EL DOCENTE ====================

    @Override
    public Cupo findByTratamientoId(Long idTratamiento) {
        // ✅ Usar el método correcto del repositorio
        List<Cupo> cupos = cupoRepository.findCuposByTratamientoId(idTratamiento);
        return cupos.isEmpty() ? null : cupos.get(0);
    }

    @Override
    public Optional<Cupo> findByTratamientoIdAndMateriaId(Long idTratamiento, Long idMateria) {
        // ✅ Usar el método correcto del repositorio
        return cupoRepository.findCupoByTratamientoIdAndMateriaId(idTratamiento, idMateria);
    }

    @Override
    public Optional<Cupo> findOptionalByTratamientoId(Long idTratamiento) {
        List<Cupo> cupos = cupoRepository.findCuposByTratamientoId(idTratamiento);
        return cupos.isEmpty() ? Optional.empty() : Optional.of(cupos.get(0));
    }

    @Override
    public boolean existeCupoParaTratamiento(Long idTratamiento) {
        // ✅ Usar el método correcto del repositorio
        return cupoRepository.existsCupoByTratamientoId(idTratamiento);
    }

    @Override
    public Cupo descontarCupo(Long idTratamiento) {
        Cupo cupo = findByTratamientoId(idTratamiento);
        if (cupo == null) {
            throw new RuntimeException("No existe cupo para el tratamiento ID: " + idTratamiento);
        }
        if (cupo.getCuposDisponibles() <= 0) {
            throw new RuntimeException("No hay cupos disponibles para este tratamiento");
        }
        cupo.setCuposDisponibles(cupo.getCuposDisponibles() - 1);
        return cupoRepository.save(cupo);
    }

    @Override
    public Cupo incrementarCupo(Long idTratamiento) {
        Cupo cupo = findByTratamientoId(idTratamiento);
        if (cupo == null) {
            throw new RuntimeException("No existe cupo para el tratamiento ID: " + idTratamiento);
        }
        cupo.setCuposDisponibles(cupo.getCuposDisponibles() + 1);
        return cupoRepository.save(cupo);
    }

    @Override
    public int obtenerTotalCuposDisponibles() {
        List<Cupo> todos = listarTodos();
        return todos.stream().mapToInt(Cupo::getCuposDisponibles).sum();
    }
}