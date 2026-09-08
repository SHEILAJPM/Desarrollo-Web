package desarrolloWeb.backend.asistencia;

public record MarcarRequest(String documentoIdentidad, TipoMarcacion tipo, Double latitud, Double longitud) {
}
