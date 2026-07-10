package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.ConsentimientoFoto;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface ConsentimientoFotoService {
    
    List<ConsentimientoFoto> obtenerFotosPorConsentimiento(Long idConsentimiento);
    
    ConsentimientoFoto guardarFoto(Long idConsentimiento, MultipartFile archivo, Integer orden) throws IOException;
    
    List<ConsentimientoFoto> guardarMultiplesFotos(
        Long idConsentimiento, 
        MultipartFile[] archivos,
        ImageConversionService conversionService
    ) throws IOException;
}