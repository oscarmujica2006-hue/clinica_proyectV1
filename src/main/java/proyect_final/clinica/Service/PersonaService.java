package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Persona;
import java.util.List;
import java.util.Optional;

public interface PersonaService {
    List<Persona> obtenerTodos();
    Optional<Persona> obtenerPorId(Long id);
    Persona guardar(Persona persona);
    Persona actualizar(Long id, Persona personaActualizada);
    void eliminar(Long id);
    List<Persona> buscarPorNombre(String nombre);
    
    // ✅ NUEVOS MÉTODOS
    List<Persona> buscarPorApellidoPaterno(String apellidoPaterno);
    List<Persona> buscarPorApellidoMaterno(String apellidoMaterno);
    List<Persona> buscarPorEdad(Integer edad);
    List<Persona> buscarPorSexo(Character sexo);
    
    // ✅ CORREGIDO: Cambiar "apellido" por "apellidoPaterno"
    List<Persona> buscarPorNombreYApellido(String nombre, String apellidoPaterno);
}