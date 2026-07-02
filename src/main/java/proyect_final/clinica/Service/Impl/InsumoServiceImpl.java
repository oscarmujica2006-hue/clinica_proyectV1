package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Insumo;
import proyect_final.clinica.Model.Dao.InsumoRepository;
import proyect_final.clinica.Model.Dto.InsumoConsumidoDTO;
import proyect_final.clinica.Model.Dto.StockBajoDTO;
import proyect_final.clinica.Service.InsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InsumoServiceImpl implements InsumoService {
    
    @Autowired
    private InsumoRepository insumoRepository;
    
    @Override
    public List<Insumo> obtenerTodos() {
        return insumoRepository.findAll();
    }
    
    @Override
    public Optional<Insumo> obtenerPorId(Long id) {
        return insumoRepository.findById(id);
    }
    
    @Override
    public Insumo guardar(Insumo insumo) {
        return insumoRepository.save(insumo);
    }
    
    @Override
    public void eliminar(Long id) {
        insumoRepository.deleteById(id);
    }

    @Override
    public List<InsumoConsumidoDTO> obtenerInsumosMasConsumidos(LocalDate fechaInicio, LocalDate fechaFin) {
        // Formatear fechas para PostgreSQL (YYYY-MM-DD)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fechaInicioStr = fechaInicio.format(formatter);
        String fechaFinStr = fechaFin.format(formatter);
        
        System.out.println("🔍 Buscando insumos entre: " + fechaInicioStr + " y " + fechaFinStr);
        
        List<Object[]> resultados = insumoRepository.findInsumosMasConsumidosPorFechas(fechaInicioStr, fechaFinStr);
        
        System.out.println("📦 Resultados encontrados: " + (resultados != null ? resultados.size() : 0));
        
        // Imprimir los primeros resultados para depuración
        if (resultados != null && !resultados.isEmpty()) {
            System.out.println("📋 Primeros resultados:");
            for (int i = 0; i < Math.min(5, resultados.size()); i++) {
                Object[] row = resultados.get(i);
                System.out.println("  Fila " + i + ":");
                System.out.println("    Clinica: " + row[0]);
                System.out.println("    Turno: " + row[1]);
                System.out.println("    Insumo: " + row[2]);
                System.out.println("    Total: " + row[3]);
                System.out.println("---");
            }
        } else {
            System.out.println("⚠️ No hay resultados para mapear");
        }
        
        return mapearResultados(resultados);
    }

    private List<InsumoConsumidoDTO> mapearResultados(List<Object[]> resultados) {
        List<InsumoConsumidoDTO> dtoList = new ArrayList<>();
        
        if (resultados == null || resultados.isEmpty()) {
            return dtoList;
        }
        
        for (Object[] row : resultados) {
            try {
                InsumoConsumidoDTO dto = new InsumoConsumidoDTO();
                
                // Orden de columnas: [clinica, turno, insumo, total_consumido]
                dto.setClinica(row[0] != null ? row[0].toString() : "SIN CLÍNICA");
                dto.setTurno(row[1] != null ? row[1].toString() : "SIN TURNO");
                dto.setInsumo(row[2] != null ? row[2].toString() : "SIN INSUMO");
                
                // Manejar diferentes tipos numéricos
                if (row[3] != null) {
                    if (row[3] instanceof Number) {
                        dto.setTotalConsumido(((Number) row[3]).longValue());
                    } else {
                        dto.setTotalConsumido(Long.parseLong(row[3].toString()));
                    }
                } else {
                    dto.setTotalConsumido(0L);
                }
                
                dtoList.add(dto);
                
            } catch (Exception e) {
                System.err.println("❌ Error al mapear fila: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return dtoList;
    }


    @Override
    public List<StockBajoDTO> obtenerStockBajo() {
        List<Object[]> resultados = insumoRepository.findStockBajo();
        return mapearStockBajo(resultados);
    }






    // ===== ⭐ NUEVO MÉTODO PARA MAPEAR STOCK BAJO =====
    private List<StockBajoDTO> mapearStockBajo(List<Object[]> resultados) {
        List<StockBajoDTO> dtoList = new ArrayList<>();
        
        if (resultados == null || resultados.isEmpty()) {
            return dtoList;
        }
        
        for (Object[] row : resultados) {
            try {
                StockBajoDTO dto = new StockBajoDTO();

                
                dto.setIdInsumo(row[0] != null ? ((Number) row[0]).longValue() : 0L);
                dto.setNombreInsumo(row[1] != null ? row[1].toString() : "N/A");
                dto.setStockTotal(row[2] != null ? ((Number) row[2]).intValue() : 0);
                dto.setStockMinimo(row[3] != null ? ((Number) row[3]).intValue() : 0);
                dto.setFaltante(row[4] != null ? ((Number) row[4]).intValue() : 0);
                dto.setEstado(row[5] != null ? row[5].toString() : "BAJO");
                
                dtoList.add(dto);
                
            } catch (Exception e) {
                System.err.println("❌ Error al mapear stock bajo: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return dtoList;
    }





}