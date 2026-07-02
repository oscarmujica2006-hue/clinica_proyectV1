package proyect_final.clinica.Model.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyect_final.clinica.Model.Entity.SolicitudRadiografia;
import proyect_final.clinica.Model.Entity.TipoPaciente;

import java.util.List;
import java.util.Optional;

public interface TipoPacienteRepository extends JpaRepository<TipoPaciente, Long> {
    Optional<TipoPaciente> findByNombreTipo(String nombreTipo);
    
    @Query("SELECT t FROM TipoPaciente t WHERE :edad BETWEEN t.edadMin AND t.edadMax")
    Optional<TipoPaciente> findTipoByEdad(@Param("edad") Integer edad);

    @Query("SELECT s FROM SolicitudRadiografia s WHERE s.estudiante.idEstudiante = :idEstudiante")
    List<SolicitudRadiografia> findByEstudianteId(@Param("idEstudiante") Long idEstudiante);
}