package desarrolloWeb.backend.pagos;

import java.time.LocalDate;

public record RegistrarPagoRequest(
        String tipoBeneficiario,
        String documentoIdentidad,
        String rucProveedor,
        String concepto,
        Double monto,
        LocalDate fechaProgramada) {
}
