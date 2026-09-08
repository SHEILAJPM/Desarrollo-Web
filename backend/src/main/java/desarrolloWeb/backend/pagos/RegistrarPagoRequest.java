package desarrolloWeb.backend.pagos;

import java.time.LocalDate;

public record RegistrarPagoRequest(String documentoIdentidad, String concepto, Double monto, LocalDate fechaProgramada) {
}
