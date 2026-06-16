package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Usuario;
import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Model.Entity.Docente;
import proyect_final.clinica.Model.Entity.EncargadoInsumo;
import proyect_final.clinica.Model.Entity.Director;
import proyect_final.clinica.Model.Dao.UsuarioRepository;
import proyect_final.clinica.Model.Dao.EstudianteRepository;
import proyect_final.clinica.Model.Dao.DocenteRepository;
import proyect_final.clinica.Model.Dao.DirectorRepository;
import proyect_final.clinica.Model.Dao.EncargadoInsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private EncargadoInsumoRepository encargadoInsumoRepository; 

    @Autowired
    private DirectorRepository directorRepository;

    public Optional<AuthResult> autenticar(String username, String password) {
        // Buscar usuario por nombre de usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verificar contraseña y estado activo
            if (usuario.getContraseña().equals(password) && usuario.getEstado()) {
                
                // Verificar si es estudiante
                Optional<Estudiante> estudianteOpt = estudianteRepository.findByUsuario(usuario);
                if (estudianteOpt.isPresent()) {
                    return Optional.of(new AuthResult(usuario, estudianteOpt.get(), "ESTUDIANTE"));
                }
                
                // Verificar si es docente
                Optional<Docente> docenteOpt = docenteRepository.findByUsuario(usuario);
                if (docenteOpt.isPresent()) {
                    return Optional.of(new AuthResult(usuario, docenteOpt.get(), "DOCENTE"));
                }
                
                // ✅ Verificar si es encargado de insumos
                Optional<EncargadoInsumo> encargadoOpt = encargadoInsumoRepository.findByUsuario(usuario);
                if (encargadoOpt.isPresent()) {
                    return Optional.of(new AuthResult(usuario, encargadoOpt.get(), "ENCARGADO_INSUMO"));
                }

                // ✅ Verificar si es director
                Optional<Director> directorOpt = directorRepository.findByUsuario(usuario);
                if (directorOpt.isPresent()) {
                    return Optional.of(new AuthResult(usuario, directorOpt.get(), "DIRECTOR"));
                }
                
                // Si es solo usuario (admin u otro tipo)
                return Optional.of(new AuthResult(usuario, null, "ADMIN"));
            }
        }
        
        return Optional.empty();
    }

    // Clase interna para el resultado
    public static class AuthResult {
        private Usuario usuario;
        private Object perfil; // Estudiante, Docente, EncargadoInsumo, etc.
        private String tipo;

        public AuthResult(Usuario usuario, Object perfil, String tipo) {
            this.usuario = usuario;
            this.perfil = perfil;
            this.tipo = tipo;
        }

        public Usuario getUsuario() { return usuario; }
        public Object getPerfil() { return perfil; }
        public String getTipo() { return tipo; }
        
        public Estudiante getEstudiante() {
            return perfil instanceof Estudiante ? (Estudiante) perfil : null;
        }
        
        public Docente getDocente() {
            return perfil instanceof Docente ? (Docente) perfil : null;
        }
        
        
        public EncargadoInsumo getEncargadoInsumo() {
            return perfil instanceof EncargadoInsumo ? (EncargadoInsumo) perfil : null;
        }

     
        public Director getDirector() {
            return perfil instanceof Director ? (Director) perfil : null;
        }
    }
}