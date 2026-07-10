package proyect_final.clinica.Model.Dto;

import proyect_final.clinica.Model.Entity.Consentimiento;
import proyect_final.clinica.Model.Entity.DiagnosticoTratamientoDiente;
import proyect_final.clinica.Model.Entity.Estudiante;
import proyect_final.clinica.Model.Entity.SolicitudRadiografia;
import proyect_final.clinica.Model.Entity.DetalleSolicitudRadiografia;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstudianteReciboDTO {
    private Long idEstudiante;
    private Integer codigoEstudiante;
    private String gestion;
    private String nombreCompleto;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Long idUsuario;
    private String clinica;
    private String roteActual;
    private String turno;
    private Boolean bloqueado;
    private String materiaNombre;
    private List<String> radiografias;
    private List<String> tratamientos;
    private List<Integer> dientesTratados;

    public EstudianteReciboDTO(Estudiante estudiante, List<Consentimiento> consentimientos) {
        this.idEstudiante = estudiante.getIdEstudiante();
        this.codigoEstudiante = estudiante.getCodigoEstudiante();
        this.gestion = estudiante.getGestion();
        this.bloqueado = estudiante.getBloqueado();
        
        Set<String> radiografiasSet = new HashSet<>();
        Set<String> tratamientosSet = new HashSet<>();
        Set<Integer> dientesSet = new HashSet<>();
        
        if (estudiante.getUsuario() != null) {
            this.idUsuario = estudiante.getUsuario().getIdUsuario();
            
            if (estudiante.getUsuario().getPersona() != null) {
                var persona = estudiante.getUsuario().getPersona();
                this.nombre = persona.getNombre();
                this.apellidoPaterno = persona.getApellidoPaterno();
                this.apellidoMaterno = persona.getApellidoMaterno();
                
                this.nombreCompleto = String.format("%s %s %s", 
                    persona.getNombre() != null ? persona.getNombre() : "",
                    persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "",
                    persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : ""
                ).trim();
                
                if (this.nombreCompleto.isEmpty()) {
                    this.nombreCompleto = "Estudiante";
                }
            }
        }
        
        if (estudiante.getRoteActual() != null) {
            this.roteActual = estudiante.getRoteActual().getNombreRote();
        }
        
        if (consentimientos != null && !consentimientos.isEmpty()) {
            for (Consentimiento consentimiento : consentimientos) {
                if (consentimiento.getDocente() != null && consentimiento.getDocente().getClinica() != null) {
                    var clinica = consentimiento.getDocente().getClinica();
                    this.clinica = clinica.getNombreClinica();
                    
                    if (clinica.getTurno() != null) {
                        this.turno = clinica.getTurno().getNombreTurno();
                    }
                }
                
                if (consentimiento.getDiagnosticoTratamiento() != null) {
                    var diagTrat = consentimiento.getDiagnosticoTratamiento();
                    
                    // =============================================
                    // 1. OBTENER TRATAMIENTOS
                    // =============================================
                    if (diagTrat.getTratamiento() != null) {
                        String nombreTratamiento = diagTrat.getTratamiento().getNombreTratamiento();
                        if (nombreTratamiento != null && !nombreTratamiento.isEmpty()) {
                            tratamientosSet.add(nombreTratamiento);
                        }
                    }
                    
                    // =============================================
                    // 2. OBTENER DIENTES TRATADOS (SOLO REALIZADOS)
                    // =============================================
                    if (diagTrat.getDientes() != null && !diagTrat.getDientes().isEmpty()) {
                        for (DiagnosticoTratamientoDiente dienteDiag : diagTrat.getDientes()) {
                            // ✅ FILTRAR: Solo dientes con estado "REALIZADO" o "COMPLETADO"
                            String estadoDiente = dienteDiag.getEstado();
                            if (dienteDiag.getDiente() != null && 
                                (estadoDiente != null && 
                                 (estadoDiente.equalsIgnoreCase("REALIZADO") || 
                                  estadoDiente.equalsIgnoreCase("COMPLETADO")))) {
                                dientesSet.add(dienteDiag.getDiente());
                            }
                        }
                    }
                    
                    // =============================================
                    // 3. OBTENER RADIOGRAFÍAS (SOLO APROBADAS)
                    // =============================================
                    if (diagTrat.getSolicitudesRadiografia() != null && !diagTrat.getSolicitudesRadiografia().isEmpty()) {
                        for (SolicitudRadiografia solicitud : diagTrat.getSolicitudesRadiografia()) {
                            // ✅ FILTRAR: Solo solicitudes con estado "APROBADO"
                            String estadoSolicitud = solicitud.getEstado();
                            if (estadoSolicitud != null && estadoSolicitud.equalsIgnoreCase("APROBADO")) {
                                
                                String radiografiaInfo = "";
                                
                                if (solicitud.getRadiografia() != null) {
                                    radiografiaInfo = solicitud.getRadiografia().getNombreRayo();
                                } else {
                                    radiografiaInfo = "Radiografía";
                                }
                                
                                // Obtener los dientes desde los detalles de la solicitud
                                Set<Integer> dientesRadiografia = new HashSet<>();
                                if (solicitud.getDetalles() != null && !solicitud.getDetalles().isEmpty()) {
                                    for (DetalleSolicitudRadiografia detalle : solicitud.getDetalles()) {
                                        if (detalle.getDiagTratDiente() != null && 
                                            detalle.getDiagTratDiente().getDiente() != null) {
                                            // ✅ FILTRAR: Solo dientes con estado "REALIZADO"
                                            String estadoDiente = detalle.getDiagTratDiente().getEstado();
                                            if (estadoDiente != null && 
                                                (estadoDiente.equalsIgnoreCase("REALIZADO") || 
                                                 estadoDiente.equalsIgnoreCase("COMPLETADO"))) {
                                                dientesRadiografia.add(detalle.getDiagTratDiente().getDiente());
                                            }
                                        }
                                    }
                                }
                                
                                // Agregar los dientes a la información de la radiografía
                                if (!dientesRadiografia.isEmpty()) {
                                    List<Integer> dientesList = new ArrayList<>(dientesRadiografia);
                                    java.util.Collections.sort(dientesList);
                                    radiografiaInfo += " (Dientes: " + String.join(", ", 
                                        dientesList.stream().map(String::valueOf).toArray(String[]::new)) + ")";
                                }
                                
                                radiografiasSet.add(radiografiaInfo);
                            }
                        }
                    }
                    
                    // =============================================
                    // 4. OBTENER MATERIA
                    // =============================================
                    try {
                        if (diagTrat.getTratamiento() != null) {
                            var tratamiento = diagTrat.getTratamiento();
                            
                            if (tratamiento.getCupos() != null && !tratamiento.getCupos().isEmpty()) {
                                for (var cupo : tratamiento.getCupos()) {
                                    if (cupo.getMateria() != null) {
                                        this.materiaNombre = cupo.getMateria().getNombreMateria();
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error al obtener materia: " + e.getMessage());
                    }
                }
            }
        }
        
        this.radiografias = new ArrayList<>(radiografiasSet);
        this.tratamientos = new ArrayList<>(tratamientosSet);
        this.dientesTratados = new ArrayList<>(dientesSet);
        
        java.util.Collections.sort(this.tratamientos);
        java.util.Collections.sort(this.dientesTratados);
        java.util.Collections.sort(this.radiografias);
        
        if (this.materiaNombre == null || this.materiaNombre.isEmpty()) {
            this.materiaNombre = "CLÍNICA INTEGRAL ADULTO";
        }
        
        if (this.clinica == null || this.clinica.isEmpty()) {
            this.clinica = "CLÍNICA ODONTOLÓGICA UAP";
        }
    }
}