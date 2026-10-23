package proyect_final.clinica.Model.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuario_estudiante")
public class Estudiante {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estudiante")
    private Long idEstudiante;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)  
    private Usuario usuario;
    
    @Column(name = "gestion", nullable = false, length = 43)
    private String gestion;

    @ManyToOne
    @JoinColumn(name = "id_rote_actual", nullable = false)
    private Rote roteActual;
    
    @Column(name = "codigo_estudiante", nullable = false)
    private Integer codigoEstudiante;
    
    // 🔥 CAMPOS DE BLOQUEO
    @Column(name = "bloqueado", nullable = false)
    private Boolean bloqueado = false;
    
    @Column(name = "razon_bloqueo", length = 50)
    private String razonBloqueo;  // "ARCHIVO", "RADIOGRAFIA", "MULTIPLE"
    
    @Column(name = "fecha_bloqueo")
    private LocalDateTime fechaBloqueo;
    
    @Column(name = "usu_reg_usuEst")
    private Integer usuRegUsuEst;

    @Column(name = "usu_mod_usuEst")
    private Integer usuModUsuEst;

    @CreationTimestamp
    @Column(name = "fech_reg_usuEst", updatable = false)
    private LocalDateTime fechRegUsuEst;

    @UpdateTimestamp
    @Column(name = "fech_mod_usuEst")
    private LocalDateTime fechModUsuEst;
    
    public boolean isBloqueadoPorRadiografia() {
        return bloqueado != null && bloqueado && "RADIOGRAFIA".equals(razonBloqueo);
    }
    
    public boolean isBloqueadoPorArchivo() {
        return bloqueado != null && bloqueado && "ARCHIVO".equals(razonBloqueo);
    }
    
    public boolean isBloqueadoMultiple() {
        return bloqueado != null && bloqueado && "MULTIPLE".equals(razonBloqueo);
    }
}