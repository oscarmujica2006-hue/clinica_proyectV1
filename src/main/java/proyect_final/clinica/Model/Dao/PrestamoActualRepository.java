package proyect_final.clinica.Model.Dao;
import proyect_final.clinica.Model.Entity.PrestamoActual;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

import java.util.*;
public interface PrestamoActualRepository extends JpaRepository<PrestamoActual, Long> {
    List<PrestamoActual> findByIdEstudiante(Long idEstudiante);
    List<PrestamoActual> findByIdArchivo(Long idArchivo);
    List<PrestamoActual> findByTipoPrestamo(String tipoPrestamo);
    List<PrestamoActual> findByFechaPrestamo(LocalDate fechaPrestamo);
    List<PrestamoActual> findByFechaLimitePrestamo(LocalDate fechaLimitePrestamo);
    List<PrestamoActual> findByEncargadoPrestamo(String encargadoPrestamo);
    List<PrestamoActual> findByFechaDevolucion(LocalDate fechaDevolucion);
    List<PrestamoActual> findByTipoPrestamoContainingIgnoreCase(String tipoPrestamo);
    List<PrestamoActual> findByEncargadoPrestamoContainingIgnoreCase(String encargadoPrestamo);
    List<PrestamoActual> findByEstadoPrestamo(String estadoPrestamo);

        // Buscar préstamo activo de un estudiante
    List<PrestamoActual> findByIdEstudianteAndEstadoPrestamo(Long idEstudiante, String estadoPrestamo);
    
    // Buscar préstamos vencidos (para el scheduler)
    @Query("SELECT p FROM PrestamoActual p WHERE p.estadoPrestamo = 'ACTIVO' AND p.fechaLimitePrestamo < CURRENT_DATE")
    List<PrestamoActual> findPrestamosVencidos();



    boolean existsByIdEstudianteAndIdArchivoAndEstadoPrestamo(
        Long idEstudiante, Long idArchivo, String estadoPrestamo);
}