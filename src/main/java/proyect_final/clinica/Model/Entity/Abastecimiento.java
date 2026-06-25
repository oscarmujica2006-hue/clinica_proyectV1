package proyect_final.clinica.Model.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "abastecimiento")
public class Abastecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_abastecimiento")
    private Long idAbastecimiento;

    @Column(name = "codigo_abastecimiento", unique = true)
    private String codigoAbastecimiento;

    @ManyToOne
    @JoinColumn(name = "id_director")
    private Director director;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @Column(name = "fecha_abastecimiento")
    private LocalDate fechaAbastecimiento;

    @Column(name = "estado_abastecimiento")
    private String estadoAbastecimiento;


    @Column(name = "usu_reg_abas")
    private Integer usuRegAbas;

    @Column(name = "usu_mod_abas")
    private Integer usuModAbas;

    @CreationTimestamp
    @Column(name = "fech_reg_abas", updatable = false)
    private LocalDateTime fechRegAbas;

    @UpdateTimestamp
    @Column(name = "fech_mod_abas")
    private LocalDateTime fechModAbas;

    @OneToMany(mappedBy = "abastecimiento")
    private List<DetalleAbastecimiento> detalles;

    
}