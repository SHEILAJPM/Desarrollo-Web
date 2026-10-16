package desarrolloWeb.backend.usuarios;

import desarrolloWeb.backend.exception.RecursoNoEncontradoException;
import desarrolloWeb.backend.exception.ReglaNegocioException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* Spring Security busca aquí al usuario en cada petición con Basic Auth */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name())
                .disabled(!usuario.isActivo())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::de).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new ReglaNegocioException("Ya existe el usuario " + request.username());
        }

        Usuario usuario = new Usuario(request.username(),
                passwordEncoder.encode(request.password()), request.rol());
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UsuarioResponse cambiarEstado(Long id, boolean activo, String usuarioActual) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));

        if (!activo && usuario.getUsername().equals(usuarioActual)) {
            throw new ReglaNegocioException("No puedes desactivar tu propio usuario");
        }

        usuario.setActivo(activo);
        return UsuarioResponse.de(usuario);
    }

    /* Crea un usuario solo si todavía no existe (usuarios iniciales) */
    @Transactional
    public void crearSiNoExiste(String username, String password, Rol rol) {
        if (!usuarioRepository.existsByUsername(username)) {
            usuarioRepository.save(new Usuario(username, passwordEncoder.encode(password), rol));
        }
    }
}
