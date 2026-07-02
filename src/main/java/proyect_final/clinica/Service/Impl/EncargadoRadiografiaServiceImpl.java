package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.EncargadoRadiografiaRepository;
import proyect_final.clinica.Model.Entity.EncargadoRadiografia;
import proyect_final.clinica.Service.EncargadoRadiografiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EncargadoRadiografiaServiceImpl implements EncargadoRadiografiaService {
    
    @Autowired
    private EncargadoRadiografiaRepository encargadoRadiografiaRepository;
    
    @Override
    public EncargadoRadiografia guardar(EncargadoRadiografia encargado) {
        return encargadoRadiografiaRepository.save(encargado);
    }
    
    @Override
    public List<EncargadoRadiografia> obtenerTodos() {
        return encargadoRadiografiaRepository.findAll();
    }
    
    @Override
    public boolean existePorCodigo(Integer codigo) {
        return encargadoRadiografiaRepository.findByCodigoEncarRadiografia(codigo).isPresent();
    }
}