package desarrolloWeb.backend.compras;

import java.time.LocalDate;

public record RegistrarCompraRequest(String ruc, String numeroComprobante, Double montoSinIgv, LocalDate fecha) {
}
