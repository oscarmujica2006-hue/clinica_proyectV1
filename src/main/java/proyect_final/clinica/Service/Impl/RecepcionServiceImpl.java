package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.RecepcionRepository;
import proyect_final.clinica.Model.Entity.Recepcion;
import proyect_final.clinica.Service.RecepcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RecepcionServiceImpl implements RecepcionService {
    
    @Autowired
    private RecepcionRepository recepcionRepository;
    
    @Override
    public Recepcion guardar(Recepcion recepcion) {
        return recepcionRepository.save(recepcion);
    }
    
    @Override
    public List<Recepcion> obtenerTodos() {
        return recepcionRepository.findAll();
    }
    
    @Override
    public boolean existePorCodigo(Integer codigo) {
        return recepcionRepository.findByCodigoRecepcion(codigo).isPresent();
    }
}