package desarrolloWeb.backend.ventas.dto;

import desarrolloWeb.backend.tributario.TipoDocumento;

public record ClienteResponse(Long id, String documento, String nombre, TipoDocumento tipoDocumento) {
}
