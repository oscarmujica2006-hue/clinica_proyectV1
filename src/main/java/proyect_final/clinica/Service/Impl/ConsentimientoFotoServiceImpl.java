package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.Consentimiento;
import proyect_final.clinica.Model.Entity.ConsentimientoFoto;
import proyect_final.clinica.Model.Dao.ConsentimientoFotoRepository;
import proyect_final.clinica.Model.Dao.ConsentimientoRepository;
import proyect_final.clinica.Service.ConsentimientoFotoService;
import proyect_final.clinica.Service.ImageConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ConsentimientoFotoServiceImpl implements ConsentimientoFotoService {
    
    @Autowired
    private ConsentimientoFotoRepository consentimientoFotoRepository;
    
    @Autowired
    private ConsentimientoRepository consentimientoRepository;
    
    @Autowired
    private ImageConversionService imageConversionService; // Inyectar el servicio
    
    @Value("${upload.consentimiento.dir:uploads/consentimientos/}")
    private String uploadDir;
    
    @Override
    public List<ConsentimientoFoto> obtenerFotosPorConsentimiento(Long idConsentimiento) {
        return consentimientoFotoRepository.findByConsentimientoIdConsentimiento(idConsentimiento);
    }
     
    @Override
    @Transactional
    public ConsentimientoFoto guardarFoto(Long idConsentimiento, MultipartFile archivo, Integer orden) throws IOException {
        Consentimiento consentimiento = consentimientoRepository.findById(idConsentimiento)
            .orElseThrow(() -> new RuntimeException("Consentimiento no encontrado con ID: " + idConsentimiento));
        
        // Si es imagen, convertir a WebP
        byte[] datosImagen;
        String contentType = archivo.getContentType();
        String nombreOriginal = archivo.getOriginalFilename();
        Long tamanoFinal;
        
        if (contentType != null && contentType.startsWith("image/")) {
            try {
                // ✅ CONVERTIR A WEBP
                datosImagen = imageConversionService.convertToWebP(archivo, 0.8f);
                contentType = "image/webp";
                tamanoFinal = (long) datosImagen.length;
                
                // Mostrar reducción de tamaño
                double reduction = (1 - (double)tamanoFinal / archivo.getSize()) * 100;
                System.out.println("✅ WebP: " + nombreOriginal + " | " + 
                    archivo.getSize()/1024 + "KB → " + tamanoFinal/1024 + "KB" +
                    " (Reducción: " + String.format("%.1f", reduction) + "%)");
                    
            } catch (Exception e) {
                // Si falla, guardar original
                System.err.println("⚠️ Error convirtiendo a WebP: " + e.getMessage());
                datosImagen = archivo.getBytes();
                tamanoFinal = archivo.getSize();
            }
        } else {
            // PDF u otros: guardar sin conversión
            datosImagen = archivo.getBytes();
            tamanoFinal = archivo.getSize();
        }
        
        // Crear directorio
        Path directorio = Paths.get(uploadDir);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }
        
        // Generar nombre único
        String extension = "";
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }
        // Cambiar extensión a .webp si se convirtió
        if (contentType.equals("image/webp")) {
            extension = ".webp";
        }
        String nombreArchivo = UUID.randomUUID().toString() + extension;
        
        // Guardar archivo físicamente
        Path rutaCompleta = directorio.resolve(nombreArchivo);
        Files.write(rutaCompleta, datosImagen);
        
        // Crear entidad
        ConsentimientoFoto foto = new ConsentimientoFoto();
        foto.setConsentimiento(consentimiento);
        foto.setRutaArchivoConsentimiento("/uploads/consentimientos/" + nombreArchivo);
        foto.setNombreConsentimientoOriginal(nombreOriginal);
        foto.setTipoContenidoConsentimiento(contentType);
        foto.setTamanoConsentimiento(tamanoFinal);
        
        return consentimientoFotoRepository.save(foto);
    }
    
    @Override
    @Transactional
    public List<ConsentimientoFoto> guardarMultiplesFotos(
            Long idConsentimiento, 
            MultipartFile[] archivos,
            ImageConversionService conversionService) throws IOException {
            
        List<ConsentimientoFoto> fotosGuardadas = new ArrayList<>();
        
        if (archivos != null) {
            for (MultipartFile archivo : archivos) {
                if (!archivo.isEmpty()) {
                    try {
                        // ✅ Usar el método que ya tiene la conversión
                        ConsentimientoFoto foto = guardarFoto(idConsentimiento, archivo, null);
                        fotosGuardadas.add(foto);
                    } catch (IllegalArgumentException e) {
                        System.err.println("⚠️ Error al guardar foto: " + e.getMessage());
                    }
                }
            }
        }
        
        return fotosGuardadas;
    }
}