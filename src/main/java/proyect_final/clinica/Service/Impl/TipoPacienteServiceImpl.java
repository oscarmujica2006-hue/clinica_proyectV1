package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.TipoPaciente;
import proyect_final.clinica.Model.Dao.TipoPacienteRepository;
import proyect_final.clinica.Service.TipoPacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TipoPacienteServiceImpl implements TipoPacienteService {

    @Autowired
    private TipoPacienteRepository tipoPacienteRepository;

    @Override
    public List<TipoPaciente> obtenerTodos() {
        return tipoPacienteRepository.findAll();
    }

    @Override
    public Optional<TipoPaciente> obtenerPorId(Long id) {
        return tipoPacienteRepository.findById(id);
    }

    @Override
    public TipoPaciente guardar(TipoPaciente tipoPaciente) {
        return tipoPacienteRepository.save(tipoPaciente);
    }

    @Override
    public void eliminar(Long id) {
        tipoPacienteRepository.deleteById(id);
    }
}