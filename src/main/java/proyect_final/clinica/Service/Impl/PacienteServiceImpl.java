package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Paciente;
import proyect_final.clinica.Model.Entity.Persona;
import proyect_final.clinica.Model.Dao.PacienteRepository;
import proyect_final.clinica.Service.PacienteService;
import proyect_final.clinica.Model.Dto.PacienteRegistroDTO;
import proyect_final.clinica.Model.Dto.RegistroPacienteResultadoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.ArrayList;

@Service
public class PacienteServiceImpl implements PacienteService {

    @PersistenceContext  
    private EntityManager entityManager;
    
    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public List<Paciente> obtenerTodos() {
        return pacienteRepository.findAll();
    }

    @Override
    public Optional<Paciente> obtenerPorId(Long id) {
        return pacienteRepository.findById(id);
    }

    @Override
    public Paciente guardar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    @Override
    public Paciente actualizar(Long id, Paciente pacienteActualizado) {
        return pacienteRepository.findById(id)
            .map(paciente -> {
                paciente.setHistorialClinico(pacienteActualizado.getHistorialClinico());
                paciente.setLugarNacimiento(pacienteActualizado.getLugarNacimiento());
                paciente.setFechaNacimiento(pacienteActualizado.getFechaNacimiento());
                paciente.setOcupacion(pacienteActualizado.getOcupacion());
                paciente.setDireccion(pacienteActualizado.getDireccion());
                paciente.setTelefono(pacienteActualizado.getTelefono());
                paciente.setGradoInstruccion(pacienteActualizado.getGradoInstruccion());
                paciente.setEstadoCivil(pacienteActualizado.getEstadoCivil());
                paciente.setNacionesOriginarias(pacienteActualizado.getNacionesOriginarias());
                paciente.setIdioma(pacienteActualizado.getIdioma());
                paciente.setCi(pacienteActualizado.getCi());
                paciente.setTipoPaciente(pacienteActualizado.getTipoPaciente());
                
                if (pacienteActualizado.getPersona() != null) {
                    Persona persona = paciente.getPersona();
                    Persona personaActualizada = pacienteActualizado.getPersona();
                    
                    persona.setNombre(personaActualizada.getNombre());
                    persona.setApellidoPaterno(personaActualizada.getApellidoPaterno());
                    persona.setApellidoMaterno(personaActualizada.getApellidoMaterno());
                    persona.setEdad(personaActualizada.getEdad());
                    persona.setSexo(personaActualizada.getSexo());
                }
                
                return pacienteRepository.save(paciente);
            })
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + id));
    }

    @Override
    public void eliminar(Long id) {
        pacienteRepository.deleteById(id);
    }

    @Override
    public List<Paciente> buscarPorOcupacion(String ocupacion) {
        return pacienteRepository.findByOcupacion(ocupacion);
    }

    @Override
    public List<Paciente> buscarPorLugarNacimiento(String lugarNacimiento) {
        return pacienteRepository.findByLugarNacimientoContainingIgnoreCase(lugarNacimiento);
    }

    @Override
    public List<Paciente> buscarPorEstadoCivil(String estadoCivil) {
        return pacienteRepository.findByEstadoCivil(estadoCivil);
    }

    @Override
    public Optional<Paciente> buscarPorHistorialClinico(String historialClinico) {
        return pacienteRepository.findByHistorialClinico(historialClinico);
    }

    @Override
    public List<Paciente> buscarPorNombreCompleto(String nombreCompleto) {
        return pacienteRepository.buscarPorNombreCompleto(nombreCompleto);
    }

    @Override
    public List<Paciente> buscarPorTermino(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String t = termino.trim();
        Set<Paciente> resultados = new LinkedHashSet<>();

        try {
            resultados.addAll(pacienteRepository.buscarPorNombreCompleto(t));
        } catch (Exception ignored) {}

        try {
            resultados.addAll(pacienteRepository.findByPersonaNombreContainingIgnoreCase(t));
        } catch (Exception ignored) {}
        try {
            resultados.addAll(pacienteRepository.findByPersonaApellidoPaternoContainingIgnoreCase(t));
        } catch (Exception ignored) {}
        try {
            resultados.addAll(pacienteRepository.findByPersonaApellidoMaternoContainingIgnoreCase(t));
        } catch (Exception ignored) {}

        try {
            Integer ci = Integer.valueOf(t);
            resultados.addAll(pacienteRepository.findByCi(ci));
        } catch (NumberFormatException ignored) {}

        return new ArrayList<>(resultados);
    }

    @Override
    public List<Paciente> buscarPorApellidoPaterno(String apellidoPaterno) {
        return pacienteRepository.findByPersonaApellidoPaternoContainingIgnoreCase(apellidoPaterno);
    }

    @Override
    public List<Paciente> buscarPorApellidoMaterno(String apellidoMaterno) {
        return pacienteRepository.findByPersonaApellidoMaternoContainingIgnoreCase(apellidoMaterno);
    }

    @Override
    public List<Paciente> buscarPorNombre(String nombre) {
        return pacienteRepository.findByPersonaNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<Paciente> buscarPorCi(Integer ci) {
        return pacienteRepository.findByCi(ci);
    }

    @Override
    public List<Paciente> buscarPorCiContaining(String ci) {
        return pacienteRepository.findByCiContaining(ci);
    }

    // ✅ MÉTODO CORREGIDO
  // En PacienteServiceImpl.java - Reemplazar el método registrarPacienteCompleto
@Override
@Transactional
public RegistroPacienteResultadoDTO registrarPacienteCompleto(PacienteRegistroDTO dto) {
    try {
        // Convertir LocalDate a java.sql.Date
        java.sql.Date fechaNacimiento = dto.getFechaNacimiento() != null ? 
            java.sql.Date.valueOf(dto.getFechaNacimiento()) : null;
        
        // ✅ Llamar al método del repository (NO usar StoredProcedureQuery)
        String resultado = pacienteRepository.registrarPacienteCompleto(
            dto.getNombre(),
            dto.getApellidoPaterno(),
            dto.getApellidoMaterno(),
            dto.getSexo() != null ? dto.getSexo().toString() : null,
            dto.getUsuRegPer() != null ? dto.getUsuRegPer() : 1,
            dto.getHistorialClinico(),
            dto.getLugarNacimiento(),
            fechaNacimiento,
            dto.getOcupacion(),
            dto.getDireccion(),
            dto.getTelefono(),
            dto.getGradoInstruccion(),
            dto.getEstadoCivil(),
            dto.getCi(),
            dto.getIdTipoPaciente(),
            dto.getUsuRegPac() != null ? dto.getUsuRegPac() : 1,
            dto.getCodigoArchivo(),
            dto.getUbicacionFisica(),
            dto.getUsuRegArc() != null ? dto.getUsuRegArc() : 1,
            dto.getNacionesOriginarias(),
            dto.getIdioma(),
            dto.getPatologiaFamiliar()
        );
        
        System.out.println("Resultado de la función: " + resultado);
        
        // ✅ Verificar el resultado
        if (resultado != null && resultado.startsWith("EXITO:")) {
            return new RegistroPacienteResultadoDTO(true, "✅ " + resultado.replace("EXITO: ", ""));
        } else {
            String mensajeError = resultado != null ? resultado.replace("ERROR: ", "") : "Error desconocido";
            return new RegistroPacienteResultadoDTO(false, "❌ " + mensajeError);
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        String mensaje = "❌ Error al registrar el paciente";
        String errorMsg = e.getMessage();
        
        if (errorMsg != null) {
            if (errorMsg.contains("HISTORIAL_DUPLICADO") || errorMsg.contains("duplicate key")) {
                mensaje = "❌ El historial clínico ya está registrado";
            } else if (errorMsg.contains("CI_DUPLICADO") || errorMsg.contains("duplicado")) {
                mensaje = "❌ El CI ya está registrado para otro paciente";
            } else if (errorMsg.contains("TIPO_PACIENTE_INEXISTENTE")) {
                mensaje = "❌ El tipo de paciente seleccionado no existe";
            } else if (errorMsg.contains("not-null") || errorMsg.contains("nullable")) {
                mensaje = "❌ Faltan datos obligatorios";
            } else {
                mensaje = "❌ Error: " + errorMsg;
            }
        }
        
        return new RegistroPacienteResultadoDTO(false, mensaje);
    }

}
}