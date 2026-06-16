package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.OdontogramaFoto;
import proyect_final.clinica.Model.Dao.OdontogramaFotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import proyect_final.clinica.Model.Dto.OdontogramaFotoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/odontograma-fotos")
@CrossOrigin(origins = "*")
public class OdontogramaFotoController {

    @Autowired
    private OdontogramaFotoRepository odontogramaFotoRepository;

    @GetMapping("/consulta/{idConsulta}")
    public ResponseEntity<List<OdontogramaFotoDTO>> getFotosByConsulta(@PathVariable Long idConsulta) {
        List<OdontogramaFoto> fotos = odontogramaFotoRepository.findByConsultaIdConsulta(idConsulta);
        
        // Convertir a DTO para evitar referencias circulares
        List<OdontogramaFotoDTO> fotosDTO = fotos.stream()
            .map(foto -> {
                OdontogramaFotoDTO dto = new OdontogramaFotoDTO();
                dto.setId(foto.getId_odontogramaFoto());
                dto.setRutaArchivo(foto.getRutaArchivo()); // Asegúrate que este campo existe
                dto.setNombreOriginal(foto.getNombreOriginal());
                dto.setConsultaId(foto.getConsulta().getIdConsulta());
                return dto;
            })
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(fotosDTO);
    }
}