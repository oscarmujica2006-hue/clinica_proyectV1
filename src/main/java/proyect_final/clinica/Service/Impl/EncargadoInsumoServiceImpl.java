package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.EncargadoInsumoRepository;
import proyect_final.clinica.Model.Entity.EncargadoInsumo;
import proyect_final.clinica.Model.Entity.Usuario;
import proyect_final.clinica.Service.EncargadoInsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EncargadoInsumoServiceImpl implements EncargadoInsumoService {

    @Autowired
    private EncargadoInsumoRepository encargadoInsumoRepository;

    @Override
    public List<EncargadoInsumo> findAll() {
        return encargadoInsumoRepository.findAll();
    }

    @Override
    public Optional<EncargadoInsumo> findById(Long id) {
        return encargadoInsumoRepository.findById(id);
    }

    @Override
    public EncargadoInsumo save(EncargadoInsumo encargadoInsumo) {
        return encargadoInsumoRepository.save(encargadoInsumo);
    }

    @Override
    public void deleteById(Long id) {
        encargadoInsumoRepository.deleteById(id);
    }

    @Override
    public Optional<EncargadoInsumo> findByUsuario(Usuario usuario) {
        return encargadoInsumoRepository.findByUsuario(usuario);
    }

    @Override
    public Optional<EncargadoInsumo> findByUsuarioId(Long idUsuario) {
        return encargadoInsumoRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Override
    public boolean existsById(Long id) {
        return encargadoInsumoRepository.existsById(id);
    }

    @Override
    public long count() {
        return encargadoInsumoRepository.count();
    }
}