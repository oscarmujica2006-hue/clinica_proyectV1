package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Diagnostico;
import proyect_final.clinica.Model.Dao.DiagnosticoRepository;
import proyect_final.clinica.Service.DiagnosticoService;
import proyect_final.clinica.Model.Entity.Paciente;
import proyect_final.clinica.Model.Entity.Persona;
import proyect_final.clinica.Model.Entity.DiagnosticoTratamiento;
import proyect_final.clinica.Model.Entity.EvolucionClinica;
import proyect_final.clinica.Service.DiagnosticoTratamientoService;
import proyect_final.clinica.Service.EvolucionClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiagnosticoServiceImpl implements DiagnosticoService {

    @Autowired
    private DiagnosticoRepository diagnosticoRepository;
    
    @Autowired
    private DiagnosticoTratamientoService diagnosticoTratamientoService;
    
    @Autowired
    private EvolucionClinicaService evolucionService;
    
    @Override
    public List<Diagnostico> obtenerTodos() {
        return diagnosticoRepository.findAll();
    }

    @Override
    public Optional<Diagnostico> obtenerPorId(Long id) {
        return diagnosticoRepository.findById(id);
    }

    @Override
    public Diagnostico guardar(Diagnostico diagnostico) {
        return diagnosticoRepository.save(diagnostico);
    }

    @Override
    public void eliminar(Long id) {
        diagnosticoRepository.deleteById(id);
    }

    @Override
    public Optional<Diagnostico> buscarPorIdConFuncionDiagnostico(Long idDiagnostico) {
        return diagnosticoRepository.buscarPorFuncionDiagnostico(idDiagnostico);
    }

    @Override
    public Map<String, Object> buscarDiagnosticoFormateado(Long idDiagnostico) {
        Diagnostico diagnostico = diagnosticoRepository.buscarPorFuncionDiagnostico(idDiagnostico)
            .orElseThrow(() -> new RuntimeException("Diagnóstico no encontrado"));
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("idDiagnostico", diagnostico.getIdDiagnostico());
        resultado.put("descripcionDiagnostico", diagnostico.getDescripcion());
        
        if (diagnostico.getRevision() != null && 
            diagnostico.getRevision().getConsulta() != null && 
            diagnostico.getRevision().getConsulta().getPaciente() != null) {
            
            Paciente paciente = diagnostico.getRevision().getConsulta().getPaciente();
            resultado.put("idPaciente", paciente.getIdPaciente());
            resultado.put("ci", paciente.getCi());
            
            if (paciente.getPersona() != null) {
                Persona persona = paciente.getPersona();
                resultado.put("nombrePaciente", 
                    persona.getNombre() + " " + 
                    persona.getApellidoPaterno() + " " + 
                    persona.getApellidoMaterno());
                resultado.put("edad", persona.getEdad());
            }
        }
        
        // ✅ ELIMINADO: Ya no buscas por diagnóstico
        // Optional<EvolucionClinica> evolucionOpt = evolucionService.obtenerPorDiagnosticoId(idDiagnostico);
        
        // ✅ Si necesitas mostrar tratamientos, mejor hazlo desde el frontend con el ID de evolución
        
        return resultado;
    }
}