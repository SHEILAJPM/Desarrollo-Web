package desarrolloWeb.backend.ventas;

import java.time.LocalDate;

public class Venta {

    public static final double TASA_IGV = 0.18;

    private Long id;
    private Long clienteId;
    private String numeroComprobante;
    private Double montoSinIgv;
    private LocalDate fecha;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public Double getMontoSinIgv() {
        return montoSinIgv;
    }

    public void setMontoSinIgv(Double montoSinIgv) {
        this.montoSinIgv = montoSinIgv;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getIgv() {
        return montoSinIgv * TASA_IGV;
    }

    public double getMontoTotal() {
        return montoSinIgv + getIgv();
    }
}
