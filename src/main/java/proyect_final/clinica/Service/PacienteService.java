package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Paciente;
import java.util.List;
import java.util.Optional;

public interface PacienteService {
    
    List<Paciente> obtenerTodos();
    
    Optional<Paciente> obtenerPorId(Long id);
    
    Paciente guardar(Paciente paciente);
    
    Paciente actualizar(Long id, Paciente paciente);
    
    void eliminar(Long id);
    
    // Métodos específicos de Paciente
    List<Paciente> buscarPorOcupacion(String ocupacion);
    List<Paciente> buscarPorLugarNacimiento(String lugarNacimiento);
    List<Paciente> buscarPorEstadoCivil(String estadoCivil);
    
    // Método para buscar por historial clínico
    Optional<Paciente> buscarPorHistorialClinico(String historialClinico);
    

    // ✅ MÉTODO PARA BÚSQUEDA POR NOMBRE COMPLETO (STRING ÚNICO)
    List<Paciente> buscarPorNombreCompleto(String nombreCompleto);

    // Buscar por término único que puede ser nombre completo o CI
    List<Paciente> buscarPorTermino(String termino);
    
    // ✅ NUEVOS MÉTODOS PARA BÚSQUEDA POR APELLIDOS
    List<Paciente> buscarPorApellidoPaterno(String apellidoPaterno);
    List<Paciente> buscarPorApellidoMaterno(String apellidoMaterno);
    List<Paciente> buscarPorNombre(String nombre);

    
    List<Paciente> buscarPorCi(Integer ci);
    List<Paciente> buscarPorCiContaining(String ci);
}