package proyect_final.clinica.Interceptor;

import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Model.Dao.EstudianteRepository;
import proyect_final.clinica.Service.Impl.SolicitudRadiografiaServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class BloqueoEstudianteInterceptor implements HandlerInterceptor {

    @Autowired
    private EstudianteRepository estudianteRepository;
    
    @Autowired
    private SolicitudRadiografiaServiceImpl solicitudRadiografiaService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) throws Exception {
        
        String path = request.getRequestURI();
        System.out.println("🔍 Interceptor - Path: " + path);
        
        // 🔥 1. SIEMPRE PERMITIR RECURSOS ESTÁTICOS
        if (path.startsWith("/css/") ||
            path.startsWith("/js/") ||
            path.startsWith("/images/") ||
            path.startsWith("/img/") ||
            path.startsWith("/webjars/") ||
            path.startsWith("/static/") ||
            path.endsWith(".css") ||
            path.endsWith(".js") ||
            path.endsWith(".png") ||
            path.endsWith(".jpg") ||
            path.endsWith(".jpeg") ||
            path.endsWith(".gif") ||
            path.endsWith(".ico") ||
            path.endsWith(".js.map")) {
            System.out.println("✅ Static resource - allowing: " + path);
            return true;
        }
        
        // 🔥 2. PERMITIR APIs SIEMPRE
        if (path.startsWith("/api/")) {
            System.out.println("✅ API route - allowing for all users: " + path);
            return true;
        }
        
        // 🔥 3. RUTAS PÚBLICAS Y DE ARCHIVO
        if (path.equals("/") ||
            path.equals("/login") ||
            path.equals("/autenticar") ||
            path.equals("/logout") ||
            path.startsWith("/recepcion/") ||
            path.startsWith("/archivos/") ||
            path.startsWith("/estudiantes/") ||
            path.equals("/bloqueado") ||
            path.startsWith("/archivo.html") ||
            path.startsWith("/prestamo.html") ||
            path.startsWith("/devolucion.html") ||
            // 🔥 AGREGAR RUTAS DE RADIOGRAFÍAS (públicas)
            path.startsWith("/solicitar-radiografia.html") ||
            path.startsWith("/gestion-solicitudes.html") ||
            path.startsWith("/ver-solicitud.html")) {
            System.out.println("✅ Public/Admin/Archivo route - allowing: " + path);
            return true;
        }
        
        // 🔥 4. PARA RUTAS DE ESTUDIANTES, VERIFICAR SESIÓN Y BLOQUEO
        if (path.startsWith("/Estudiantes_Static/") || 
            path.startsWith("/consulta/") ||
            path.startsWith("/revision.html")) {
            
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("idEstudiante") == null) {
                System.out.println("❌ Estudiante - No session, redirect to login: " + path);
                response.sendRedirect("/login");
                return false;
            }
            
            Long idEstudiante = (Long) session.getAttribute("idEstudiante");
            
            // 🔥 =============================================
            // 🔥 NUEVO: VALIDAR CON FUNCIÓN POSTGRESQL
            // 🔥 =============================================
            try {
                Map<String, Object> validacion = solicitudRadiografiaService.puedeSolicitar(idEstudiante);
                
                if (!(Boolean) validacion.get("puedeSolicitar")) {
                    String motivo = (String) validacion.get("motivo");
                    String mensaje = (String) validacion.get("mensaje");
                    
                    System.out.println("❌ Estudiante BLOQUEADO - Motivo: " + motivo);
                    System.out.println("   Mensaje: " + mensaje);
                    
                    // Guardar en sesión para mostrar en página de bloqueo
                    session.setAttribute("mensajeBloqueo", mensaje);
                    session.setAttribute("tipoBloqueo", motivo);
                    
                    // Si es petición AJAX
                    if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write(
                            "{\"mensaje\":\"" + mensaje + 
                            "\", \"tipo\":\"" + motivo + 
                            "\", \"redirect\":\"/bloqueado\"}"
                        );
                        return false;
                    }
                    
                    response.sendRedirect("/bloqueado");
                    return false;
                }
                
                // 🔥 Verificar límite de 3 solicitudes por día (solo para crear)
                if (path.startsWith("/solicitar-radiografia.html") || 
                    path.startsWith("/api/solicitudes-radiografia/crear")) {
                    
                    int solicitudesHoy = solicitudRadiografiaService.contarSolicitudesHoy(idEstudiante);
                    if (solicitudesHoy >= 3) {
                        String mensaje = "⚠️ Límite de 3 solicitudes diarias alcanzado. Puede solicitar nuevamente mañana.";
                        
                        session.setAttribute("mensajeBloqueo", mensaje);
                        session.setAttribute("tipoBloqueo", "LIMITE_DIARIO");
                        
                        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                "{\"mensaje\":\"" + mensaje + 
                                "\", \"tipo\":\"LIMITE_DIARIO\", " +
                                "\"solicitudesHoy\":" + solicitudesHoy + 
                                ", \"limiteMaximo\":3, " +
                                "\"redirect\":\"/bloqueado\"}"
                            );
                            return false;
                        }
                        
                        response.sendRedirect("/bloqueado");
                        return false;
                    }
                }
                
            } catch (Exception e) {
                System.err.println("❌ Error al validar bloqueo: " + e.getMessage());
                // Si hay error, permitir acceso (fallback seguro)
                return true;
            }
            
            System.out.println("✅ Estudiante route - allowing: " + path);
            return true;
        }
        
        // 🔥 5. CUALQUIER OTRA RUTA, PERMITIR
        System.out.println("✅ Other route - allowing: " + path);
        return true;
    }
}