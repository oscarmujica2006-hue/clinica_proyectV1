package proyect_final.clinica.Service.Impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import proyect_final.clinica.Model.Dao.SolicitudRadiografiaRepository;
import proyect_final.clinica.Model.Entity.Consentimiento;
import proyect_final.clinica.Model.Entity.DiagnosticoTratamiento;
import proyect_final.clinica.Model.Entity.Radiografia;
import proyect_final.clinica.Model.Entity.SolicitudRadiografia;
import proyect_final.clinica.Service.ReciboService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudRadiografiaServiceImplTest {

    @Mock
    private SolicitudRadiografiaRepository solicitudRadiografiaRepository;

    @Mock
    private ReciboService reciboService;

    @InjectMocks
    private SolicitudRadiografiaServiceImpl service;

    @Test
    void aprobarSolicitud_debeRegistrarRadiografiaEnRecibo() {
        SolicitudRadiografia solicitud = new SolicitudRadiografia();
        solicitud.setIdSolicitudRadiografia(10L);
        solicitud.setEstado("PENDIENTE");

        Radiografia radiografia = new Radiografia();
        radiografia.setPrecioRayo(150.0);
        solicitud.setRadiografia(radiografia);

        Consentimiento consentimiento = new Consentimiento();
        consentimiento.setIdConsentimiento(77L);

        DiagnosticoTratamiento diagnostico = new DiagnosticoTratamiento();
        diagnostico.setConsentimiento(consentimiento);
        solicitud.setDiagnosticoTratamiento(diagnostico);

        when(solicitudRadiografiaRepository.findById(10L)).thenReturn(Optional.of(solicitud));

        service.aprobarSolicitud(10L);

        verify(reciboService).generarRecibo(
                eq("RADIOGRAFIA"),
                eq(10L),
                any(),
                eq(150.0),
                eq(null),
                eq(77L)
        );
        assertEquals("APROBADO", solicitud.getEstado());
    }
}
