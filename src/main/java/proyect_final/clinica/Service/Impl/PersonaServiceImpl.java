package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Persona;
import proyect_final.clinica.Model.Dao.PersonaRepository;
import proyect_final.clinica.Service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PersonaServiceImpl implements PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public List<Persona> obtenerTodos() {
        return personaRepository.findAll();
    }

    @Override
    public Optional<Persona> obtenerPorId(Long id) {
        return personaRepository.findById(id);
    }

    @Override
    public Persona guardar(Persona persona) {
        return personaRepository.save(persona);
    }

    @Override
    public Persona actualizar(Long id, Persona personaActualizada) {
        return personaRepository.findById(id)
            .map(persona -> {
                persona.setNombre(personaActualizada.getNombre());
                persona.setApellidoPaterno(personaActualizada.getApellidoPaterno());
                persona.setApellidoMaterno(personaActualizada.getApellidoMaterno());
                persona.setEdad(personaActualizada.getEdad());
                persona.setSexo(personaActualizada.getSexo());
                return personaRepository.save(persona);
            })
            .orElseThrow(() -> new RuntimeException("Persona no encontrada con id: " + id));
    }

    @Override
    public void eliminar(Long id) {
        personaRepository.deleteById(id);
    }

    @Override
    public List<Persona> buscarPorNombre(String nombre) {
        return personaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // ✅ NUEVOS MÉTODOS
    @Override
    public List<Persona> buscarPorApellidoPaterno(String apellidoPaterno) {
        return personaRepository.findByApellidoPaternoContainingIgnoreCase(apellidoPaterno);
    }

    @Override
    public List<Persona> buscarPorApellidoMaterno(String apellidoMaterno) {
        return personaRepository.findByApellidoMaternoContainingIgnoreCase(apellidoMaterno);
    }

    @Override
    public List<Persona> buscarPorEdad(Integer edad) {
        return personaRepository.findByEdad(edad);
    }

    @Override
    public List<Persona> buscarPorSexo(Character sexo) {
        return personaRepository.findBySexo(sexo);
    }

    // ✅ CORREGIDO: Cambiar "apellido" por "apellidoPaterno"
    @Override
    public List<Persona> buscarPorNombreYApellido(String nombre, String apellidoPaterno) {
        return personaRepository.findByNombreAndApellidoPaterno(nombre, apellidoPaterno);
    }
}