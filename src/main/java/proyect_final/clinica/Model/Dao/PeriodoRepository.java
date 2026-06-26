package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Periodo;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, Long> {
    @Query("SELECT p FROM Periodo p WHERE p.fechaInicio <= CURRENT_DATE AND p.fechaFin >= CURRENT_DATE")
    List<Periodo> findActivosByFecha();
}
