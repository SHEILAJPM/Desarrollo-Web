package desarrolloWeb.backend.ventas.dto;

import desarrolloWeb.backend.tributario.TipoComprobante;

public record VentasPorComprobante(TipoComprobante tipoComprobante, Long cantidad) {
}
