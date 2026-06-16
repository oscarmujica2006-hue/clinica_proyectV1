package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Consulta;
import proyect_final.clinica.Model.Dto.ConsultaCompletaDTO;
import java.time.LocalDate;
import java.util.List;
import java.io.IOException;
import proyect_final.clinica.Model.Dto.ConsultaConFotosDTO;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface ConsultaService {
    
    List<Consulta> obtenerTodos();
    
    Optional<Consulta> obtenerPorId(Long id);
    
    Consulta guardar(Consulta consulta);
    
    Consulta guardarConsultaCompleta(ConsultaCompletaDTO consultaDTO);
    
    void eliminar(Long id);
    
    
    List<Consulta> obtenerPorPaciente(Long idPaciente);
    
    List<Consulta> obtenerPorEstudiante(Long idEstudiante);
    
    // List<Consulta> obtenerPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);

    // esto es para buscar
    List<Consulta> buscarPorCriterio(String criterio);
    
    Optional<Consulta> obtenerConsultaCompleta(Long idConsulta);
    List<ConsultaCompletaDTO> obtenerConsultasCompletasPorPaciente(Long idPaciente);

    Consulta guardarConsultaConFotos(ConsultaConFotosDTO dto) throws IOException;

     void guardarFotoConsulta(Long idConsulta, MultipartFile archivo) throws IOException;
}