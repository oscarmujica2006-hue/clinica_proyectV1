package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Periodo;
import proyect_final.clinica.Model.Dao.PeriodoRepository;
import proyect_final.clinica.Service.PeriodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeriodoServiceImpl implements PeriodoService {

    @Autowired
    private PeriodoRepository periodoRepository;

    @Override
    public List<Periodo> listarTodos() {
        return periodoRepository.findAll();
    }

    @Override
    public Periodo obtenerPorId(Long id) {
        return periodoRepository.findById(id).orElse(null);
    }

    @Override
    public Periodo guardar(Periodo periodo) {
        return periodoRepository.save(periodo);
    }

    @Override
    public List<Periodo> listarActivosPorFecha() {
        return periodoRepository.findActivosByFecha();
    }
}