package desarrolloWeb.backend.ventas.dto;

import desarrolloWeb.backend.tributario.TipoComprobante;
import desarrolloWeb.backend.ventas.entity.EstadoVenta;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VentaResponse(
        Long id,
        Long clienteId,
        String documentoCliente,
        String nombreCliente,
        TipoComprobante tipoComprobante,
        String numeroComprobante,
        LocalDate fecha,
        BigDecimal montoSinIgv,
        BigDecimal igv,
        BigDecimal montoTotal,
        BigDecimal tasaAplicada,
        EstadoVenta estado) {
}
