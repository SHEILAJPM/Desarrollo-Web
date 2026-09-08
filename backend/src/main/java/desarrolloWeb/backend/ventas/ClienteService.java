package desarrolloWeb.backend.ventas;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ClienteService {

    private final List<Cliente> clientes = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(0);

    public Cliente registrar(String documento, String nombre) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento del cliente es obligatorio");
        }
        if (buscarPorDocumento(documento).isPresent()) {
            throw new IllegalStateException("Ya existe un cliente registrado con el documento " + documento);
        }

        Cliente cliente = new Cliente();
        cliente.setId(contadorId.incrementAndGet());
        cliente.setDocumento(documento);
        cliente.setNombre(nombre);

        clientes.add(cliente);
        return cliente;
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clientes.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    public Optional<Cliente> buscarPorDocumento(String documento) {
        return clientes.stream()
                .filter(c -> c.getDocumento().equals(documento))
                .findFirst();
    }

    public List<Cliente> listarTodos() {
        return clientes;
    }
}
