package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.DiagnosticoTratamiento;
import proyect_final.clinica.Model.Dao.DiagnosticoTratamientoRepository;
import proyect_final.clinica.Service.DiagnosticoTratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DiagnosticoTratamientoServiceImpl implements DiagnosticoTratamientoService {

    @Autowired
    private DiagnosticoTratamientoRepository diagnosticoTratamientoRepository;

    @Override
    public List<DiagnosticoTratamiento> findAll() {
        return diagnosticoTratamientoRepository.findAll();
    }

    @Override
    public Optional<DiagnosticoTratamiento> obtenerPorId(Long id) {
        return diagnosticoTratamientoRepository.findById(id);
    }

    @Override
    public DiagnosticoTratamiento guardar(DiagnosticoTratamiento diagnosticoTratamiento) {
        return diagnosticoTratamientoRepository.save(diagnosticoTratamiento);
    }

    @Override
    public void eliminar(Long id) {
        diagnosticoTratamientoRepository.deleteById(id);
    }

    @Override
    public List<DiagnosticoTratamiento> findByEvolucionClinicaId(Long idEvolucionClinica) {
        return diagnosticoTratamientoRepository.findByEvolucionClinica_IdEvolucionClinica(idEvolucionClinica);
    }

    @Override
    public List<DiagnosticoTratamiento> findByTratamientoId(Long idTratamiento) {
        return diagnosticoTratamientoRepository.findByTratamiento_IdTratamiento(idTratamiento);
    }
}