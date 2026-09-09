package desarrolloWeb.backend.personal.documentos;

import java.time.LocalDate;

public record RegistrarDocumentoRequest(String documentoIdentidad, String tipo, LocalDate fechaVencimiento) {
}
