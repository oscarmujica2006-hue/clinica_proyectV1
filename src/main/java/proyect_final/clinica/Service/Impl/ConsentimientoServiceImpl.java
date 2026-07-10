package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Consentimiento;
import proyect_final.clinica.Model.Entity.ConsentimientoFoto;
import proyect_final.clinica.Model.Dao.ConsentimientoRepository;
import proyect_final.clinica.Model.Dao.ConsentimientoFotoRepository;
import proyect_final.clinica.Service.ConsentimientoService;
import proyect_final.clinica.Service.ConsentimientoFotoService;
import proyect_final.clinica.Service.ImageConversionService; // ✅ Importar

import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;
import java.io.IOException; 
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ConsentimientoServiceImpl implements ConsentimientoService {

    @Autowired
    private ConsentimientoRepository consentimientoRepository;
    
    @Autowired
    private ConsentimientoFotoRepository consentimientoFotoRepository;
    
    @Autowired
    private ConsentimientoFotoService consentimientoFotoService;
    
    @Autowired
    private ImageConversionService imageConversionService; // ✅ Inyectar

    // ================================================================
    // ============ MÉTODOS EXISTENTES ============
    // ================================================================
    
    @Override
    public List<Consentimiento> obtenerTodos() {
        return consentimientoRepository.findAll();
    }

    @Override
    public Optional<Consentimiento> obtenerPorId(Long id) {
        return consentimientoRepository.findById(id);
    }

    @Override
    public Consentimiento guardar(Consentimiento consentimiento) {
        if (consentimiento.getEstado() == null) {
            consentimiento.setEstado("PENDIENTE");
        }
        if (consentimiento.getIdConsentimiento() == null && consentimiento.getFecha() == null) {
            consentimiento.setFecha(LocalDateTime.now());
        }
        if (consentimiento.getFechRegCon() == null) {
            consentimiento.setFechRegCon(LocalDateTime.now());
        }
        consentimiento.setFechModCon(LocalDateTime.now());
        
        return consentimientoRepository.save(consentimiento);
    }

    @Override
    public void eliminar(Long id) {
        Optional<Consentimiento> consentimiento = consentimientoRepository.findById(id);
        if (consentimiento.isPresent()) {
            Consentimiento cons = consentimiento.get();
            cons.setEstado("ANULADO");
            cons.setFechModCon(LocalDateTime.now());
            consentimientoRepository.save(cons);
        }
    }

    @Override
    @Transactional
    public Consentimiento guardarConFotos(Consentimiento consentimiento, MultipartFile[] fotos) throws IOException {
        Consentimiento saved = guardar(consentimiento);
        
        if (fotos != null && fotos.length > 0) {
            // ✅ CORREGIDO: pasar los 3 argumentos
            consentimientoFotoService.guardarMultiplesFotos(
                saved.getIdConsentimiento(), 
                fotos,
                imageConversionService  // ✅ Pasar el servicio de conversión
            );
        }
        
        return saved;
    }

    // ================================================================
    // ============ NUEVOS MÉTODOS ============
    // ================================================================
    
    @Override
    public List<Consentimiento> findByDocenteId(Long idDocente) {
        return consentimientoRepository.findByDocente_IdDocente(idDocente);
    }

    @Override
    public Optional<Consentimiento> findByDiagnosticoTratamientoId(Long idDiagTrat) {
        return consentimientoRepository.findByDiagnosticoTratamiento_IdDiagTrat(idDiagTrat);
    }

    @Override
    public List<ConsentimientoFoto> findFotosByConsentimientoId(Long idConsentimiento) {
        return consentimientoFotoRepository.findByConsentimiento_IdConsentimiento(idConsentimiento);
    }

    @Override
    @Transactional
    public Consentimiento cambiarEstado(Long idConsentimiento, String estado) {
        Optional<Consentimiento> optional = consentimientoRepository.findById(idConsentimiento);
        if (optional.isEmpty()) {
            throw new RuntimeException("Consentimiento no encontrado con ID: " + idConsentimiento);
        }
        
        Consentimiento consentimiento = optional.get();
        
        if (!estado.equals("APROBADO") && !estado.equals("RECHAZADO") && !estado.equals("PENDIENTE")) {
            throw new IllegalArgumentException("Estado inválido: " + estado);
        }
        
        if (consentimiento.getEstado().equals("APROBADO") || consentimiento.getEstado().equals("RECHAZADO")) {
            if (estado.equals("PENDIENTE")) {
                throw new IllegalStateException("No se puede cambiar un consentimiento " + 
                    consentimiento.getEstado().toLowerCase() + " a pendiente");
            }
        }
        
        consentimiento.setEstado(estado);
        consentimiento.setFechModCon(LocalDateTime.now());
        
        return consentimientoRepository.save(consentimiento);
    }

    @Override
    public List<Consentimiento> findByEstudianteId(Long idEstudiante) {
        return consentimientoRepository.findByEstudiante_IdEstudiante(idEstudiante);
    }

    @Override
    public List<Consentimiento> findByEstado(String estado) {
        return consentimientoRepository.findByEstado(estado);
    }

    @Override
    public List<Consentimiento> findByDocenteIdAndEstado(Long idDocente, String estado) {
        return consentimientoRepository.findByDocente_IdDocenteAndEstado(idDocente, estado);
    }
}