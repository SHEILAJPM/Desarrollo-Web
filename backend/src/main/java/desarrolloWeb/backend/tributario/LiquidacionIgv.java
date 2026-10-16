package desarrolloWeb.backend.tributario;

import java.math.BigDecimal;

public record LiquidacionIgv(BigDecimal igvPorPagar, BigDecimal saldoAFavor) {
}
