package proyect_final.clinica.Web;

import proyect_final.clinica.Model.Dto.LoginRequest;
import proyect_final.clinica.Model.Dto.LoginResponse;
import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Model.Entity.Docente;
import proyect_final.clinica.Model.Entity.Director;
import proyect_final.clinica.Model.Entity.EncargadoInsumo;
import proyect_final.clinica.Model.Entity.Recepcion;
import proyect_final.clinica.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import proyect_final.clinica.Model.Entity.Usuario;
import java.util.Optional; 
import java.util.Map; 
import java.util.HashMap; 

@Controller
@RequestMapping("/")
public class loginViewController {

    @Autowired
    private AuthService authService;

    @Autowired
    private HttpSession session;

    @GetMapping
    public String paginaLogin() {
        return "login/login-vista";
    }

    @PostMapping("/autenticar")
    @ResponseBody
    public ResponseEntity<LoginResponse> autenticar(@RequestBody LoginRequest loginRequest) {
        
        Optional<AuthService.AuthResult> authResultOpt = authService.autenticar(
            loginRequest.getUsuario(), 
            loginRequest.getContraseña()
        );

        if (authResultOpt.isPresent()) {
            AuthService.AuthResult authResult = authResultOpt.get();
            Usuario usuario = authResult.getUsuario();
            
            session.setAttribute("idUsuario", usuario.getIdUsuario());
            session.setAttribute("nombreUsuario", usuario.getNombreUsuario());
            session.setAttribute("tipoUsuario", authResult.getTipo());
            
            String nombre = usuario.getPersona().getNombre();
            String apellidoPaterno = usuario.getPersona().getApellidoPaterno();
            String apellidoMaterno = usuario.getPersona().getApellidoMaterno();
            String apellidos = apellidoPaterno + " " + apellidoMaterno;
            
            switch (authResult.getTipo()) {
                case "ESTUDIANTE":
                    Estudiante estudiante = authResult.getEstudiante();
                    if (estudiante != null && !estudiante.getBloqueado()) {
                        session.setAttribute("idEstudiante", estudiante.getIdEstudiante());
                        session.setAttribute("codigoEstudiante", estudiante.getCodigoEstudiante());
                        
                        String email = usuario.getEmail();
                        Integer codigoEstudiante = estudiante.getCodigoEstudiante();
                        
                        System.out.println("✅ SESIÓN ESTUDIANTE - ID: " + estudiante.getIdEstudiante());
                        System.out.println("   Email: " + email);
                        System.out.println("   Código: " + codigoEstudiante);
                        
                        LoginResponse response = new LoginResponse(
                            true, "Login exitoso", nombre, apellidos,
                            estudiante.getGestion(), usuario.getIdUsuario(), 
                            estudiante.getIdEstudiante(),
                            email,
                            codigoEstudiante
                        );
                        response.setTipoUsuario("ESTUDIANTE");
                        return ResponseEntity.ok(response);
                    } else if (estudiante != null && estudiante.getBloqueado()) {
                        return ResponseEntity.status(401)
                            .body(new LoginResponse(false, "Estudiante bloqueado"));
                    }
                    break;
                    
                case "DOCENTE":
                    Docente docente = authResult.getDocente();
                    if (docente != null && docente.getEstado()) {
                        session.setAttribute("idDocente", docente.getIdDocente());
                        session.setAttribute("codigoDocente", docente.getCodigoDocente());
                        
                        System.out.println("✅ SESIÓN DOCENTE - ID: " + docente.getIdDocente());
                        
                        LoginResponse response = new LoginResponse(
                            true, "Login exitoso", nombre, apellidos,
                            usuario.getIdUsuario(), docente.getIdDocente()
                        );
                        return ResponseEntity.ok(response);
                    } else if (docente != null && !docente.getEstado()) {
                        return ResponseEntity.status(401)
                            .body(new LoginResponse(false, "Docente inactivo"));
                    }
                    break;
                
                case "ENCARGADO_INSUMO":
                    EncargadoInsumo encargado = authResult.getEncargadoInsumo();
                    if (encargado != null) {
                        Long idEncargado = encargado.getIdEncargadoInsumo();
                        session.setAttribute("idEncargadoInsumo", idEncargado);
                        session.setAttribute("areaResponsabilidad", encargado.getArea_responsabilidad());
                        
                        System.out.println("✅ SESIÓN ENCARGADO_INSUMO - ID: " + idEncargado);
                        
                        LoginResponse response = new LoginResponse(
                            true, "Login exitoso", nombre, apellidos,
                            usuario.getIdUsuario(), idEncargado
                        );
                        response.setTipoUsuario("ENCARGADO_INSUMO");
                        return ResponseEntity.ok(response);
                    }
                    break;

                case "DIRECTOR":
                    Director director = authResult.getDirector();
                    if (director != null) {
                        Long idUsuarioClinica = director.getIdUsuarioClinica();
                        session.setAttribute("idDirector", idUsuarioClinica);
                        System.out.println("✅ SESIÓN DIRECTOR - ID: " + idUsuarioClinica);
                        
                        LoginResponse response = new LoginResponse(
                            true, "Login exitoso", nombre, apellidos,
                            usuario.getIdUsuario(), idUsuarioClinica
                        );
                        response.setTipoUsuario("DIRECTOR");
                        return ResponseEntity.ok(response);
                    }
                    break;

                case "RECEPCION":
                    Recepcion recepcion = authResult.getRecepcion();
                    if (recepcion != null) {
                        Long idRecepcion = recepcion.getIdRecepcion();
                        session.setAttribute("idRecepcion", idRecepcion);
                        session.setAttribute("codigoRecepcion", recepcion.getCodigoRecepcion());
                        
                        System.out.println("✅ SESIÓN RECEPCION - ID: " + idRecepcion);
                        
                        LoginResponse response = new LoginResponse(
                            true, "Login exitoso", nombre, apellidos,
                            usuario.getIdUsuario(), idRecepcion
                        );
                        response.setTipoUsuario("RECEPCION");
                        return ResponseEntity.ok(response);
                    }
                    break;
                    
                case "ADMIN":
                    System.out.println("✅ SESIÓN ADMIN - ID Usuario: " + usuario.getIdUsuario());
                    
                    LoginResponse response = new LoginResponse(
                        true, "Login exitoso", nombre, apellidos,
                        usuario.getIdUsuario(), null
                    );
                    response.setTipoUsuario("ADMIN");
                    return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(401)
            .body(new LoginResponse(false, "Credenciales incorrectas"));
    }
    
    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/";
    }
    
    @GetMapping("/sesion-actual")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSesionActual() {
        Map<String, Object> sesionInfo = new HashMap<>();
        sesionInfo.put("idUsuario", session.getAttribute("idUsuario"));
        sesionInfo.put("tipoUsuario", session.getAttribute("tipoUsuario"));
        sesionInfo.put("idEstudiante", session.getAttribute("idEstudiante"));
        sesionInfo.put("idDocente", session.getAttribute("idDocente"));
        sesionInfo.put("idEncargadoInsumo", session.getAttribute("idEncargadoInsumo"));
        sesionInfo.put("idDirector", session.getAttribute("idDirector"));
        sesionInfo.put("idRecepcion", session.getAttribute("idRecepcion"));
        sesionInfo.put("autenticado", session.getAttribute("idUsuario") != null);
        
        return ResponseEntity.ok(sesionInfo);
    }

    @GetMapping("/encargado-insumo")
    public String paginaEncargadoInsumo() {
        if (session.getAttribute("idEncargadoInsumo") == null) {
            return "redirect:/";
        }
        return "encargado_insumo/encargado-vista";
    }

    // ⭐ ELIMINADO: @GetMapping("/archivos") - Ya existe en ArchivosViewController

    @GetMapping("/solicitud-password/vista-solicitud")
    public String paginaResetearPassword() {
        return "login/solicitud-password/vista-solicitud";
    }

    @GetMapping("/recuperacion-password/vista-password")
    public String paginaRecuperarPassword() {
        return "login/recuperacion-password/vista-password";
    }
}