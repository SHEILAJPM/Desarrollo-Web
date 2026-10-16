package desarrolloWeb.backend.tributario;

import java.math.BigDecimal;

public record Desglose(BigDecimal base, BigDecimal igv) {
}
