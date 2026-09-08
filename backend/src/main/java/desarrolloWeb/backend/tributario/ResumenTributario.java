package desarrolloWeb.backend.tributario;

import java.time.LocalDate;

public record ResumenTributario(
        LocalDate desde,
        LocalDate hasta,
        double totalComprasSinIgv,
        double igvCompras,
        double totalVentasSinIgv,
        double igvVentas,
        double igvPorPagar) {
}
