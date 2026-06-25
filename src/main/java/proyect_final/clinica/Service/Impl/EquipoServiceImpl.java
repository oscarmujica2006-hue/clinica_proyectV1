package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Equipo;
import proyect_final.clinica.Model.Dao.EquipoRepository;
import proyect_final.clinica.Service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class EquipoServiceImpl implements EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Override
    public List<Equipo> obtenerTodos() {
        return equipoRepository.findAll();
    }

    @Override
    public Optional<Equipo> obtenerPorId(Long id) {
        return equipoRepository.findById(id);
    }

    @Override
    public Equipo guardar(Equipo equipo) {
        return equipoRepository.save(equipo);
    }

    @Override
    public void eliminar(Long id) {
        equipoRepository.deleteById(id);
    }
    
    // ✅ CREAR EQUIPO CON FUNCIÓN (devuelve String)
    @Override
    public String crearEquipoConFuncion(String codigo, String nombre, String estado, Integer usuario) {
        try {
            // Validaciones básicas
            if (codigo == null || codigo.trim().isEmpty()) {
                return "ERROR: El código es obligatorio";
            }
            if (nombre == null || nombre.trim().isEmpty()) {
                return "ERROR: El nombre es obligatorio";
            }
            
            // ✅ CORREGIDO: Si usuario es null, usar 1 (SISTEMA) en lugar de "SISTEMA"
            Integer usuarioId = (usuario != null) ? usuario : 1;
            
            // Llamar a la función de PostgreSQL
            Object[] resultadoArray = equipoRepository.crearEquipoConUsuario(
                codigo.toUpperCase().trim(),
                nombre.trim(),
                estado != null ? estado : "ACTIVO",
                usuarioId  // ✅ AHORA PASAMOS INTEGER
            );
            
            // El resultado viene como Object[], convertir a String
            if (resultadoArray != null && resultadoArray.length > 0) {
                return resultadoArray[0].toString();
            }
            
            return "ERROR: No se recibió respuesta de la función";
            
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    @Override
    @Transactional
    public Equipo actualizarEstadoEquipo(Long idEquipo, String nuevoEstado) {
        Equipo equipo = equipoRepository.findById(idEquipo)
            .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        
        equipo.setEstadoEquipo(nuevoEstado);
        return equipoRepository.save(equipo);
    }
}