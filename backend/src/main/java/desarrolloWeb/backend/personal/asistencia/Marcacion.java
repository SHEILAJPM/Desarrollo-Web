
package desarrolloWeb.backend.personal.asistencia;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "marcaciones")
public class Marcacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trabajador_id", nullable = false)
    private Long trabajadorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMarcacion tipo;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    public Marcacion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrabajadorId() {
        return trabajadorId;
    }

    public void setTrabajadorId(Long trabajadorId) {
        this.trabajadorId = trabajadorId;
    }

    public TipoMarcacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoMarcacion tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
