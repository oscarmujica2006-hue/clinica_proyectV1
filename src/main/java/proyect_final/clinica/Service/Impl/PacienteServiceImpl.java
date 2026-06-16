package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Paciente;
import proyect_final.clinica.Model.Entity.Persona;
import proyect_final.clinica.Model.Dao.PacienteRepository;
import proyect_final.clinica.Service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.ArrayList;

@Service
public class PacienteServiceImpl implements PacienteService {

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
                // Actualizar campos específicos de Paciente
                paciente.setHistorialClinico(pacienteActualizado.getHistorialClinico());
                paciente.setLugarNacimiento(pacienteActualizado.getLugarNacimiento());
                paciente.setFechaNacimiento(pacienteActualizado.getFechaNacimiento());
                paciente.setOcupacion(pacienteActualizado.getOcupacion());
                paciente.setDireccion(pacienteActualizado.getDireccion());
                paciente.setTelefono(pacienteActualizado.getTelefono());
                paciente.setGradoInstruccion(pacienteActualizado.getGradoInstruccion()); // ✅ NO OLVIDES ESTE
                paciente.setEstadoCivil(pacienteActualizado.getEstadoCivil());
                paciente.setNacionesOriginarias(pacienteActualizado.getNacionesOriginarias());
                paciente.setIdioma(pacienteActualizado.getIdioma());
                paciente.setCi(pacienteActualizado.getCi());
                paciente.setTipoPaciente(pacienteActualizado.getTipoPaciente());
                
                // Actualizar datos de Persona (a través de la relación)
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

        // Buscar por concatenación nombre + apellidos
        try {
            resultados.addAll(pacienteRepository.buscarPorNombreCompleto(t));
        } catch (Exception ignored) {}

        // Buscar en los campos individuales (por si no está en el mismo orden)
        try {
            resultados.addAll(pacienteRepository.findByPersonaNombreContainingIgnoreCase(t));
        } catch (Exception ignored) {}
        try {
            resultados.addAll(pacienteRepository.findByPersonaApellidoPaternoContainingIgnoreCase(t));
        } catch (Exception ignored) {}
        try {
            resultados.addAll(pacienteRepository.findByPersonaApellidoMaternoContainingIgnoreCase(t));
        } catch (Exception ignored) {}

        // Si el término es numérico, buscar por CI exacto
        try {
            Integer ci = Integer.valueOf(t);
            resultados.addAll(pacienteRepository.findByCi(ci));
        } catch (NumberFormatException ignored) {
            // no es número, ignorar
        }

        return new ArrayList<>(resultados);
    }



    // ✅ NUEVOS MÉTODOS
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
}