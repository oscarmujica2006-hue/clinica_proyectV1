package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    
    List<Persona> findByNombreContainingIgnoreCase(String nombre);
    List<Persona> findByApellidoPaternoContainingIgnoreCase(String apellidoPaterno);
    List<Persona> findByApellidoMaternoContainingIgnoreCase(String apellidoMaterno);
    List<Persona> findByEdad(Integer edad);
    List<Persona> findBySexo(Character sexo);
    List<Persona> findByNombreAndApellidoPaterno(String nombre, String apellidoPaterno);
}