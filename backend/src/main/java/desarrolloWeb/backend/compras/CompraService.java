package desarrolloWeb.backend.compras;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CompraService {

    private final ProveedorService proveedorService;
    private final List<Compra> compras = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(0);

    public CompraService(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    public Compra registrar(String rucProveedor, String numeroComprobante, Double montoSinIgv, LocalDate fecha) {
        Proveedor proveedor = proveedorService.buscarPorRuc(rucProveedor)
                .orElseThrow(() -> new NoSuchElementException("No existe un proveedor con RUC " + rucProveedor));

        Compra compra = new Compra();
        compra.setId(contadorId.incrementAndGet());
        compra.setProveedorId(proveedor.getId());
        compra.setNumeroComprobante(numeroComprobante);
        compra.setMontoSinIgv(montoSinIgv);
        compra.setFecha(fecha);

        compras.add(compra);
        return compra;
    }

    public List<Compra> listarTodas() {
        return compras;
    }

    public List<Compra> listarPorProveedor(Long proveedorId) {
        return compras.stream()
                .filter(c -> c.getProveedorId().equals(proveedorId))
                .toList();
    }

    public Optional<Compra> buscarPorId(Long id) {
        return compras.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    public Compra actualizar(Long id, String rucProveedor, String numeroComprobante, Double montoSinIgv, LocalDate fecha) {
        Compra compra = buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe una compra con id " + id));

        Proveedor proveedor = proveedorService.buscarPorRuc(rucProveedor)
                .orElseThrow(() -> new NoSuchElementException("No existe un proveedor con RUC " + rucProveedor));

        compra.setProveedorId(proveedor.getId());
        compra.setNumeroComprobante(numeroComprobante);
        compra.setMontoSinIgv(montoSinIgv);
        compra.setFecha(fecha);
        return compra;
    }

    public void eliminar(Long id) {
        Compra compra = buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe una compra con id " + id));
        compras.remove(compra);
    }

    public List<Compra> listarPorPeriodo(LocalDate desde, LocalDate hasta) {
        return compras.stream()
                .filter(c -> !c.getFecha().isBefore(desde) && !c.getFecha().isAfter(hasta))
                .toList();
    }

    public double calcularTotalIgvPeriodo(LocalDate desde, LocalDate hasta) {
        return listarPorPeriodo(desde, hasta).stream()
                .mapToDouble(Compra::getIgv)
                .sum();
    }
}
