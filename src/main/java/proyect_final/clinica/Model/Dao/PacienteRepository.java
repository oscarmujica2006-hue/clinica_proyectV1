package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
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


 // En PacienteRepository.java
@Query(value = "SELECT registrar_paciente_completo(" +
       "CAST(:p_nombre AS VARCHAR), " +
       "CAST(:p_apellido_paterno AS VARCHAR), " +
       "CAST(:p_apellido_materno AS VARCHAR), " +
       "CAST(:p_sexo AS VARCHAR), " +
       "CAST(:p_usu_reg_per AS INTEGER), " +
       "CAST(:p_historial_clinico AS VARCHAR), " +
       "CAST(:p_lugar_nacimiento AS VARCHAR), " +
       "CAST(:p_fecha_nacimiento AS DATE), " +
       "CAST(:p_ocupacion AS VARCHAR), " +
       "CAST(:p_direccion AS VARCHAR), " +
       "CAST(:p_telefono AS VARCHAR), " +
       "CAST(:p_grado_instruccion AS VARCHAR), " +
       "CAST(:p_estado_civil AS VARCHAR), " +
       "CAST(:p_ci AS INTEGER), " +
       "CAST(:p_id_tipo_paciente AS BIGINT), " +
       "CAST(:p_usu_reg_pac AS INTEGER), " +
       "CAST(:p_codigo_archivo AS VARCHAR), " +
       "CAST(:p_ubicacion_fisica AS VARCHAR), " +
       "CAST(:p_usu_reg_arc AS INTEGER), " +
       "CAST(:p_naciones_originarias AS VARCHAR), " +
       "CAST(:p_idioma AS VARCHAR), " +
       "CAST(:p_patologia_familiar AS VARCHAR))", 
       nativeQuery = true)
String registrarPacienteCompleto(
    @Param("p_nombre") String nombre,
    @Param("p_apellido_paterno") String apellidoPaterno,
    @Param("p_apellido_materno") String apellidoMaterno,
    @Param("p_sexo") String sexo,
    @Param("p_usu_reg_per") Integer usuRegPer,
    @Param("p_historial_clinico") String historialClinico,
    @Param("p_lugar_nacimiento") String lugarNacimiento,
    @Param("p_fecha_nacimiento") java.sql.Date fechaNacimiento,
    @Param("p_ocupacion") String ocupacion,
    @Param("p_direccion") String direccion,
    @Param("p_telefono") String telefono,
    @Param("p_grado_instruccion") String gradoInstruccion,
    @Param("p_estado_civil") String estadoCivil,
    @Param("p_ci") Integer ci,
    @Param("p_id_tipo_paciente") Long idTipoPaciente,
    @Param("p_usu_reg_pac") Integer usuRegPac,
    @Param("p_codigo_archivo") String codigoArchivo,
    @Param("p_ubicacion_fisica") String ubicacionFisica,
    @Param("p_usu_reg_arc") Integer usuRegArc,
    @Param("p_naciones_originarias") String nacionesOriginarias,
    @Param("p_idioma") String idioma,
    @Param("p_patologia_familiar") String patologiaFamiliar
);
}