package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.PrestamoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoEquipoRepository extends JpaRepository<PrestamoEquipo, Long> {

    // Buscar por docente
    List<PrestamoEquipo> findByIdDocente_IdDocente(Long idDocente);

    // Buscar por encargado de insumo
    List<PrestamoEquipo> findByIdEncargadoInsumo_IdEncargadoInsumo(Long idEncargadoInsumo);

    // Buscar por estado de devolución
    List<PrestamoEquipo> findByEstadoDevolucion(String estadoDevolucion);

    // Buscar préstamos activos de un equipo específico
    List<PrestamoEquipo> findByIdEquipo_IdEquipoAndEstadoDevolucion(Long idEquipo, String estadoDevolucion);
}