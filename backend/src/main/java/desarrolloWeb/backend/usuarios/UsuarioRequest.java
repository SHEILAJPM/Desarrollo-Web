package desarrolloWeb.backend.usuarios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(

        @NotBlank(message = "El usuario es obligatorio")
        @Pattern(regexp = "^[a-zA-Z0-9._-]{3,50}$",
                message = "El usuario debe tener de 3 a 50 caracteres (letras, números, punto o guion)")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
        String password,

        @NotNull(message = "El rol es obligatorio")
        Rol rol) {
}
