package proyect_final.clinica.Controller;

import proyect_final.clinica.Model.Entity.ConsentimientoFoto;
import proyect_final.clinica.Model.Dto.ConsentimientoFotoDTO;
import proyect_final.clinica.Service.ConsentimientoFotoService;
import proyect_final.clinica.Service.ImageConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/consentimiento-fotos")
@CrossOrigin(origins = "*")
public class ConsentimientoFotoController {

    @Autowired
    private ConsentimientoFotoService consentimientoFotoService;

    @Autowired
    private ImageConversionService imageConversionService;

    @GetMapping("/consentimiento/{idConsentimiento}")
    public ResponseEntity<?> getFotosByConsentimiento(@PathVariable Long idConsentimiento) {
        try {
            List<ConsentimientoFoto> fotos = consentimientoFotoService.obtenerFotosPorConsentimiento(idConsentimiento);
            
            List<ConsentimientoFotoDTO> fotosDTO = fotos.stream().map(f -> {
                ConsentimientoFotoDTO dto = new ConsentimientoFotoDTO();
                dto.setId(f.getId_consentimientoFoto());
                dto.setRutaArchivo(f.getRutaArchivoConsentimiento());
                dto.setNombreOriginal(f.getNombreConsentimientoOriginal());
                dto.setTipoContenido(f.getTipoContenidoConsentimiento());
                dto.setTamano(f.getTamanoConsentimiento());
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(fotosDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/consentimiento/{idConsentimiento}/subir")
    public ResponseEntity<?> subirFotos(
            @PathVariable Long idConsentimiento,
            @RequestParam("archivos") MultipartFile[] archivos) {
        try {
            List<ConsentimientoFoto> fotos = consentimientoFotoService.guardarMultiplesFotos(
                idConsentimiento, 
                archivos,
                imageConversionService // Pasamos el servicio de conversión
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Fotos subidas correctamente");
            response.put("total", fotos.size());
            
            // Mostrar información de conversión
            fotos.forEach(foto -> {
                if (foto.getTipoContenidoConsentimiento().equals("image/webp")) {
                    response.put("convertidas_a_webp", true);
                }
            });
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al guardar las imágenes: " + e.getMessage()));
        }
    }
}