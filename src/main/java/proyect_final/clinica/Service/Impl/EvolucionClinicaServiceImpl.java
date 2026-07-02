package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.EvolucionClinica;
import proyect_final.clinica.Model.Entity.Diagnostico;
import proyect_final.clinica.Model.Dao.EvolucionClinicaRepository;
import proyect_final.clinica.Model.Dao.DiagnosticoRepository;
import proyect_final.clinica.Service.EvolucionClinicaService;
import proyect_final.clinica.Model.Dto.EvolucionClinicaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvolucionClinicaServiceImpl implements EvolucionClinicaService {

    @Autowired
    private EvolucionClinicaRepository evolucionClinicaRepository;
    
    @Autowired
    private DiagnosticoRepository diagnosticoRepository;

    @Override
    public EvolucionClinica obtenerPorId(Long id) {
        return evolucionClinicaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public EvolucionClinica guardarEvolucion(EvolucionClinicaDTO dto) {
        Diagnostico diagnostico = diagnosticoRepository.findById(dto.getIdDiagnostico())
            .orElseThrow(() -> new RuntimeException("Diagnóstico no encontrado con ID: " + dto.getIdDiagnostico()));
        
        EvolucionClinica evolucion = new EvolucionClinica();
        evolucion.setDiagnostico(diagnostico);
        evolucion.setTipoRegistro(dto.getTipoRegistro());
        evolucion.setFechaHora(LocalDateTime.now());
        evolucion.setSubjetivo(dto.getSubjetivo());
        evolucion.setObjetivo(dto.getObjetivo());
        evolucion.setAnalisis(dto.getAnalisis());
        evolucion.setPlanAccion(dto.getPlanAccion());
        
        return evolucionClinicaRepository.save(evolucion);
    }

    @Override
    public EvolucionClinica guardar(EvolucionClinica evolucion) {
        return evolucionClinicaRepository.save(evolucion);
    }

    @Override
    public List<EvolucionClinica> obtenerPorDiagnostico(Long idDiagnostico) {
        return evolucionClinicaRepository.findByDiagnosticoIdDiagnostico(idDiagnostico);
    }



    @Override
    public List<EvolucionClinica> obtenerEvolucionesIniciales(Long idDiagnostico) {
        return evolucionClinicaRepository.findByDiagnosticoIdDiagnosticoAndEvolucionPadreIsNull(idDiagnostico);
    }

    @Override
    public List<EvolucionClinica> obtenerSesionesDeEvolucion(Long idEvolucion) {
        return evolucionClinicaRepository.findByEvolucionPadreIdEvolucionClinica(idEvolucion);
    }

}