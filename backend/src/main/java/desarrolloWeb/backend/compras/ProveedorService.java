package desarrolloWeb.backend.compras;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProveedorService {

    private final List<Proveedor> proveedores = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(0);

    public Proveedor registrar(String ruc, String razonSocial) {
        if (ruc == null || ruc.isBlank()) {
            throw new IllegalArgumentException("El RUC del proveedor es obligatorio");
        }
        if (buscarPorRuc(ruc).isPresent()) {
            throw new IllegalStateException("Ya existe un proveedor registrado con el RUC " + ruc);
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setId(contadorId.incrementAndGet());
        proveedor.setRuc(ruc);
        proveedor.setRazonSocial(razonSocial);

        proveedores.add(proveedor);
        return proveedor;
    }

    public Optional<Proveedor> buscarPorId(Long id) {
        return proveedores.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Optional<Proveedor> buscarPorRuc(String ruc) {
        return proveedores.stream()
                .filter(p -> p.getRuc().equals(ruc))
                .findFirst();
    }

    public List<Proveedor> listarTodos() {
        return proveedores;
    }

    public Proveedor actualizar(Long id, String ruc, String razonSocial) {
        Proveedor proveedor = buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un proveedor con id " + id));

        if (ruc == null || ruc.isBlank()) {
            throw new IllegalArgumentException("El RUC del proveedor es obligatorio");
        }
        buscarPorRuc(ruc)
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> {
                    throw new IllegalStateException("Ya existe un proveedor registrado con el RUC " + ruc);
                });

        proveedor.setRuc(ruc);
        proveedor.setRazonSocial(razonSocial);
        return proveedor;
    }

    public void eliminar(Long id) {
        Proveedor proveedor = buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un proveedor con id " + id));
        proveedores.remove(proveedor);
    }
}
