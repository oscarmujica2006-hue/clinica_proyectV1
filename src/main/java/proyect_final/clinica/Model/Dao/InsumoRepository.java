package proyect_final.clinica.Model.Dao;
import proyect_final.clinica.Model.Entity.Insumo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {

     @Query(value = "SELECT * FROM fn_insumo_mas_consumido_por_clinica(CAST(:fechaInicio AS DATE), CAST(:fechaFin AS DATE))", nativeQuery = true)
    List<Object[]> findInsumosMasConsumidosPorFechas(
        @Param("fechaInicio") String fechaInicio,
        @Param("fechaFin") String fechaFin
    );

    @Query(value = "SELECT * FROM fn_stock_bajo()", nativeQuery = true)
    List<Object[]> findStockBajo();




}
