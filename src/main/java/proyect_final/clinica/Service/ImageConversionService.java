package proyect_final.clinica.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Service
public class ImageConversionService {

    /**
     * Convierte una imagen a WebP con calidad ajustable
     * @param file Archivo de imagen (JPG, PNG, GIF, etc.)
     * @param quality Calidad 0.0 - 1.0 (0.8 = 80%)
     * @return byte[] en formato WebP
     */
    public byte[] convertToWebP(MultipartFile file, float quality) throws IOException {
        // Leer la imagen original
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IllegalArgumentException("El archivo no es una imagen válida");
        }

        // Obtener writer para WebP
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No se encontró soporte para WebP");
        }
        
        ImageWriter writer = writers.next();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);

        // Configurar calidad
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType(param.getCompressionTypes()[0]); // "lossy" o "lossless"
            param.setCompressionQuality(quality);
        }

        // Escribir la imagen
        writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
        
        // Limpiar recursos
        ios.close();
        writer.dispose();
        
        return baos.toByteArray();
    }

    /**
     * Convierte con calidad por defecto (80%)
     */
    public byte[] convertToWebP(MultipartFile file) throws IOException {
        return convertToWebP(file, 0.8f);
    }

    /**
     * Verifica si el archivo es una imagen soportada
     */
    public boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
            contentType.startsWith("image/") ||
            contentType.equals("application/pdf") // PDF no se convierte
        );
    }
}