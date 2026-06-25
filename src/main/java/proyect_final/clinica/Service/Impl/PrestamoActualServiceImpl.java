package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.*;
import proyect_final.clinica.Model.Dao.*;
import proyect_final.clinica.Service.PrestamoActualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;  // ← IMPORTAR LocalDate
import java.time.temporal.ChronoUnit;  // ← PARA CALCULAR DÍAS
import java.util.*;

@Service
public class PrestamoActualServiceImpl implements PrestamoActualService {

    @Autowired
    private PrestamoActualRepository prestamoRepository;
    
    @Autowired
    private EstudianteRepository estudianteRepository;
    
    @Autowired
    private ArchivoRepository archivoRepository;
    
    // ===== CRUD BÁSICO =====
    
    @Override
    public List<PrestamoActual> obtenerTodos() {
        return prestamoRepository.findAll();
    }
    
    @Override
    public Optional<PrestamoActual> obtenerPorId(Long id) {
        return prestamoRepository.findById(id);
    }
    
    @Override
    public PrestamoActual guardar(PrestamoActual prestamoActual) {
        return prestamoRepository.save(prestamoActual);
    }
    
    @Override
    public PrestamoActual actualizar(Long id, PrestamoActual prestamoActualActualizado) {
        if (prestamoRepository.existsById(id)) {
            prestamoActualActualizado.setId(id);
            return prestamoRepository.save(prestamoActualActualizado);
        }
        return null;
    }
    
    @Override
    public void eliminar(Long id) {
        prestamoRepository.deleteById(id);
    }
    
    @Override
    public List<PrestamoActual> buscarPorIdEstudiante(Long idEstudiante) {
        return prestamoRepository.findByIdEstudiante(idEstudiante);
    }
    
    @Override
    public List<PrestamoActual> buscarPorIdArchivo(Long idArchivo) {
        return prestamoRepository.findByIdArchivo(idArchivo);
    }
    
    // ===== MÉTODOS DE NEGOCIO =====
    
    @Override
    public Optional<PrestamoActual> obtenerPrestamoActivoPorEstudiante(Long idEstudiante) {
        List<PrestamoActual> prestamos = prestamoRepository
            .findByIdEstudianteAndEstadoPrestamo(idEstudiante, "ACTIVO");
        
        if (prestamos.isEmpty()) {
            return Optional.empty();
        }
        
        // Retornar el más reciente
        prestamos.sort((p1, p2) -> p2.getFechaPrestamo().compareTo(p1.getFechaPrestamo()));
        return Optional.of(prestamos.get(0));
    }
    
    @Override
    @Transactional
    public PrestamoActual registrarPrestamo(PrestamoActual prestamo) {
        // Verificar que el estudiante NO esté bloqueado
        Estudiante estudiante = estudianteRepository.findById(prestamo.getIdEstudiante())
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
            
        if (estudiante.getBloqueado()) {
            throw new RuntimeException("ESTUDIANTE BLOQUEADO - No puede realizar préstamos hasta que regularice su situación");
        }
        
        // Verificar que no tenga otro préstamo activo
        Optional<PrestamoActual> prestamoActivo = obtenerPrestamoActivoPorEstudiante(prestamo.getIdEstudiante());

        if (prestamoActivo.isPresent()) {
            throw new RuntimeException("El estudiante ya tiene un préstamo activo");
        }

        // Configurar el nuevo préstamo
        prestamo.setEstadoPrestamo("ACTIVO");
        
        return prestamoRepository.save(prestamo);
    }
    
    @Override
    @Transactional
    public PrestamoActual devolverArchivo(Long idPrestamo, LocalDate fechaDevolucion) {  // ← CAMBIADO A LocalDate
        PrestamoActual prestamo = prestamoRepository.findById(idPrestamo)
            .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        
        prestamo.setFechaDevolucion(fechaDevolucion);
        
        // Verificar si hay retraso
        if (fechaDevolucion.isAfter(prestamo.getFechaLimitePrestamo())) {  // ← USAR isAfter()
            // Calcular días de retraso con ChronoUnit
            long diasRetraso = ChronoUnit.DAYS.between(
                prestamo.getFechaLimitePrestamo(), 
                fechaDevolucion
            );
            
            prestamo.setEstadoPrestamo("DEVUELTO");
            prestamo.setDiasRetraso((int) diasRetraso);
            
            // BLOQUEAR AL ESTUDIANTE
            bloquearEstudiante(prestamo.getIdEstudiante());
        } else {
            prestamo.setEstadoPrestamo("DEVUELTO");
            prestamo.setDiasRetraso(0);
            
            // Verificar si el estudiante tiene otros préstamos vencidos
            List<PrestamoActual> otrosVencidos = prestamoRepository
                .findByIdEstudianteAndEstadoPrestamo(prestamo.getIdEstudiante(), "VENCIDO");
            
            if (otrosVencidos.isEmpty()) {
                desbloquearEstudiante(prestamo.getIdEstudiante());
            }
        }
        
        return prestamoRepository.save(prestamo);
    }
    
    private void bloquearEstudiante(Long idEstudiante) {
        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        estudiante.setBloqueado(true);
        estudianteRepository.save(estudiante);
    }
    
    @Override
    @Transactional
    public void desbloquearEstudiante(Long idEstudiante) {
        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        estudiante.setBloqueado(false);
        estudianteRepository.save(estudiante);
    }
    
    @Override
    public boolean puedeRealizarConsulta(Long idEstudiante, Long idPaciente) {

        System.out.println("========== VERIFICANDO PERMISO ==========");
        System.out.println("ID Estudiante: " + idEstudiante);
        System.out.println("ID Paciente a verificar: " + idPaciente);
        


        // 1. Verificar si está bloqueado
        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
            
        if (estudiante.getBloqueado()) {
            return false;
        }
        
        // 2. Buscar su préstamo activo
        Optional<PrestamoActual> prestamoOpt = obtenerPrestamoActivoPorEstudiante(idEstudiante);
        
        if (prestamoOpt.isEmpty()) {
            return false;
        }
        
        PrestamoActual prestamo = prestamoOpt.get();

        System.out.println("✅ Préstamo activo encontrado:");
        System.out.println("   - ID Préstamo: " + prestamo.getId());
        System.out.println("   - ID Archivo: " + prestamo.getIdArchivo());
        
        
        // 3. Verificar que el préstamo no esté vencido (usar LocalDate.now())
        if (prestamo.getFechaLimitePrestamo().isBefore(LocalDate.now())) {  // ← CAMBIADO
            prestamo.setEstadoPrestamo("VENCIDO");
            prestamoRepository.save(prestamo);
            bloquearEstudiante(idEstudiante);
            return false;
        }
        
        // 4. Verificar que el paciente sea el del archivo prestado
        Optional<Archivo> archivoOpt = archivoRepository.findById(prestamo.getIdArchivo());
        
        if (archivoOpt.isEmpty()) {
            return false;
        }
        
        Archivo archivo = archivoOpt.get();
        
        return archivo.getPaciente().getIdPaciente().equals(idPaciente);
    }
    
    @Override
    public Map<String, Object> obtenerInfoPacientePrestado(Long idEstudiante) {
        Optional<PrestamoActual> prestamoOpt = obtenerPrestamoActivoPorEstudiante(idEstudiante);
        
        Map<String, Object> respuesta = new HashMap<>();
        
        if (prestamoOpt.isEmpty()) {
            respuesta.put("tienePrestamo", false);
            return respuesta;
        }
        
        PrestamoActual prestamo = prestamoOpt.get();
        
        // Buscar el archivo
        Optional<Archivo> archivoOpt = archivoRepository.findById(prestamo.getIdArchivo());
        
        if (archivoOpt.isEmpty()) {
            respuesta.put("tienePrestamo", false);
            return respuesta;
        }
        
        Archivo archivo = archivoOpt.get();
        Paciente paciente = archivo.getPaciente();
        Persona persona = paciente.getPersona();
        
        String nombreCompleto = "";
        if (persona != null) {
            nombreCompleto = (persona.getNombre() != null ? persona.getNombre() : "") + " " +
                            (persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "") + " " +
                            (persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : "");
            nombreCompleto = nombreCompleto.trim().replaceAll("\\s+", " ");
        }
        
        respuesta.put("tienePrestamo", true);
        respuesta.put("idPaciente", paciente.getIdPaciente());
        respuesta.put("nombreCompleto", nombreCompleto);
        respuesta.put("ci", paciente.getCi());
        respuesta.put("fechaLimite", prestamo.getFechaLimitePrestamo());
        respuesta.put("fechaPrestamo", prestamo.getFechaPrestamo());
        respuesta.put("idPrestamoActual", prestamo.getId());
        
        return respuesta;
    }





    // Agrega este método en PrestamoActualServiceImpl
    @Override
    public List<PrestamoActual> buscarPorEstado(String estado) {
        return prestamoRepository.findByEstadoPrestamo(estado);
    }
// Agrega este método al final de PrestamoActualServiceImpl
@Override
public int contarPrestamosActivos(Long idEstudiante) {
    List<PrestamoActual> prestamos = prestamoRepository
        .findByIdEstudianteAndEstadoPrestamo(idEstudiante, "ACTIVO");
    return prestamos.size();
}
}