package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Equipo;
import java.util.List;
import java.util.Optional;
import java.util.Map;

public interface EquipoService {

    // ✅ Método para crear equipo - devuelve String (el resultado de la función)
    String crearEquipoConFuncion(
        String codigoEquipo,
        String nombreEquipo,
        String estadoEquipo,
        Integer usuario
    );

    List<Equipo> obtenerTodos();
    
    Optional<Equipo> obtenerPorId(Long id);
    
    Equipo guardar(Equipo equipo);
    
    void eliminar(Long id);
    Equipo actualizarEstadoEquipo(Long idEquipo, String nuevoEstado);

}