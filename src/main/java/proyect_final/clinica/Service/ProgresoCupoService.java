package proyect_final.clinica.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.*;
import java.util.*;

@Service
@Transactional
public class ProgresoCupoService {

    @Autowired
    private EntityManager entityManager;

    // ✅ CAMBIADO: Long → Integer
    public List<Map<String, Object>> obtenerProgresoEstudiante(Integer idEstudiante) {
        try {
            System.out.println("📊 Ejecutando función para estudiante: " + idEstudiante);
            
            Query query = entityManager.createNativeQuery(
                "SELECT * FROM fn_obtener_progreso_estudiante(:idEstudiante)"
            );
            query.setParameter("idEstudiante", idEstudiante);
            
            List<Object[]> resultados = query.getResultList();
            System.out.println("📊 Resultados obtenidos: " + resultados.size());
            
            List<Map<String, Object>> progreso = new ArrayList<>();
            
            for (Object[] row : resultados) {
                Map<String, Object> map = new HashMap<>();
                map.put("idProgreso", row[0]);
                map.put("idEstudiante", row[1]);
                map.put("nombreEstudiante", row[2]);
                map.put("idTratamiento", row[3]);
                map.put("nombreTratamiento", row[4]);
                map.put("cupoTotal", row[5]);
                map.put("cupoRealizado", row[6]);
                map.put("cupoPendiente", row[7]);
                map.put("estado", row[8]);
                map.put("porcentaje", row[9]);
                map.put("fechaInicio", row[10]);
                map.put("fechaCompletado", row[11]);
                progreso.add(map);
            }
            
            return progreso;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener progreso: " + e.getMessage(), e);
        }
    }

    // ✅ CAMBIADO: Long → Integer
    public Map<String, Object> obtenerEstadisticasEstudiante(Integer idEstudiante) {
        try {
            Query query = entityManager.createNativeQuery(
                "SELECT * FROM fn_estadisticas_estudiante(:idEstudiante)"
            );
            query.setParameter("idEstudiante", idEstudiante);
            
            List<Object[]> resultados = query.getResultList();
            
            if (resultados.isEmpty()) {
                return Map.of(
                    "totalTratamientos", 0L,
                    "totalRequeridos", 0,
                    "totalRealizados", 0,
                    "porcentajeGeneral", 0.0
                );
            }
            
            Object[] row = resultados.get(0);
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalTratamientos", row[0]);
            estadisticas.put("totalRequeridos", row[1]);
            estadisticas.put("totalRealizados", row[2]);
            estadisticas.put("porcentajeGeneral", row[3]);
            
            return estadisticas;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener estadísticas: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // RESUMEN GLOBAL (opcional)
    // ==========================================
    public List<Map<String, Object>> obtenerResumenGlobal() {
        try {
            Query query = entityManager.createNativeQuery(
                "SELECT * FROM fn_resumen_global_cupos()"
            );
            
            List<Object[]> resultados = query.getResultList();
            List<Map<String, Object>> resumen = new ArrayList<>();
            
            for (Object[] row : resultados) {
                Map<String, Object> map = new HashMap<>();
                map.put("nombreTratamiento", row[0]);
                map.put("cupoGlobal", row[1]);
                map.put("estudiantesInscritos", row[2]);
                map.put("cuposAsignados", row[3]);
                map.put("cuposRealizados", row[4]);
                map.put("porcentajeGlobal", row[5]);
                resumen.add(map);
            }
            
            return resumen;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener resumen global: " + e.getMessage(), e);
        }
    }
}