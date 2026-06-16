package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.PrestamoActual;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.time.LocalDate;

public interface PrestamoActualService {
    
    // CRUD BÁSICO
    List<PrestamoActual> obtenerTodos();
    Optional<PrestamoActual> obtenerPorId(Long id);
    PrestamoActual guardar(PrestamoActual prestamoActual);
    PrestamoActual actualizar(Long id, PrestamoActual prestamoActualActualizado);
    void eliminar(Long id);
    
    // BÚSQUEDAS ESPECÍFICAS
    List<PrestamoActual> buscarPorIdEstudiante(Long idEstudiante);
    List<PrestamoActual> buscarPorIdArchivo(Long idArchivo);
    
    // MÉTODOS DE NEGOCIO
    PrestamoActual registrarPrestamo(PrestamoActual prestamo);
    PrestamoActual devolverArchivo(Long idPrestamo, LocalDate fechaDevolucion);
    void desbloquearEstudiante(Long idEstudiante);
    boolean puedeRealizarConsulta(Long idEstudiante, Long idPaciente);
    
    // NUEVOS MÉTODOS PARA VALIDACIÓN
    Optional<PrestamoActual> obtenerPrestamoActivoPorEstudiante(Long idEstudiante);
    Map<String, Object> obtenerInfoPacientePrestado(Long idEstudiante);



    
    // ✅ CORRECTO - Solo la declaración del método
    List<PrestamoActual> buscarPorEstado(String estado);
}