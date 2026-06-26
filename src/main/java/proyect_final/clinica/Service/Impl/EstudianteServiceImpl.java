package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Model.Dao.EstudianteRepository;
import proyect_final.clinica.Service.EstudianteService;
import proyect_final.clinica.Model.Dto.ProgresoMateriaDTO;
import proyect_final.clinica.Model.Dto.ProgresoTratamientoDTO;
import proyect_final.clinica.Model.Entity.InscripcionMateria;
import proyect_final.clinica.Model.Entity.Materia;
import proyect_final.clinica.Model.Entity.Tratamiento;
import proyect_final.clinica.Model.Entity.Cupo;
import proyect_final.clinica.Model.Entity.DiagnosticoTratamiento;
import proyect_final.clinica.Model.Entity.DiagnosticoTratamientoDiente;
import proyect_final.clinica.Model.Dao.CupoRepository;
import proyect_final.clinica.Model.Dao.InscripcionMateriaRepository;
import proyect_final.clinica.Model.Dao.DiagnosticoTratamientoRepository;
import proyect_final.clinica.Model.Dao.DiagnosticoTratamientoDienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EstudianteServiceImpl implements EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;
    
    @Autowired
    private InscripcionMateriaRepository inscripcionMateriaRepository;

    @Autowired
    private CupoRepository cupoRepository;
    
    @Autowired
    private DiagnosticoTratamientoRepository diagnosticoTratamientoRepository;
    
    @Autowired
    private DiagnosticoTratamientoDienteRepository diagnosticoTratamientoDienteRepository;

    // ==================== MÉTODOS BÁSICOS ====================
    
    @Override
    public List<Estudiante> obtenerTodos() {
        return estudianteRepository.findAll();
    }

    @Override
    public Optional<Estudiante> obtenerPorId(Long id) {
        return estudianteRepository.findById(id);
    }

    @Override
    public Estudiante guardar(Estudiante estudiante) {
        return estudianteRepository.save(estudiante);
    }



    @Override
    public List<Estudiante> buscarPorCodigoEstudiante(Integer codigo) {
        return estudianteRepository.findAllByCodigoEstudiante(codigo);
    }

    // ==================== MÉTODOS DE BÚSQUEDA ====================
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Estudiante> buscarPorCodigoExacto(Integer codigoEstudiante) {
        return estudianteRepository.findByCodigoEstudiante(codigoEstudiante);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existePorCodigoEstudiante(Integer codigoEstudiante) {
        return estudianteRepository.existsByCodigoEstudiante(codigoEstudiante);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estudiante> buscarPorGestion(String gestion) {
        return estudianteRepository.findByGestion(gestion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estudiante> buscarPorBloqueado(Boolean bloqueado) {
        return estudianteRepository.findByBloqueado(bloqueado);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estudiante> buscarPorNombreUsuario(String username) {
        return estudianteRepository.buscarPorNombreUsuario(username);
    }

    // ==================== MÉTODO PARA OBTENER PROGRESO ====================
    
    @Override
    public List<ProgresoMateriaDTO> obtenerProgresoPorEstudiante(Long idEstudiante) {
        System.out.println("=== OBTENIENDO PROGRESO PARA ESTUDIANTE ID: " + idEstudiante);
        
        // Verificar que el estudiante exista
        Optional<Estudiante> estudianteOpt = estudianteRepository.findById(idEstudiante);
        if (estudianteOpt.isEmpty()) {
            System.out.println("❌ Estudiante no encontrado");
            return new ArrayList<>();
        }
        
        Estudiante estudiante = estudianteOpt.get();
        
        // Obtener TODAS las inscripciones del estudiante
        List<InscripcionMateria> inscripciones = inscripcionMateriaRepository.findByEstudiante(estudiante);
        System.out.println("📋 Total inscripciones encontradas: " + inscripciones.size());
        
        if (inscripciones.isEmpty()) {
            System.out.println("⚠️ El estudiante no tiene inscripciones");
            return new ArrayList<>();
        }
        
        List<ProgresoMateriaDTO> resultado = new ArrayList<>();
        
        for (InscripcionMateria inscripcion : inscripciones) {
            Materia materia = inscripcion.getMateria();
            System.out.println("📚 Procesando materia: " + materia.getNombreMateria());
            
            // Obtener los cupos (tratamientos requeridos) para esta materia
            List<Cupo> cuposList = cupoRepository.findByMateria(materia);
            System.out.println("  Cupos encontrados: " + cuposList.size());
            
            List<ProgresoTratamientoDTO> tratamientosProgreso = new ArrayList<>();
            
            if (cuposList.isEmpty()) {
                System.out.println("  ⚠️ La materia no tiene tratamientos configurados");
                tratamientosProgreso.add(new ProgresoTratamientoDTO(
                    0L,
                    "Sin tratamientos asignados",
                    0,
                    0
                ));
            } else {
                for (Cupo cupo : cuposList) {
                    Tratamiento tratamiento = cupo.getTratamiento();
                    Integer requerido = cupo.getCuposDisponibles();
                    
                    // ✅ Buscar planes por ID de tratamiento
                    List<DiagnosticoTratamiento> planes = diagnosticoTratamientoRepository
                        .findByTratamiento_IdTratamiento(tratamiento.getIdTratamiento());
                    
                    int realizado = 0;
                    
                    for (DiagnosticoTratamiento plan : planes) {
                        // Contar dientes APROBADOS de este plan
                        List<DiagnosticoTratamientoDiente> dientes = diagnosticoTratamientoDienteRepository
                            .findByDiagnosticoTratamientoAndEstado(plan, "APROBADO");
                        realizado += dientes.size();
                    }
                    
                    // ✅ Crear DTO con todos los campos incluyendo faltante
                    ProgresoTratamientoDTO dto = new ProgresoTratamientoDTO(
                        tratamiento.getIdTratamiento(),
                        tratamiento.getNombreTratamiento(),
                        requerido != null ? requerido : 0,
                        realizado
                    );
                    // El faltante se calcula automáticamente en el constructor
                    
                    System.out.println("    Tratamiento: " + tratamiento.getNombreTratamiento() + 
                                     ", Requerido: " + dto.getRequerido() + 
                                     ", Realizado: " + dto.getRealizado() +
                                     ", Faltante: " + dto.getFaltante());
                    
                    tratamientosProgreso.add(dto);
                }
            }
            
            ProgresoMateriaDTO materiaDTO = new ProgresoMateriaDTO();
            materiaDTO.setIdMateria(materia.getId_materia());
            materiaDTO.setNombreMateria(materia.getNombreMateria());
            materiaDTO.setTratamientos(tratamientosProgreso);
            
            resultado.add(materiaDTO);
        }
        
        System.out.println("✅ Total materias a mostrar: " + resultado.size());
        return resultado;
    }
}