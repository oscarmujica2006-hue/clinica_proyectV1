package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.DiagnosticoTratamientoDiente;
import proyect_final.clinica.Model.Dao.DiagnosticoTratamientoDienteRepository;
import proyect_final.clinica.Service.DiagnosticoTratamientoDienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DiagnosticoTratamientoDienteServiceImpl implements DiagnosticoTratamientoDienteService {

    @Autowired
    private DiagnosticoTratamientoDienteRepository diagnosticoTratamientoDienteRepository;

    @Override
    public List<DiagnosticoTratamientoDiente> findAll() {
        return diagnosticoTratamientoDienteRepository.findAll();
    }

    @Override
    public Optional<DiagnosticoTratamientoDiente> obtenerPorId(Long id) {
        return diagnosticoTratamientoDienteRepository.findById(id);
    }

    @Override
    public DiagnosticoTratamientoDiente guardar(DiagnosticoTratamientoDiente diente) {
        return diagnosticoTratamientoDienteRepository.save(diente);
    }

    @Override
    public List<DiagnosticoTratamientoDiente> guardarTodos(List<DiagnosticoTratamientoDiente> dientes) {
        return diagnosticoTratamientoDienteRepository.saveAll(dientes);
    }

    @Override
    public void eliminar(Long id) {
        diagnosticoTratamientoDienteRepository.deleteById(id);
    }

    @Override
    public void eliminarTodosPorDiagnosticoTratamientoId(Long idDiagTrat) {
        List<DiagnosticoTratamientoDiente> dientes = findByDiagnosticoTratamientoId(idDiagTrat);
        if (!dientes.isEmpty()) {
            diagnosticoTratamientoDienteRepository.deleteAll(dientes);
        }
    }

    @Override
    public List<DiagnosticoTratamientoDiente> findByDiagnosticoTratamientoId(Long idDiagTrat) {
        return diagnosticoTratamientoDienteRepository.findByDiagnosticoTratamiento_IdDiagTrat(idDiagTrat);
    }

    @Override
    public List<DiagnosticoTratamientoDiente> findByDiagnosticoTratamientoIdAndEstado(Long idDiagTrat, String estado) {
        return diagnosticoTratamientoDienteRepository.findByDiagnosticoTratamiento_IdDiagTratAndEstado(idDiagTrat, estado);
    }

    @Override
    public List<DiagnosticoTratamientoDiente> findByDiente(Integer diente) {
        return diagnosticoTratamientoDienteRepository.findByDiente(diente);
    }

    @Override
    public long countByDiagnosticoTratamientoId(Long idDiagTrat) {
        return diagnosticoTratamientoDienteRepository.countByDiagnosticoTratamiento_IdDiagTrat(idDiagTrat);
    }

    @Override
    public long countByDiagnosticoTratamientoIdAndEstado(Long idDiagTrat, String estado) {
        return diagnosticoTratamientoDienteRepository.countByDiagnosticoTratamiento_IdDiagTratAndEstado(idDiagTrat, estado);
    }

    @Override
    public DiagnosticoTratamientoDiente actualizarEstado(Long idDiente, String nuevoEstado) {
        DiagnosticoTratamientoDiente diente = diagnosticoTratamientoDienteRepository.findById(idDiente)
            .orElseThrow(() -> new RuntimeException("Diente no encontrado con ID: " + idDiente));
        
        diente.setEstado(nuevoEstado);
        return diagnosticoTratamientoDienteRepository.save(diente);
    }

    @Override
    public void actualizarEstadoPorDiagnosticoTratamientoId(Long idDiagTrat, String nuevoEstado) {
        List<DiagnosticoTratamientoDiente> dientes = findByDiagnosticoTratamientoId(idDiagTrat);
        if (!dientes.isEmpty()) {
            dientes.forEach(d -> d.setEstado(nuevoEstado));
            diagnosticoTratamientoDienteRepository.saveAll(dientes);
        }
    }
}