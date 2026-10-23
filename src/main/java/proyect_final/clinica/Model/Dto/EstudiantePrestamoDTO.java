package proyect_final.clinica.Model.Dto;

import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Model.Entity.Persona;
import proyect_final.clinica.Model.Entity.Usuario;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EstudiantePrestamoDTO {
    private Long idEstudiante;
    private Integer codigoEstudiante;
    private String nombreCompleto;
    private String email;
    private String nombreUsuario;  // ← Útil para mostrar en el frontend
    private Boolean bloqueado;      // ← Para saber si puede hacer préstamos
    private String motivoBloqueo;   // ARCHIVO / RADIOGRAFIA / MULTIPLE
    private LocalDateTime fechaBloqueo;
    
    // Constructor desde entidad
    public EstudiantePrestamoDTO(Estudiante estudiante) {
        if (estudiante != null) {
            this.idEstudiante = estudiante.getIdEstudiante();
            this.codigoEstudiante = estudiante.getCodigoEstudiante();
            this.bloqueado = estudiante.getBloqueado();
            this.motivoBloqueo = estudiante.getRazonBloqueo();
            this.fechaBloqueo = estudiante.getFechaBloqueo();
            
            // Obtener datos del usuario
            Usuario usuario = estudiante.getUsuario();
            if (usuario != null) {
                this.email = usuario.getEmail();
                this.nombreUsuario = usuario.getNombreUsuario();
                
                // Obtener datos de la persona
                Persona persona = usuario.getPersona();
                if (persona != null) {
                    this.nombreCompleto = persona.getNombre() + " " + 
                                         persona.getApellidoPaterno() + " " + 
                                         persona.getApellidoMaterno();
                }
            }
        }
    }
}