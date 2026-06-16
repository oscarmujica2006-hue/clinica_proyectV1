package proyect_final.clinica.Interceptor;

import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Model.Dao.EstudianteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class BloqueoEstudianteInterceptor implements HandlerInterceptor {

    @Autowired
    private EstudianteRepository estudianteRepository;
    
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
            path.endsWith(".js.map")) {   // ← Agregar esto
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
            path.startsWith("/recepcion/") ||    // ← CRÍTICO para archivo
            path.startsWith("/archivos/") ||      // ← CRÍTICO para archivo
            path.startsWith("/estudiantes/") ||
            path.equals("/bloqueado") ||
            
            // 🔥 AGREGAR TODAS LAS RUTAS DE ARCHIVO
            path.startsWith("/archivo.html") ||
            path.startsWith("/prestamo.html") ||
            path.startsWith("/devolucion.html")) {
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
            Estudiante estudiante = estudianteRepository.findById(idEstudiante).orElse(null);
            
            if (estudiante != null && estudiante.getBloqueado()) {
                System.out.println("❌ Estudiante bloqueado - redirect: " + path);
                
                if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"mensaje\":\"Usuario bloqueado por no devolver archivo\", \"redirect\":\"/bloqueado\"}");
                    return false;
                }
                
                response.sendRedirect("/bloqueado");
                return false;
            }
            
            System.out.println("✅ Estudiante route - allowing: " + path);
            return true;
        }
        
        // 🔥 5. CUALQUIER OTRA RUTA, PERMITIR
        System.out.println("✅ Other route - allowing: " + path);
        return true;
    }
}