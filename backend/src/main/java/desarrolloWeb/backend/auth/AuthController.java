
package desarrolloWeb.backend.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @GetMapping("/me")
    public Map<String, String> obtenerUsuario(
            Authentication autenticacion) {

        Map<String, String> datos = new HashMap<>();

        datos.put("usuario", autenticacion.getName());

        String autoridad = autenticacion.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        datos.put("rol", autoridad.replace("ROLE_", ""));

        return datos;
    }
}
