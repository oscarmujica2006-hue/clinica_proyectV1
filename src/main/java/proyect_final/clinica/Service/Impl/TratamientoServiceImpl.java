package proyect_final.clinica.Service.Impl;
import proyect_final.clinica.Model.Entity.Tratamiento;
import proyect_final.clinica.Model.Dao.TratamientoRepository;
import proyect_final.clinica.Service.TratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class TratamientoServiceImpl implements TratamientoService {
    
    @Autowired
    private TratamientoRepository tratamientoRepository;


    @Override
    public List<Tratamiento> obtenerTodos() {
        return tratamientoRepository.findAll();
    }

    @Override
    public Optional<Tratamiento> obtenerPorId(Long id) {
        return tratamientoRepository.findById(id);
    }

    @Override
    public Tratamiento guardar(Tratamiento tratamiento) {
        return tratamientoRepository.save(tratamiento);
    }

    @Override
    public void eliminar(Long id) {
        tratamientoRepository.deleteById(id);
    }   



}
