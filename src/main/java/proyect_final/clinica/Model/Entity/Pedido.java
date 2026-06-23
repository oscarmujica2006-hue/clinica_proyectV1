package proyect_final.clinica.Model.Entity;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;

@Getter 
@Setter
@Entity

@Table(name = "pedido")

public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;

    @ManyToOne
    @JoinColumn(name = "id_encargado_insumo", nullable = false)
    private EncargadoInsumo encargadoInsumo;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDate fechaPedido;

    @Column(name = "estado_pedido", nullable = false, length = 20)
    private String estadoPedido;    


    @Column(name = "usu_reg_ped", length = 100)
    private String usuRegPed;

    @Column(name = "usu_mod_ped", length = 100)
    private String usuModPed;

    @CreationTimestamp
    @Column(name = "fech_reg_ped", updatable = false)
    private LocalDateTime fechRegPed;

    @UpdateTimestamp
    @Column(name = "fech_mod_ped")
    private LocalDateTime fechModPed;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetallePedido> detalles = new ArrayList<>();
}
