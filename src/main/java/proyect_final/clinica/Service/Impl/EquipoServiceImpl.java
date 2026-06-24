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

    // ✅ CREAR EQUIPO CON FUNCIÓN (devuelve String)
    @Override
    public String crearEquipoConFuncion(String codigo, String nombre, String estado, String usuario) {
        try {
            // Validaciones básicas
            if (codigo == null || codigo.trim().isEmpty()) {
                return "ERROR: El código es obligatorio";
            }
            if (nombre == null || nombre.trim().isEmpty()) {
                return "ERROR: El nombre es obligatorio";
            }
            
            // Llamar a la función de PostgreSQL
            Object[] resultadoArray = equipoRepository.crearEquipoConUsuario(
                codigo.toUpperCase().trim(),
                nombre.trim(),
                estado != null ? estado : "ACTIVO",
                usuario != null ? usuario : "SISTEMA"
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





}