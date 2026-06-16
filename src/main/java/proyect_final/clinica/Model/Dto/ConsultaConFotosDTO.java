package  proyect_final.clinica.Model.Dto;

import org.springframework.web.multipart.MultipartFile;

public class ConsultaConFotosDTO extends ConsultaCompletaDTO {
    private MultipartFile[] odontogramaFotos;

    public MultipartFile[] getOdontogramaFotos() {
        return odontogramaFotos;
    }

    public void setOdontogramaFotos(MultipartFile[] odontogramaFotos) {
        this.odontogramaFotos = odontogramaFotos;
    }
}