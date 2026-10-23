package proyect_final.clinica.Scheduler;

import proyect_final.clinica.Service.PrestamoActualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class PrestamoScheduler {

    @Autowired
    private PrestamoActualService prestamoActualService;

    // Se ejecuta todos los días a las 00:00
    @Scheduled(cron = "0 0 0 * * *")
    public void verificarPrestamosVencidos() {
        System.out.println("🔍 Verificando préstamos vencidos: " + new Date());

        int vencidos = prestamoActualService.marcarVencidosYBloquear();

        System.out.println("✅ Préstamos vencidos actualizados: " + vencidos);
    }
}
