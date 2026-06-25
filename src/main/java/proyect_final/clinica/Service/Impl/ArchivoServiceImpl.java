package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Archivo;
import proyect_final.clinica.Model.Dao.ArchivoRepository;
import proyect_final.clinica.Service.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArchivoServiceImpl implements ArchivoService {

    @Autowired
    private ArchivoRepository archivoRepository;

    @Override
    public List<Archivo> getAllArchivos() {
        return archivoRepository.findAll();
    }

    @Override
    public Optional<Archivo> getArchivoById(Long id) {
        return archivoRepository.findById(id);
    }

    @Override
    public Archivo saveArchivo(Archivo archivo) {
        return archivoRepository.save(archivo);
    }

    @Override
    public void deleteArchivo(Long id) {
        archivoRepository.deleteById(id);
    }

    @Override
    public Optional<Archivo> findArchivoByPacienteCiOrNombre(Integer ci, String nombre) {
        if ((ci == null || ci == 0) && (nombre == null || nombre.trim().isEmpty())) {
            throw new IllegalArgumentException("Debe proporcionar CI o nombre del paciente");
        }
        
        String nombreBusqueda = nombre != null ? nombre.trim() : null;
        return archivoRepository.buscarArchivoPorCiONombre(ci, nombreBusqueda);
    }
    
    @Override
    public Optional<Long> findIdArchivoByPacienteCiOrNombre(Integer ci, String nombre) {
        if ((ci == null || ci == 0) && (nombre == null || nombre.trim().isEmpty())) {
            throw new IllegalArgumentException("Debe proporcionar CI o nombre del paciente");
        }
        
        String nombreBusqueda = nombre != null ? nombre.trim() : null;
        return archivoRepository.findIdArchivoByPacienteCiOrNombre(ci, nombreBusqueda);
    }
    
    @Override
    public boolean pacienteTieneArchivo(Long idPaciente) {
        if (idPaciente == null) {
            return false;
        }
        return archivoRepository.existsByPacienteId(idPaciente);
    }
    
    @Override
    public Optional<Archivo> findArchivoByPacienteId(Long idPaciente) {
        return archivoRepository.findByPacienteIdPaciente(idPaciente)
                                .stream()
                                .findFirst();
    }

    // ⭐ NUEVO MÉTODO IMPLEMENTADO: Llama directamente a la función de PostgreSQL
    @Override
    public Optional<Archivo> buscarArchivoPorCiONombre(Integer ci, String nombre) {
        // Validar parámetros
        if ((ci == null || ci == 0) && (nombre == null || nombre.trim().isEmpty())) {
            throw new IllegalArgumentException("Debe proporcionar CI o nombre del paciente");
        }
        
        String nombreBusqueda = nombre != null ? nombre.trim() : null;
        
        // Llamar al repositorio que usa la función de PostgreSQL
        return archivoRepository.buscarArchivoPorCiONombre(ci, nombreBusqueda);
    }
}