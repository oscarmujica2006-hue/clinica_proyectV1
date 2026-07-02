package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Archivo;
import java.util.List;
import java.util.Optional;

public interface ArchivoService {
    List<Archivo> getAllArchivos();
    Optional<Archivo> getArchivoById(Long id);
    Archivo saveArchivo(Archivo archivo);
    void deleteArchivo(Long id);

    
    // ✅ Verificar si paciente tiene archivo
    boolean pacienteTieneArchivo(Long idPaciente);

    // ⭐ NUEVO MÉTODO: Buscar usando la función de PostgreSQL
    Optional<Archivo> buscarArchivoPorCiONombre(Integer ci, String nombre);
}