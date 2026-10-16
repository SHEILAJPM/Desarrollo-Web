package desarrolloWeb.backend.proveedores;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import desarrolloWeb.backend.pagos.PagoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final PagoRepository pagoRepository;
    private final List<Proveedor> proveedores = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(0);

    @Autowired
    public ProveedorService(ProveedorRepository proveedorRepository, PagoRepository pagoRepository) {
        this.proveedorRepository = proveedorRepository;
        this.pagoRepository = pagoRepository;
    }

    /** Constructor para uso aislado del servicio, por ejemplo en pruebas unitarias. */
    public ProveedorService() {
        this.proveedorRepository = null;
        this.pagoRepository = null;
    }

    @Transactional
    public Proveedor registrar(String ruc, String razonSocial) {
        validarDatos(ruc, razonSocial);
        ruc = ruc.trim();
        razonSocial = razonSocial.trim();
        if (buscarPorRuc(ruc).isPresent()) {
            throw new IllegalStateException("Ya existe un proveedor registrado con el RUC " + ruc);
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setRuc(ruc);
        proveedor.setRazonSocial(razonSocial);

        if (proveedorRepository != null) {
            return proveedorRepository.save(proveedor);
        }
        proveedor.setId(contadorId.incrementAndGet());
        proveedores.add(proveedor);
        return proveedor;
    }

    public Optional<Proveedor> buscarPorId(Long id) {
        if (proveedorRepository != null) {
            return proveedorRepository.findById(id);
        }
        return proveedores.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Optional<Proveedor> buscarPorRuc(String ruc) {
        if (ruc == null) {
            return Optional.empty();
        }
        String rucNormalizado = ruc.trim();
        if (proveedorRepository != null) {
            return proveedorRepository.findByRuc(rucNormalizado);
        }
        return proveedores.stream()
                .filter(p -> p.getRuc().equals(rucNormalizado))
                .findFirst();
    }

    public List<Proveedor> listarTodos() {
        if (proveedorRepository != null) {
            return proveedorRepository.findAll();
        }
        return List.copyOf(proveedores);
    }

    @Transactional
    public Proveedor actualizar(Long id, String ruc, String razonSocial) {
        Proveedor proveedor = buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un proveedor con id " + id));

        validarDatos(ruc, razonSocial);
        String rucNormalizado = ruc.trim();
        String razonSocialNormalizada = razonSocial.trim();
        if (proveedorRepository != null) {
            if (proveedorRepository.existsByRucAndIdNot(rucNormalizado, id)) {
                throw new IllegalStateException("Ya existe un proveedor registrado con el RUC " + rucNormalizado);
            }
        } else {
            buscarPorRuc(rucNormalizado)
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> {
                    throw new IllegalStateException("Ya existe un proveedor registrado con el RUC " + rucNormalizado);
                });
        }

        proveedor.setRuc(rucNormalizado);
        proveedor.setRazonSocial(razonSocialNormalizada);
        return proveedorRepository == null ? proveedor : proveedorRepository.save(proveedor);
    }

    @Transactional
    public void eliminar(Long id) {
        Proveedor proveedor = buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un proveedor con id " + id));
        if (pagoRepository != null && pagoRepository.existsByProveedor_Id(id)) {
            throw new IllegalStateException("No se puede eliminar el proveedor porque tiene pagos registrados");
        }
        if (proveedorRepository != null) {
            proveedorRepository.delete(proveedor);
            return;
        }
        proveedores.remove(proveedor);
    }

    private void validarDatos(String ruc, String razonSocial) {
        if (ruc == null || !ruc.trim().matches("\\d{11}")) {
            throw new IllegalArgumentException("El RUC debe contener exactamente 11 dígitos");
        }
        if (razonSocial == null || razonSocial.isBlank()) {
            throw new IllegalArgumentException("La razón social del proveedor es obligatoria");
        }
    }
}
