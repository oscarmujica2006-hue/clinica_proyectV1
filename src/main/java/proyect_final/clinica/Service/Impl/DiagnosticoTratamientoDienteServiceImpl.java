package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.DiagnosticoTratamientoDiente;
import proyect_final.clinica.Model.Dao.DiagnosticoTratamientoDienteRepository;
import proyect_final.clinica.Service.DiagnosticoTratamientoDienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
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
    @Transactional
    public DiagnosticoTratamientoDiente guardar(DiagnosticoTratamientoDiente diente) {
        if (diente.getIdDiagnosticoTratamientoDiente() == null) {
            diente.setFechRegDiaTraDie(LocalDateTime.now());
        }
        diente.setFechModDiaTraDie(LocalDateTime.now());
        return diagnosticoTratamientoDienteRepository.save(diente);
    }

    @Override
    @Transactional
    public List<DiagnosticoTratamientoDiente> guardarTodos(List<DiagnosticoTratamientoDiente> dientes) {
        for (DiagnosticoTratamientoDiente d : dientes) {
            if (d.getIdDiagnosticoTratamientoDiente() == null) {
                d.setFechRegDiaTraDie(LocalDateTime.now());
            }
            d.setFechModDiaTraDie(LocalDateTime.now());
        }
        return diagnosticoTratamientoDienteRepository.saveAll(dientes);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        diagnosticoTratamientoDienteRepository.deleteById(id);
    }

    @Override
    public List<DiagnosticoTratamientoDiente> findByDiagnosticoTratamientoId(Long idDiagTrat) {
        return diagnosticoTratamientoDienteRepository.findByDiagnosticoTratamiento_IdDiagTrat(idDiagTrat);
    }

    @Override
    public List<DiagnosticoTratamientoDiente> findByEstado(String estado) {
        return diagnosticoTratamientoDienteRepository.findByEstado(estado);
    }

    @Override
    public long countByDiagnosticoTratamientoId(Long idDiagTrat) {
        return diagnosticoTratamientoDienteRepository.countByDiagnosticoTratamiento_IdDiagTrat(idDiagTrat);
    }

    @Override
    public long countByDiagnosticoTratamientoIdAndEstado(Long idDiagTrat, String estado) {
        return diagnosticoTratamientoDienteRepository.countByDiagnosticoTratamiento_IdDiagTratAndEstado(idDiagTrat, estado);
    }

    // ===== NUEVOS MÉTODOS =====

    @Override
    public List<DiagnosticoTratamientoDiente> findPendientesByDocenteId(Long idDocente) {
        return diagnosticoTratamientoDienteRepository.findPendientesByDocenteId(idDocente);
    }

    @Override
    public List<DiagnosticoTratamientoDiente> findCompletadosByDocenteId(Long idDocente) {
        return diagnosticoTratamientoDienteRepository.findCompletadosByDocenteId(idDocente);
    }

    @Override
    public List<DiagnosticoTratamientoDiente> findRechazadosByDocenteId(Long idDocente) {
        return diagnosticoTratamientoDienteRepository.findRechazadosByDocenteId(idDocente);
    }

    @Override
    public Long countByEstadoAndDocenteId(String estado, Long idDocente) {
        return diagnosticoTratamientoDienteRepository.countByEstadoAndDocenteId(estado, idDocente);
    }
}