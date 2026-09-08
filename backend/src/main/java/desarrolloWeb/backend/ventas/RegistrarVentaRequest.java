package desarrolloWeb.backend.ventas;

import java.time.LocalDate;

public record RegistrarVentaRequest(String documentoCliente, String numeroComprobante, Double montoSinIgv, LocalDate fecha) {
}
