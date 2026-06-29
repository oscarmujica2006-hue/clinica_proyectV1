package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Radiografia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RadiografiaRepository extends JpaRepository<Radiografia, Long> {
    
    // Buscar todas las radiografías activas
    List<Radiografia> findAll();
    
    // Buscar por nombre
    List<Radiografia> findByNombreRayoContainingIgnoreCase(String nombre);
    
    // Buscar por ID
    Optional<Radiografia> findById(Long id);
    
    // Contar total
    long count();
}