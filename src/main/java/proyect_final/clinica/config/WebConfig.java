package proyect_final.clinica.config;

import proyect_final.clinica.Interceptor.BloqueoEstudianteInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private BloqueoEstudianteInterceptor bloqueoInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(bloqueoInterceptor)
                .addPathPatterns(
                    // 🔥 SOLO APLICAR A RUTAS DE ESTUDIANTES 192.168.100.113
                    "/Estudiantes_Static/**",
                    "/estudiante/**",
                    "/consulta/**"
                )
                .excludePathPatterns(
                    // ✅ EXCLUIR SIEMPRE RECURSOS ESTÁTICOS
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/img/**",
                    "/webjars/**",
                    "/static/**",
                    
                    // ✅ EXCLUIR APIs (accesibles para todos)
                    "/api/**",
                    
                    // ✅ EXCLUIR RUTAS PÚBLICAS
                    "/",
                    "/login",
                    "/autenticar",
                    "/logout",
                    
                    // ✅ EXCLUIR RUTAS DE ADMIN/ARCHIVO
                    "/recepcion/**",
                    "/archivos/**",
                    "/estudiantes/**",
                    "/bloqueado"
                );
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/"); // Ruta absoluta o relativa
    }
}