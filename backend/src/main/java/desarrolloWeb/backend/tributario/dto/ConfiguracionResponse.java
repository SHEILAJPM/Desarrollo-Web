package desarrolloWeb.backend.tributario.dto;

import java.math.BigDecimal;

public record ConfiguracionResponse(String codigo, BigDecimal tasa, boolean vigente) {
}
