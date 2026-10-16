package desarrolloWeb.backend.ventas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TicketPromedioResponse(LocalDate desde, LocalDate hasta, BigDecimal ticketPromedio) {
}
