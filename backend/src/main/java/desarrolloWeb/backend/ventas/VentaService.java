package desarrolloWeb.backend.ventas;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VentaService {

    private final ClienteService clienteService;
    private final List<Venta> ventas = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(0);

    public VentaService(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public Venta registrar(String documentoCliente, String numeroComprobante, Double montoSinIgv, LocalDate fecha) {
        Cliente cliente = clienteService.buscarPorDocumento(documentoCliente)
                .orElseThrow(() -> new NoSuchElementException("No existe un cliente con documento " + documentoCliente));

        Venta venta = new Venta();
        venta.setId(contadorId.incrementAndGet());
        venta.setClienteId(cliente.getId());
        venta.setNumeroComprobante(numeroComprobante);
        venta.setMontoSinIgv(montoSinIgv);
        venta.setFecha(fecha);

        ventas.add(venta);
        return venta;
    }

    public List<Venta> listarTodas() {
        return ventas;
    }

    public List<Venta> listarPorCliente(Long clienteId) {
        return ventas.stream()
                .filter(v -> v.getClienteId().equals(clienteId))
                .toList();
    }

    public List<Venta> listarPorPeriodo(LocalDate desde, LocalDate hasta) {
        return ventas.stream()
                .filter(v -> !v.getFecha().isBefore(desde) && !v.getFecha().isAfter(hasta))
                .toList();
    }

    public double calcularTotalIgvPeriodo(LocalDate desde, LocalDate hasta) {
        return listarPorPeriodo(desde, hasta).stream()
                .mapToDouble(Venta::getIgv)
                .sum();
    }
}
