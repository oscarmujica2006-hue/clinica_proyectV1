package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    
    // Métodos específicos para Paciente
    List<Paciente> findByOcupacion(String ocupacion);
    
    List<Paciente> findByLugarNacimientoContainingIgnoreCase(String lugarNacimiento);
    
    List<Paciente> findByEstadoCivil(String estadoCivil);
    
    // ✅ MÉTODO PARA BUSCAR POR HISTORIAL CLÍNICO
    Optional<Paciente> findByHistorialClinico(String historialClinico);

    // Búsquedas a través de la relación Persona - CORREGIDAS
    List<Paciente> findByPersonaNombreContainingIgnoreCase(String nombre);
    List<Paciente> findByPersonaApellidoPaternoContainingIgnoreCase(String apellidoPaterno);
    List<Paciente> findByPersonaApellidoMaternoContainingIgnoreCase(String apellidoMaterno);

    // ✅ NUEVO MÉTODO: Buscar por nombre completo (concatena nombre y apellidos)
    @Query("SELECT p FROM Paciente p WHERE " +
           "LOWER(CONCAT(p.persona.nombre, ' ', p.persona.apellidoPaterno, ' ', p.persona.apellidoMaterno)) " +
           "LIKE LOWER(CONCAT('%', :nombreCompleto, '%'))")
    List<Paciente> buscarPorNombreCompleto(@Param("nombreCompleto") String nombreCompleto);

    // ✅ Este método ya existe, verifica que funcione
    List<Paciente> findByCi(Integer ci);
    
    // ✅ AGREGAR para búsqueda parcial por CI
    @Query("SELECT p FROM Paciente p WHERE CAST(p.ci AS string) LIKE %:ci%")
    List<Paciente> findByCiContaining(@Param("ci") String ci);
}