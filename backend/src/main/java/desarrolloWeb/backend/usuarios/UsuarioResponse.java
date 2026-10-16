package desarrolloWeb.backend.usuarios;

public record UsuarioResponse(Long id, String username, Rol rol, boolean activo) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getUsername(),
                usuario.getRol(), usuario.isActivo());
    }
}
