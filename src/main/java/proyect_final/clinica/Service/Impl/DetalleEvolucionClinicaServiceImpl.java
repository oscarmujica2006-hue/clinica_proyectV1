package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.DetalleEvolucionClinica;
import proyect_final.clinica.Model.Dao.DetalleEvolucionClinicaRepository;
import proyect_final.clinica.Service.DetalleEvolucionClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DetalleEvolucionClinicaServiceImpl implements DetalleEvolucionClinicaService {

    @Autowired
    private DetalleEvolucionClinicaRepository detalleEvolucionClinicaRepository;

    @Override
    public List<DetalleEvolucionClinica> findAll() {
        return detalleEvolucionClinicaRepository.findAll();
    }

    @Override
    public Optional<DetalleEvolucionClinica> obtenerPorId(Long id) {
        return detalleEvolucionClinicaRepository.findById(id);
    }

    @Override
    public DetalleEvolucionClinica guardar(DetalleEvolucionClinica detalle) {
        return detalleEvolucionClinicaRepository.save(detalle);
    }

    @Override
    public List<DetalleEvolucionClinica> guardarTodos(List<DetalleEvolucionClinica> detalles) {
        return detalleEvolucionClinicaRepository.saveAll(detalles);
    }

    @Override
    public void eliminar(Long id) {
        detalleEvolucionClinicaRepository.deleteById(id);
    }

    @Override
    public List<DetalleEvolucionClinica> findByEvolucionClinicaId(Long idEvolucion) {
        return detalleEvolucionClinicaRepository.findByEvolucionClinica_IdEvolucionClinica(idEvolucion);
    }

    @Override
    public List<DetalleEvolucionClinica> findByDientePlanId(Long idDientePlan) {
        return detalleEvolucionClinicaRepository
            .findByDiagnosticoTratamientoDiente_IdDiagnosticoTratamientoDiente(idDientePlan);
    }

    @Override
    public List<DetalleEvolucionClinica> findByEvolucionClinicaIdAndEstado(Long idEvolucion, String estDetEvoClin) {
        return detalleEvolucionClinicaRepository
            .findByEvolucionClinica_IdEvolucionClinicaAndEstDetEvoClin(idEvolucion, estDetEvoClin);
    }

    @Override
    public long countByEvolucionClinicaId(Long idEvolucion) {
        return detalleEvolucionClinicaRepository.countByEvolucionClinica_IdEvolucionClinica(idEvolucion);
    }
}