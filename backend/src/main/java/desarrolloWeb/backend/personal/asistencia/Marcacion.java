package desarrolloWeb.backend.personal.asistencia;

import java.time.LocalDateTime;

public class Marcacion {

    private Long id;
    private Long trabajadorId;
    private TipoMarcacion tipo;
    private LocalDateTime fechaHora;
    private Double latitud;
    private Double longitud;

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

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}
