package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    
    List<Consulta> findByPacienteIdPaciente(Long idPaciente);
    
    List<Consulta> findByEstudianteIdEstudiante(Long idEstudiante);
    
    // @Query("SELECT c FROM Consulta c WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin")
    // List<Consulta> findByFechaBetween(@Param("fechaInicio") LocalDate fechaInicio, 
    //                                 @Param("fechaFin") LocalDate fechaFin);

    // CORRECCIÓN: Usar paciente.persona.nombre en lugar de paciente.nombres
    @Query("SELECT c FROM Consulta c WHERE " +
        "c.paciente.persona.nombre LIKE %:criterio% OR " +
        "c.paciente.persona.apellidoPaterno LIKE %:criterio% OR " +
        "c.paciente.persona.apellidoMaterno LIKE %:criterio% OR " +
        "CAST(c.idConsulta AS string) LIKE %:criterio%")
    List<Consulta> buscarPorCriterio(@Param("criterio") String criterio);
    
    // ✅ NUEVO MÉTODO - Cargar consultas con todas las relaciones
    @Query("SELECT c FROM Consulta c " +
           "LEFT JOIN FETCH c.informante " +
           "LEFT JOIN FETCH c.patologiaPersonal " +
           "LEFT JOIN FETCH c.tratamientoMedico " +
           "LEFT JOIN FETCH c.examenExtraOral " +
           "LEFT JOIN FETCH c.examenIntraOral " +
           "LEFT JOIN FETCH c.antecedentesBucodentales " +
           "LEFT JOIN FETCH c.antecedentesHigieneOral " +
           "LEFT JOIN FETCH c.paciente " +
           "LEFT JOIN FETCH c.estudiante " +
           "WHERE c.paciente.idPaciente = :idPaciente")
    List<Consulta> findByPacienteIdPacienteWithRelations(@Param("idPaciente") Long idPaciente);
}