package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.PrestamoEquipo;
import proyect_final.clinica.Model.Dao.PrestamoEquipoRepository;
import proyect_final.clinica.Service.PrestamoEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PrestamoEquipoServiceImpl implements PrestamoEquipoService {

    @Autowired
    private PrestamoEquipoRepository prestamoEquipoRepository;

    @Override
    public List<PrestamoEquipo> obtenerTodos() {
        return prestamoEquipoRepository.findAll();
    }

    @Override
    public Optional<PrestamoEquipo> obtenerPorId(Long id) {
        return prestamoEquipoRepository.findById(id);
    }

    @Override
    public PrestamoEquipo guardar(PrestamoEquipo solicitud) {
        // Si es un nuevo préstamo, validar que el equipo no esté prestado
        if (solicitud.getIdPrestamoEquipo() == null) {
            if (solicitud.getIdEquipo() != null) {
                List<PrestamoEquipo> prestamosActivos = prestamoEquipoRepository
                    .findByIdEquipo_IdEquipoAndEstadoDevolucion(
                        solicitud.getIdEquipo().getIdEquipo(), 
                        "PENDIENTE"
                    );
                
                if (!prestamosActivos.isEmpty()) {
                    throw new RuntimeException("El equipo ya está prestado y no ha sido devuelto");
                }
            }

            // Asignar fecha y hora si no vienen
            if (solicitud.getFechaEntrePrestamo() == null) {
                solicitud.setFechaEntrePrestamo(LocalDate.now());
            }
            if (solicitud.getHoraEntreEquipo() == null) {
                solicitud.setHoraEntreEquipo(new Time(System.currentTimeMillis()));
            }
            
            // Estado inicial si no tiene
            if (solicitud.getEstadoDevolucion() == null) {
                solicitud.setEstadoDevolucion("PENDIENTE");
            }
        }

        return prestamoEquipoRepository.save(solicitud);
    }

    @Override
    public void eliminar(Long id) {
        prestamoEquipoRepository.deleteById(id);
    }

    @Override
    public List<PrestamoEquipo> findByDocenteId(Long idDocente) {
        return prestamoEquipoRepository.findByIdDocente_IdDocente(idDocente);
    }

    @Override
    public List<PrestamoEquipo> findByEncargadoId(Long idEncargado) {
        return prestamoEquipoRepository.findByIdEncargadoInsumo_IdEncargadoInsumo(idEncargado);
    }

    @Override
    @Transactional
    public PrestamoEquipo aprobarSolicitud(Long idSolicitud) {
        PrestamoEquipo prestamo = prestamoEquipoRepository.findById(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        // En tu entidad no hay estado "APROBADO", así que simplemente registramos
        // que el préstamo está activo
        prestamo.setEstadoDevolucion("APROBADO");
        return prestamoEquipoRepository.save(prestamo);
    }

    @Override
    @Transactional
    public PrestamoEquipo rechazarSolicitud(Long idSolicitud, String motivo) {
        PrestamoEquipo prestamo = prestamoEquipoRepository.findById(idSolicitud)
            .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        // Rechazar = cancelar el préstamo
        prestamo.setEstadoDevolucion("RECHAZADO");
        // Guardar el motivo en observaciones
        prestamo.setObservacionPrestamo("RECHAZADO: " + motivo);
        return prestamoEquipoRepository.save(prestamo);
    }

    // ===== MÉTODOS ADICIONALES ÚTILES (NO están en la interface) =====
    
    public List<PrestamoEquipo> findPrestamosActivos() {
        return prestamoEquipoRepository.findByEstadoDevolucion("PENDIENTE");
    }

    @Transactional
    public PrestamoEquipo registrarDevolucion(Long idPrestamo) {
        PrestamoEquipo prestamo = prestamoEquipoRepository.findById(idPrestamo)
            .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        if (!"PENDIENTE".equals(prestamo.getEstadoDevolucion())) {
            throw new RuntimeException("Este préstamo ya fue devuelto o cancelado");
        }

        prestamo.setEstadoDevolucion("DEVUELTO");
        prestamo.setHoraDevolEquipo(new Time(System.currentTimeMillis()));
        return prestamoEquipoRepository.save(prestamo);
    }
}