package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    
    // ========== CRUD BÁSICO ==========
    List<Usuario> obtenerTodos();
    Optional<Usuario> obtenerPorId(Long id);
    Usuario guardar(Usuario usuario);
    Usuario actualizar(Long id, Usuario usuarioActualizado);
    Optional<Usuario> obtenerPorEmail(String email);
    void actualizarPassword(Long idUsuario, String nuevaPassword);
    void eliminar(Long id);
    
    // ========== MÉTODOS QUE TE FALTAN ==========
    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);
    Boolean existePorNombreUsuario(String nombreUsuario);
    List<Usuario> buscarPorEstado(Boolean estado);
    List<Usuario> buscarPorPersonaId(Long idPersona);  
    Optional<Usuario> findByIdUsuario(Long id);
}