package proyect_final.clinica.Service;


import proyect_final.clinica.Model.Entity.Docente;
import proyect_final.clinica.Model.Dto.DocenteDTO;
import java.util.List;
import java.util.Optional;

public interface DocenteService {
    
    // Métodos básicos CRUD
    List<Docente> obtenerTodos();
    List<Docente> obtenerTodosActivos();
    Optional<Docente> obtenerPorId(Long id);
    Docente guardar(Docente docente);
    void eliminar(Long id);
    boolean existePorCodigoDocente(Integer codigoDocente);
    
    // Métodos de búsqueda específicos
    Optional<Docente> obtenerPorCodigoDocente(Integer codigoDocente);
    Optional<Docente> obtenerPorIdUsuario(Long idUsuario);
    List<Docente> obtenerPorClinica(Long idClinica);
    List<Docente> obtenerActivosPorClinica(Long idClinica);
    List<Docente> buscarPorEspecialidad(String especialidad);
    List<Docente> buscarPorNombre(String nombre);
    List<Docente> buscarPorNombreUsuario(String username);
    
    // Métodos para DTOs
    List<DocenteDTO> obtenerTodosDTO();
    List<DocenteDTO> obtenerActivosDTO();
    Optional<DocenteDTO> obtenerDTOPorId(Long id);
    DocenteDTO convertirADTO(Docente docente);
    Docente convertirAEntidad(DocenteDTO docenteDTO);
    
    // Métodos de utilidad
    String getNombreCompletoDocente(Docente docente);
    boolean existeRelacionConUsuario(Long idUsuario);
}