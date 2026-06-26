package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Tratamiento;
import java.util.List;
import java.util.Optional;
public interface TratamientoService {

        List<Tratamiento> obtenerTodos() ;
        
        Optional<Tratamiento> obtenerPorId(Long id);
        
        Tratamiento guardar(Tratamiento tratamiento);
        
        void eliminar(Long id);

}
