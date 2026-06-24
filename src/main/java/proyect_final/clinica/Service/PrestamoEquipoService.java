package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.*;
import java.util.List;
import java.util.Optional;

public interface PrestamoEquipoService {

    // CRUD Básico
    List<PrestamoEquipo> obtenerTodos();

    Optional<PrestamoEquipo> obtenerPorId(Long id);

    PrestamoEquipo guardar(PrestamoEquipo solicitud);

    void eliminar(Long id);



    // Búsquedas
    List<PrestamoEquipo> findByDocenteId(Long idDocente);


    // Para encargado
    List<PrestamoEquipo> findByEncargadoId(Long idEncargado);

    // Aprobar/Rechazar
    PrestamoEquipo aprobarSolicitud(Long idSolicitud);

    PrestamoEquipo rechazarSolicitud(Long idSolicitud, String motivo);


}