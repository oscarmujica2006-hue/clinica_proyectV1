package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Docente;
import proyect_final.clinica.Model.Entity.Usuario;
import proyect_final.clinica.Model.Entity.Persona;
import proyect_final.clinica.Model.Dao.DocenteRepository;
import proyect_final.clinica.Model.Dao.UsuarioRepository;
import proyect_final.clinica.Model.Dao.ClinicaRepository;
import proyect_final.clinica.Model.Dto.DocenteDTO;
import proyect_final.clinica.Service.DocenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocenteServiceImpl implements DocenteService {

    @Autowired
    private DocenteRepository docenteRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    

    
    @Autowired
    private ClinicaRepository clinicaRepository;

    @Override
    public List<Docente> obtenerTodos() {
        return docenteRepository.findAll();
    }

    @Override
    public List<Docente> obtenerTodosActivos() {
        return docenteRepository.findByEstadoTrue();
    }

    @Override
    public Optional<Docente> obtenerPorId(Long id) {
        return docenteRepository.findById(id);
    }

    @Override
    @Transactional
    public Docente guardar(Docente docente) {
        return docenteRepository.save(docente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Optional<Docente> docente = docenteRepository.findById(id);
        if (docente.isPresent()) {
            Docente doc = docente.get();
            doc.setEstado(false); // Soft delete
            docenteRepository.save(doc);
        }
    }

    @Override
    public boolean existePorCodigoDocente(Integer codigoDocente) {
        return docenteRepository.existsByCodigoDocente(codigoDocente);
    }

    @Override
    public Optional<Docente> obtenerPorCodigoDocente(Integer codigoDocente) {
        return docenteRepository.findByCodigoDocente(codigoDocente);
    }

    @Override
    public Optional<Docente> obtenerPorIdUsuario(Long idUsuario) {
        return docenteRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Override
    public List<Docente> obtenerPorClinica(Long idClinica) {
        return docenteRepository.findByClinicaIdClinica(idClinica);
    }

    @Override
    public List<Docente> obtenerActivosPorClinica(Long idClinica) {
        return docenteRepository.findByClinicaIdClinicaAndEstadoTrue(idClinica);
    }

    @Override
    public List<Docente> buscarPorEspecialidad(String especialidad) {
        return docenteRepository.findByEspecialidadContainingIgnoreCase(especialidad);
    }

    @Override
    public List<Docente> buscarPorNombre(String nombre) {
        return docenteRepository.buscarPorNombreCompleto(nombre);
    }

    @Override
    public List<Docente> buscarPorNombreUsuario(String username) {
        return docenteRepository.buscarPorNombreUsuario(username);
    }

    @Override
    public List<DocenteDTO> obtenerTodosDTO() {
        return docenteRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocenteDTO> obtenerActivosDTO() {
        return docenteRepository.findByEstadoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DocenteDTO> obtenerDTOPorId(Long id) {
        return docenteRepository.findById(id)
                .map(this::convertirADTO);
    }

    @Override
    public DocenteDTO convertirADTO(Docente docente) {
        if (docente == null) return null;
        
        DocenteDTO dto = new DocenteDTO();
        
        // Datos básicos de Docente
        dto.setIdDocente(docente.getIdDocente());
        dto.setEspecialidad(docente.getEspecialidad());
        dto.setCodigoDocente(docente.getCodigoDocente());
        dto.setContrato(docente.getContrato());
        dto.setEstado(docente.getEstado());
        
        // Datos de Usuario
        if (docente.getUsuario() != null) {
            Usuario usuario = docente.getUsuario();
            dto.setIdUsuario(usuario.getIdUsuario());
            dto.setNombreUsuario(usuario.getNombreUsuario());
            
            // Datos de Persona
            if (usuario.getPersona() != null) {
                Persona persona = usuario.getPersona();
                dto.setIdPersona(persona.getId_persona());
                dto.setNombre(persona.getNombre());
                dto.setApellidoPaterno(persona.getApellidoPaterno());
                dto.setApellidoMaterno(persona.getApellidoMaterno());
                dto.setEdad(persona.getEdad());
                dto.setSexo(persona.getSexo());
                
                // Construir nombre completo
                String nombreCompleto = persona.getNombre() + " " + 
                                      persona.getApellidoPaterno() + " " + 
                                      persona.getApellidoMaterno();
                dto.setNombreCompleto(nombreCompleto.trim());
            }
        }
        
        // Datos de Clínica
        if (docente.getClinica() != null) {
            dto.setIdClinica(docente.getClinica().getIdClinica());
            dto.setNombreClinica(docente.getClinica().getNombreClinica());
        }
        
        return dto;
    }

    @Override
    @Transactional
    public Docente convertirAEntidad(DocenteDTO dto) {
        if (dto == null) return null;
        
        Docente docente = new Docente();
        
        // Si tiene ID, buscar existente
        if (dto.getIdDocente() != null) {
            Optional<Docente> existente = docenteRepository.findById(dto.getIdDocente());
            if (existente.isPresent()) {
                docente = existente.get();
            }
        }
        
        // Actualizar campos
        docente.setEspecialidad(dto.getEspecialidad());
        docente.setCodigoDocente(dto.getCodigoDocente());
        docente.setContrato(dto.getContrato());
        docente.setEstado(dto.getEstado());
        
        // Asignar Usuario (si es nuevo o existe)
        if (dto.getIdUsuario() != null) {
            usuarioRepository.findById(dto.getIdUsuario()).ifPresent(docente::setUsuario);
        }
        
        // Asignar Clínica
        if (dto.getIdClinica() != null) {
            clinicaRepository.findById(dto.getIdClinica()).ifPresent(docente::setClinica);
        }
        
        return docente;
    }

    @Override
    public String getNombreCompletoDocente(Docente docente) {
        if (docente != null && 
            docente.getUsuario() != null && 
            docente.getUsuario().getPersona() != null) {
            Persona persona = docente.getUsuario().getPersona();
            return (persona.getNombre() + " " + 
                   persona.getApellidoPaterno() + " " + 
                   persona.getApellidoMaterno()).trim();
        }
        return "Docente sin nombre";
    }

    @Override
    public boolean existeRelacionConUsuario(Long idUsuario) {
        return docenteRepository.findByUsuarioIdUsuario(idUsuario).isPresent();
    }
}