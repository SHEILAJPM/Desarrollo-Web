package desarrolloWeb.backend.personal.asistencia;

import desarrolloWeb.backend.personal.asistencia.TipoMarcacion;

public record MarcarRequest(String documentoIdentidad, TipoMarcacion tipo, Double latitud, Double longitud) {
}
