package desarrolloWeb.backend.compras;

import java.time.LocalDate;

public class Compra {

    public static final double TASA_IGV = 0.18;

    private Long id;
    private Long proveedorId;
    private String numeroComprobante;
    private Double montoSinIgv;
    private LocalDate fecha;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
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
