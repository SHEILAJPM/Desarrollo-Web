package desarrolloWeb.backend.personal.asistencia;

import java.time.LocalDateTime;

public record MarcarRequest(
        String documentoIdentidad,
        TipoMarcacion tipo,
        LocalDateTime fechaHora
) {
}